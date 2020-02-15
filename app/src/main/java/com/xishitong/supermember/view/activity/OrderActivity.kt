package com.xishitong.supermember.view.activity

import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.gson.Gson
import com.gyf.immersionbar.ImmersionBar
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import com.trello.rxlifecycle2.android.ActivityEvent
import com.xishitong.supermember.R
import com.xishitong.supermember.adapter.OrderAdapter
import com.xishitong.supermember.base.BaseActivity
import com.xishitong.supermember.base.BaseModel
import com.xishitong.supermember.bean.OrderBean
import com.xishitong.supermember.bean.UploadImgBean
import com.xishitong.supermember.network.BaseObserver
import com.xishitong.supermember.network.IApiService
import com.xishitong.supermember.network.NetClient
import com.xishitong.supermember.storage.ConfigPreferences
import com.xishitong.supermember.util.GlideEngine
import com.xishitong.supermember.util.ToastUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_processing_order.*
import kotlinx.android.synthetic.main.common_toolbar.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

/**
 * 订单Activity
 */
class OrderActivity : BaseActivity(), View.OnClickListener, OnRefreshListener,
    BaseQuickAdapter.OnItemClickListener, BaseQuickAdapter.OnItemChildClickListener {
    private var type = "0"
    private var orderAdapter: OrderAdapter? = null
    private var listData: MutableList<OrderBean.DataBean.ListBean> = mutableListOf()
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
        v_state_bar.setBackgroundColor(Color.WHITE)
        ImmersionBar.with(this)
            .statusBarDarkFont(true)
            .init()
        rl_toobar.setBackgroundColor(Color.WHITE)
        fl_back.visibility = View.VISIBLE
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
        tv_title.setTextColor(resources.getColor(R.color.color_333333))
        fl_back.setOnClickListener(this)
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
        smart_refresh.setOnRefreshListener(this)
        smart_refresh.setEnableLoadMore(false)
        smart_refresh.autoRefresh()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fl_back -> {
                finish()
            }
        }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        orderAdapter?.isUseEmpty(true)
        val hashMap = HashMap<String, Any>()
        hashMap["token"] = ConfigPreferences.instance.getToken()
        hashMap["status"] = type
        hashMap["page"] = "1"
        hashMap["limit"] = "20"
        NetClient.getInstance()
            .create(IApiService::class.java)
            .getOrderList(RequestBody.create("application/json".toMediaTypeOrNull(), Gson().toJson(hashMap)))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(bindUntilEvent(ActivityEvent.DESTROY))
            .subscribe(object : BaseObserver<OrderBean>() {
                override fun onSuccess(t: OrderBean?) {
                    smart_refresh.finishRefresh()
                    t?.data?.let {
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

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
    }

    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        when (view?.id) {
            R.id.tv_fail_reason -> {//失败原因
                ToastUtils.showToast("失败原因")

            }
            R.id.tv_voucher -> {//凭证
                when ((view as TextView).text) {
                    "上传凭证" -> {
                        //直接上传凭证
                        uploadVoucher("${listData[position].id}")
                    }
                    "查看/修改凭证", "查看凭证" -> {
                        val urlList = listData[position].urlList
                        if (urlList == null || urlList.size == 0) {
                            ToastUtils.showToast("无凭证")
                        } else {
                            val type = if (listData[position].processStatus == "cw") {
                                0
                            } else {
                                1
                            }
                            val intent = Intent(this, CheckVoucherActivity::class.java)
                            intent.putExtra("id", "${listData[position].id}")
                            intent.putExtra("type", type)
                            startActivity(intent)

                        }
                    }
                }
            }
            R.id.tv_courier_number -> {//快递单号
                ToastUtils.showToast("快递单号")
            }
            R.id.tv_receice_address -> {//收货地址
                ToastUtils.showToast("收货地址")
            }
            R.id.tv_order_qrcode -> {//订单二维码
                ToastUtils.showToast("订单二维码")
            }
        }
    }

    private fun uploadVoucher(id: String) {
        PictureSelector.create(this)
            .openGallery(PictureMimeType.ofImage())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
    //                    .theme()//主题样式(不设置为默认样式) 也可参考demo values/styles下 例如：R.style.picture.white.style
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
                val compressPath = result[0].compressPath
                val file = File(compressPath)

//                //上传oss
//                val map = HashMap<String, RequestBody>()
//                val path =
//                    RequestBody.create("application/json".toMediaTypeOrNull(), "xstvip")
//                map["path"] = path
//                val file = File(result[0].compressPath)
//                val body = RequestBody.create("multiart/form-data".toMediaTypeOrNull(), file)
//                val formData = MultipartBody.Part.createFormData("file", file.name, body)
//
//                NetClient.getInstance()
//                    .create(IApiService::class.java)
//                    .uploadImg(map, formData)
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .compose(bindUntilEvent(ActivityEvent.DESTROY))
//                    .subscribe(object : BaseObserver<UploadImgBean>() {
//                        override fun onSuccess(t: UploadImgBean?) {
//                            t?.data?.let {
//                                val hashMap = HashMap<String, String>()
//                                hashMap["token"] = ConfigPreferences.instance.getToken()
//                                hashMap["id"] = id
//                                hashMap["url"] = t.data.webUrl
//                                NetClient.getInstance()
//                                    .create(IApiService::class.java)
//                                    .uploadVoucher(
//                                        RequestBody.create(
//                                            "application/json".toMediaTypeOrNull(),
//                                            Gson().toJson(hashMap)
//                                        )
//                                    )
//                                    .subscribeOn(Schedulers.io())
//                                    .observeOn(AndroidSchedulers.mainThread())
//                                    .compose(bindUntilEvent(ActivityEvent.DESTROY))
//                                    .subscribe(object : BaseObserver<BaseModel>() {
//                                        override fun onSuccess(t: BaseModel?) {
//                                            ToastUtils.showToast("上传成功")
//                                        }
//
//                                        override fun onError(msg: String?) {
//                                            ToastUtils.showToast(msg)
//                                        }
//                                    })
//                            }
//                        }
//
//                        override fun onError(msg: String?) {
//                            ToastUtils.showToast(msg)
//                        }
//                    })
            }
    }
}
