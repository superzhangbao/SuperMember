package cn.cystal.app.view.activity

import android.Manifest
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import cn.cystal.app.R
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.gson.Gson
import com.gyf.immersionbar.ImmersionBar
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import com.tbruyelle.rxpermissions2.RxPermissions
import com.trello.rxlifecycle2.android.ActivityEvent
import cn.cystal.app.adapter.OrderAdapter
import cn.cystal.app.base.*
import cn.cystal.app.bean.OrderBean
import cn.cystal.app.bean.UploadImgBean
import cn.cystal.app.network.BaseObserver
import cn.cystal.app.network.IApiService
import cn.cystal.app.network.NetClient
import cn.cystal.app.storage.ConfigPreferences
import cn.cystal.app.util.DialogUtils
import cn.cystal.app.util.GlideEngine
import cn.cystal.app.util.LogUtil
import cn.cystal.app.util.ToastUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_apply_invoice.*
import kotlinx.android.synthetic.main.activity_processing_order.*
import kotlinx.android.synthetic.main.common_toolbar.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

/**
 * 订单Activity
 */
class OrderActivity : BaseActivity(), OnRefreshListener,
    BaseQuickAdapter.OnItemClickListener, BaseQuickAdapter.OnItemChildClickListener, OnLoadMoreListener {

    private var type = "0"
    private var orderAdapter: OrderAdapter? = null
    private var listData: MutableList<OrderBean.DataBean.ListBean> = mutableListOf()
    private var page = 1
    private var choosePicImageDialog: DialogUtils? = null

    override fun setContentView(): Int {
        return R.layout.activity_processing_order
    }

    override fun init(savedInstanceState: Bundle?) {
        type = intent.getStringExtra("type")
        initToolbar()
        initRecyclerView()
        initSmartRefresh()
    }

    private fun initToolbar() {
        ImmersionBar.with(this)
            .statusBarDarkFont(true)
            .init()
        tv_title.text = when (type) {
            "100" -> {
                getString(R.string.processing_order)
            }
            "200" -> {
                getString(R.string.completed_roder)
            }
            "300" -> {
                getString(R.string.fail_order)
            }
            "0" -> {
                getString(R.string.all_order)
            }
            else -> ""
        }
        tb_toolbar.title = ""
        setSupportActionBar(tb_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)//左侧添加一个默认的返回图标
        supportActionBar?.setHomeButtonEnabled(true) //设置返回键可用
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return true
    }

    private fun initRecyclerView() {
        recycler_view.layoutManager = LinearLayoutManager(this)
        orderAdapter = OrderAdapter(listData)
        orderAdapter!!.openLoadAnimation(BaseQuickAdapter.ALPHAIN)
        orderAdapter!!.isFirstOnly(false)
        orderAdapter!!.onItemClickListener = this
        orderAdapter!!.onItemChildClickListener = this
        orderAdapter?.emptyView = layoutInflater.inflate(R.layout.empty, recycler_view.parent as ViewGroup, false)
        orderAdapter?.bindToRecyclerView(recycler_view)
        orderAdapter?.isUseEmpty(false)
    }

    private fun initSmartRefresh() {
        smart_refresh.setEnableLoadMore(true)
        smart_refresh.setOnRefreshListener(this)
        smart_refresh.setOnLoadMoreListener(this)
        smart_refresh.autoRefresh()
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        orderAdapter?.isUseEmpty(true)
        page = 1
        val hashMap = HashMap<String, Any>()
        hashMap["token"] = ConfigPreferences.instance.getToken()
        hashMap["status"] = type
        hashMap["page"] = page
        hashMap["limit"] = LIMIT
        NetClient.getInstance()
            .create(IApiService::class.java)
            .getOrderList(RequestBody.create("application/json".toMediaTypeOrNull(), Gson().toJson(hashMap)))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(bindUntilEvent(ActivityEvent.DESTROY))
            .subscribe(object : BaseObserver<OrderBean>() {
                override fun onSuccess(t: OrderBean?) {
                    t?.data?.let {
                        it.list?.let { list ->
                            if (list.size < LIMIT) {
                                smart_refresh.finishRefreshWithNoMoreData()
                            } else {
                                smart_refresh.finishRefresh()
                                smart_refresh.setNoMoreData(false)
                            }
                        }
                        listData = it.list
                        orderAdapter!!.setNewData(listData)
                    }
                }

                override fun onError(msg: String?) {
                    smart_refresh.finishRefresh()
                    ToastUtils.showToast(msg)
                }
            })
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        orderAdapter?.isUseEmpty(true)
        page++
        val hashMap = HashMap<String, Any>()
        hashMap["token"] = ConfigPreferences.instance.getToken()
        hashMap["status"] = type
        hashMap["page"] = page
        hashMap["limit"] = LIMIT
        NetClient.getInstance()
            .create(IApiService::class.java)
            .getOrderList(RequestBody.create("application/json".toMediaTypeOrNull(), Gson().toJson(hashMap)))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(bindUntilEvent(ActivityEvent.DESTROY))
            .subscribe(object : BaseObserver<OrderBean>() {
                override fun onSuccess(t: OrderBean?) {
                    t?.data?.let {
                        it.list?.let { list ->
                            if (list.size < LIMIT) {
                                smart_refresh.finishLoadMoreWithNoMoreData()
                            } else {
                                smart_refresh.finishLoadMore()
                            }
                            listData.addAll(list)
                            orderAdapter!!.setNewData(listData)
                        }

                    }
                }

                override fun onError(msg: String?) {
                    smart_refresh.finishLoadMore()
                    ToastUtils.showToast(msg)
                }
            })
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
    }

    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        val listBean = listData[position]
        when (view?.id) {
            R.id.tv_fail_reason -> {//失败原因
                showErrorReasonDialog(listBean.reason)
            }
            R.id.tv_voucher -> {//凭证
                when ((view as TextView).text) {
                    "上传凭证" -> {
                        //直接上传凭证
                        RxPermissions(this)
                            .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .doOnNext {
                                if (it) {
                                    uploadVoucher("${listBean.id}", listBean.billId)
                                }
                            }
                            .subscribe()
                    }
                    "查看/修改凭证", "查看凭证" -> {
                        val urlList = listBean.urlList
                        if (urlList == null || urlList.size == 0) {
                            ToastUtils.showToast("无凭证")
                        } else {
                            val type = if (listBean.processStatus == "cw") {
                                0
                            } else {
                                1
                            }
                            val intent = Intent(this, CheckVoucherActivity::class.java)
                            intent.putExtra("id", "${listBean.id}")
                            intent.putExtra("billId", listBean.billId)
                            intent.putExtra("type", type)
                            startActivityForResult(intent, REQUEST_CODE_EDIT)
                        }
                    }
                }
            }
            R.id.tv_courier_number -> {//快递单号
                showCourierNumberDialog(listBean.courierNumber)
            }
            R.id.tv_receice_address -> {//收货地址
                showReceiveAddrDialog(
                    listBean.addressName,
                    listBean.addressPhone,
                    listBean.addressGegion + listBean.addressDetailed
                )
            }
            R.id.tv_order_qrcode -> {//订单二维码
                val hashMap = HashMap<String, String>()
                hashMap["billId"] = listBean.billId
                hashMap["payId"] = listBean.payOrderId
                showQrCodeDialog(
                    "${listBean.name}/${listBean.userPhone}",
                    "${listBean.amount / 100.0}",
                    Gson().toJson(hashMap)
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null && resultCode == RESULT_CODE_EDIT) {
            if (requestCode == REQUEST_CODE_EDIT) {
                val orderNo = data.getStringExtra("orderNo")
                //提示开票
                showApplyInvoiceDialog(View.OnClickListener {
                    val intent = Intent(this@OrderActivity, ApplyInvoiceActivity::class.java)
                    intent.putExtra("orderNo", orderNo)
                    startActivity(intent)
                    mApplyInvoiceDialog?.dismiss()
                })
            }
        }
    }

    private fun uploadVoucher(id: String, billId: String) {
        val builder = DialogUtils.Builder(this)
        choosePicImageDialog = builder.view(R.layout.dialog_selectsex)
            .cancelable(true)
            .gravity(Gravity.BOTTOM)
            .cancelTouchout(false)
            .style(R.style.Dialog)
            .addViewOnclick(R.id.tv_man) {
                selectPicFromCamera(id, billId)
                choosePicImageDialog?.dismiss()
            }
            .addViewOnclick(R.id.tv_women) {
                selectPicFromAlbum(id, billId)
                choosePicImageDialog?.dismiss()
            }
            .addViewOnclick(R.id.tv_cancle) { choosePicImageDialog?.dismiss() }
            .build()
        choosePicImageDialog!!.show()
    }

    private fun selectPicFromCamera(id: String, billId: String) {
        RxPermissions(this)
            .request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
            .doOnNext {
                if (!it) {
                    ToastUtils.showToast("没有相机权限")
                } else {
                    PictureSelector.create(this)
                        .openCamera(PictureMimeType.ofImage())
                        .loadImageEngine(GlideEngine.createGlideEngine()) // 请参考Demo GlideEngine.java
                        .compress(true)
                        .forResult { result ->
                            //上传oss
                            uploadImage(result, id, billId)
                        }
                }
            }
            .subscribe()
    }

    private fun selectPicFromAlbum(id: String, billId: String) {
        PictureSelector.create(this)
            .openGallery(PictureMimeType.ofImage())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
            .loadImageEngine(GlideEngine.createGlideEngine())// 外部传入图片加载引擎，必传项   参考Demo MainActivity中代码
            .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)// 设置相册Activity方向，不设置默认使用系统
            .isWeChatStyle(true)// 是否开启微信图片选择风格，此开关开启了才可使用微信主题！！！
            .maxSelectNum(5)// 最大图片选择数量 int
            .imageSpanCount(4)// 每行显示个数 int
            .isReturnEmpty(false)// 未选择数据时点击按钮是否可以返回
            .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
            .previewImage(true)// 是否可预览图片 true or false
            .isCamera(true)// 是否显示拍照按钮 true or false
            .compress(true)// 是否压缩 true or false
            .isGif(false)// 是否显示gif图片 true or false
            .freeStyleCropEnabled(true)// 裁剪框是否可拖拽 true or false
            .circleDimmedLayer(false)// 是否圆形裁剪 true or false
            .openClickSound(false)// 是否开启点击声音 true or false
            .previewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
            .synOrAsy(true)//同步true或异步false 压缩 默认同步
            .scaleEnabled(true)// 裁剪是否可放大缩小图片 true or false
            .isDragFrame(true)// 是否可拖动裁剪框(固定)
            .forResult { result ->
                //上传oss
                uploadImage(result, id, billId)
            }
    }

    private fun uploadImage(result: MutableList<LocalMedia>, id: String, billId: String) {
        val map = HashMap<String, RequestBody>()
        val path =
            RequestBody.create("application/json".toMediaTypeOrNull(), "xstvip")
        map["path"] = path
        showLoading()
        val arrayList = ArrayList<MultipartBody.Part>()
        result.forEach {
            val file = File(it.compressPath)
            val body = RequestBody.create("multiart/form-data".toMediaTypeOrNull(), file)
            val formData = MultipartBody.Part.createFormData("file", file.name, body)
            arrayList.add(formData)
        }
        NetClient.getInstance()
            .create(IApiService::class.java)
            .uploadImg(map, arrayList)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(bindUntilEvent(ActivityEvent.DESTROY))
            .subscribe(object : BaseObserver<UploadImgBean>() {
                override fun onSuccess(t: UploadImgBean?) {
                    if (t?.data != null) {
                        val hashMap = HashMap<String, String>()
                        var sb = StringBuilder()
                        t.data.forEach {
                            sb = sb.append(it.webUrl).append(",")
                        }
                        val url = sb.toString().substring(0, sb.lastIndex)
                        LogUtil.e(TAG, "url:$url")
                        hashMap["token"] = ConfigPreferences.instance.getToken()
                        hashMap["id"] = id
                        hashMap["url"] = url
                        NetClient.getInstance()
                            .create(IApiService::class.java)
                            .uploadVoucher(
                                RequestBody.create(
                                    "application/json".toMediaTypeOrNull(),
                                    Gson().toJson(hashMap)
                                )
                            )
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .compose(bindUntilEvent(ActivityEvent.DESTROY))
                            .subscribe(object : BaseObserver<BaseModel>() {
                                override fun onSuccess(t: BaseModel?) {
                                    hideLoading()
                                    //刷新列表
                                    smart_refresh.autoRefresh()
                                    //提示开票
                                    showApplyInvoiceDialog(View.OnClickListener {
                                        val intent = Intent(this@OrderActivity, ApplyInvoiceActivity::class.java)
                                        intent.putExtra("orderNo", billId)
                                        startActivity(intent)
                                        mApplyInvoiceDialog?.dismiss()
                                    })
                                }

                                override fun onError(msg: String?) {
                                    hideLoading()
                                    ToastUtils.showToast(msg)
                                }
                            })
                    } else {
                        hideLoading()
                    }
                }

                override fun onError(msg: String?) {
                    hideLoading()
                    ToastUtils.showToast(msg)
                }
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        choosePicImageDialog?.let {
            if (choosePicImageDialog!!.isShowing) {
                choosePicImageDialog!!.dismiss()
            }
        }
    }
}
