package cn.cystal.app.view.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import cn.cystal.app.R
import cn.cystal.app.base.BaseActivity
import cn.cystal.app.base.PHONE_NUMBER
import cn.cystal.app.base.VIP_AGREEMENT
import cn.cystal.app.bean.VertifyCodeBean
import cn.cystal.app.event.WebEvent
import cn.cystal.app.network.BaseObserver
import cn.cystal.app.network.IApiService
import cn.cystal.app.network.NetClient
import cn.cystal.app.util.PhoneNumberUtils
import cn.cystal.app.util.ToastUtils
import com.google.gson.Gson
import com.gyf.immersionbar.ImmersionBar
import com.trello.rxlifecycle2.android.ActivityEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.common_toolbar.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.greenrobot.eventbus.EventBus

/**
 *  author : zhangbao
 *  date : 2020-02-08 12:59
 *  description : 登陆页面
 */
class LoginActivity : BaseActivity(), View.OnClickListener {

    override fun setContentView(): Int {
        return R.layout.activity_login
    }

    override fun init(savedInstanceState: Bundle?) {
        initTitle()
        tv_login_agreement.text = getClickableSpan()
        tv_login_agreement.movementMethod = LinkMovementMethod.getInstance()
        tv_login_agreement.highlightColor = Color.WHITE
        et_login.textAlignment = View.TEXT_ALIGNMENT_CENTER
        et_login.gravity = Gravity.CENTER
    }

    /**
     * 初始化状态栏
     */
    private fun initTitle() {
        ImmersionBar.with(this)
            .statusBarDarkFont(true)
            .init()
        tv_title.text = getString(R.string.login_title)
        btn_login.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_login->{
                //校验手机号码
                val phone = et_login.text.toString()
                if (phone.length!=11) ToastUtils.showToast("请输入手机号")
                if (!PhoneNumberUtils.isPhoneLegal(phone)) {
                    ToastUtils.showToast("请输入正确的手机号")
                    return
                }
                val hashMapOf = hashMapOf("phone" to phone)
                NetClient.getInstance()
                    .create(IApiService::class.java)
                    .getVertifyCode(RequestBody.Companion.create("application/json".toMediaTypeOrNull(), Gson().toJson(hashMapOf)))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .compose(bindUntilEvent(ActivityEvent.DESTROY))
                    .subscribe(object : BaseObserver<VertifyCodeBean>(){
                        override fun onSuccess(t: VertifyCodeBean?) {
                            //网络请求
                            val intent = Intent(this@LoginActivity,LoginVerificationCodeActivity::class.java)
                            intent.putExtra(PHONE_NUMBER,phone)
                            startActivity(intent)
                        }

                        override fun onError(msg: String?) {
                            ToastUtils.showToast(msg)
                        }
                    })
            }
        }
    }

    private fun getClickableSpan(): SpannableStringBuilder {
        return SpannableStringBuilder(getString(R.string.login_agreement)).also {
            it.setSpan(UnderlineSpan(), 15, 18, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            it.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    EventBus.getDefault().postSticky(WebEvent(VIP_AGREEMENT))
                    val intent = Intent(this@LoginActivity, CommonWebActivity::class.java)
                    startActivity(intent)
                }
            }, 15, 18, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            it.setSpan(
                ForegroundColorSpan(resources.getColor(R.color.color_6BB467)),
                15,
                18,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }
}
