package com.xishitong.supermember.base

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import com.trello.rxlifecycle2.android.ActivityEvent
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import com.xishitong.supermember.R
import com.xishitong.supermember.event.LogoutEvent
import com.xishitong.supermember.storage.ConfigPreferences
import com.xishitong.supermember.util.*
import com.xishitong.supermember.view.activity.LoginActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File

/**
 *  author : zhangbao
 *  date : 2020-02-07 11:19
 *  description :
 */
abstract class BaseActivity : RxAppCompatActivity() {
    companion object {
        val TAG: String = this.javaClass.simpleName
    }

    var mAppManager: AppManager? = null
    var mCourierNumberDialog: DialogUtils? = null
    var mReceiveAddrDialog: DialogUtils? = null
    var mQrCodeDialog: DialogUtils? = null
    private var mSyncEncodeQRCode: Bitmap? = null
    protected var mLoadingView:LoadingViewUtils? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 将当前Activity加入栈管理
        mAppManager = AppManager.SINGLETON
        mAppManager?.addActivity(this)
        if (!isTaskRoot) {
            val intent = intent
            val action = intent.action
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && action != null && action == Intent.ACTION_MAIN) {
                mAppManager?.finishActivity(this)
                return
            }
        }
        setContentView(View.inflate(this, setContentView(), null))
        mLoadingView = LoadingViewUtils(this)
        init(savedInstanceState)
        EventBus.getDefault().register(this)
    }


    abstract fun setContentView(): Int

    abstract fun init(savedInstanceState: Bundle?)

    @Subscribe(threadMode = ThreadMode.POSTING)
    fun onLogoutEvent(logoutEvent: LogoutEvent) {
        ConfigPreferences.instance.setLoginState(false)
        ConfigPreferences.instance.setToken("")
        ConfigPreferences.instance.setIsMember(false)
        ConfigPreferences.instance.setPhone("")
        startActivity(Intent(this, LoginActivity::class.java))
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        dismissDialog()
    }

    private fun dismissDialog() {
        mCourierNumberDialog?.let {
            if (mCourierNumberDialog!!.isShowing) {
                mCourierNumberDialog!!.dismiss()
            }
        }
        mQrCodeDialog?.let {
            if (mQrCodeDialog!!.isShowing) {
                mQrCodeDialog!!.dismiss()
            }
        }
        mReceiveAddrDialog?.let {
            if (mReceiveAddrDialog!!.isShowing) {
                mReceiveAddrDialog!!.dismiss()
            }
        }
        mLoadingView?.let {
            mLoadingView!!.dismiss()
        }
    }

    protected fun showLoading() {
        mLoadingView?.show()
    }

    protected fun hideLoading() {
        mLoadingView?.dismiss()
    }

    protected fun showReceiveAddrDialog(name:String,phone:String,address:String) {
        val builder = DialogUtils.Builder(this)
        mReceiveAddrDialog = builder.view(R.layout.dialog_receive_addr)
            .cancelable(true)
            .gravity(Gravity.CENTER)
            .cancelTouchout(true)
            .settext(name, R.id.tv_name)
            .settext("(${phone})", R.id.tv_phone)
            .settext(address, R.id.tv_address)
            .style(R.style.Dialog)
            .build()
        mReceiveAddrDialog!!.show()
    }

    //快递单号dialog
    protected fun showCourierNumberDialog(courierNumber: String) {
        val builder = DialogUtils.Builder(this)
        mCourierNumberDialog = builder.view(R.layout.dialog_courier_number)
            .cancelable(true)
            .gravity(Gravity.CENTER)
            .cancelTouchout(true)
            .settext(courierNumber, R.id.tv_number)
            .addViewOnclick(R.id.tv_copy) {
                //获取剪贴板管理器：
                val cm: ClipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                // 创建普通字符型ClipData
                val mClipData = ClipData.newPlainText("Label", courierNumber)
                // 将ClipData内容放到系统剪贴板里。
                cm.primaryClip = mClipData
                ToastUtils.showToast("复制成功")
                mCourierNumberDialog?.dismiss()
            }
            .style(R.style.Dialog)
            .build()
        mCourierNumberDialog!!.show()
    }

    protected fun showQrCodeDialog(code: String) {
        val builder = DialogUtils.Builder(this)
        mCourierNumberDialog = builder.view(R.layout.dialog_qrcode)
            .cancelable(true)
            .gravity(Gravity.CENTER)
            .cancelTouchout(true)
            .style(R.style.Dialog)
            .build()
        val ivQrcode: ImageView = builder.childView.findViewById(R.id.iv_qrcode)
        mCourierNumberDialog!!.show()
        val filePath = "${getFileRoot(App.getInstance())}${File.separator}qr_${System.currentTimeMillis()}.jpg"
        Observable.create<Bitmap?> {
            val createQRImage = QRCodeUtil.createQRImage(code, UiUtils.dip2px(this, 280.0f),
                UiUtils.dip2px(this, 280.0f), null, filePath)
            if (createQRImage) {
                mSyncEncodeQRCode = BitmapFactory.decodeFile(filePath)
                it.onNext(mSyncEncodeQRCode.let { bitmap -> bitmap }!!)
                it.onComplete()
            }
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(bindUntilEvent(ActivityEvent.DESTROY))
            .doOnNext {
                try {
                    ivQrcode.setImageBitmap(mSyncEncodeQRCode)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            .observeOn(Schedulers.io())
            .doOnComplete {
                try {
                    FileUtils.deleteFile(filePath)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            .subscribe()
    }

    //文件存储根目录
    private fun getFileRoot( context: Context):String {
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            val external = context.applicationContext.getExternalFilesDir(null)
            if (external != null) {
                return external.absolutePath
            }
        }
        return context.filesDir.absolutePath
    }
}