package com.xishitong.supermember.view.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.ConfigurationInfo
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.gyf.immersionbar.ImmersionBar
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener
import com.luck.picture.lib.style.PictureParameterStyle
import com.tbruyelle.rxpermissions2.RxPermissions
import com.trello.rxlifecycle2.android.ActivityEvent
import com.xishitong.supermember.R
import com.xishitong.supermember.base.App
import com.xishitong.supermember.base.BaseActivity
import com.xishitong.supermember.base.BaseModel
import com.xishitong.supermember.bean.CheckVoucherBean
import com.xishitong.supermember.bean.UploadImgBean
import com.xishitong.supermember.network.BaseObserver
import com.xishitong.supermember.network.IApiService
import com.xishitong.supermember.network.NetClient
import com.xishitong.supermember.storage.ConfigPreferences
import com.xishitong.supermember.util.*
import com.xishitong.supermember.view.fragment.NetViewHolder
import com.xishitong.supermember.widget.FigureIndicatorView
import com.zhpan.bannerview.BannerViewPager
import com.zhpan.bannerview.constants.TransformerStyle
import com.zhpan.bannerview.holder.ViewHolder
import com.zhpan.bannerview.utils.BannerUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_check_voucher.*
import kotlinx.android.synthetic.main.common_toolbar.*
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

/**
 * 查看凭证
 */
class CheckVoucherActivity : BaseActivity(), View.OnClickListener {

    private var mBannerViewPager: BannerViewPager<String, VoucherViewHolder>? = null
    private var id = ""
    private var type = 0
    private var choosePicImageDialog: DialogUtils? = null

    override fun setContentView(): Int {
        return R.layout.activity_check_voucher
    }

    override fun init(savedInstanceState: Bundle?) {
        initToolBar()
        initBannerView()
    }

    private fun initToolBar() {
        v_state_bar.setBackgroundColor(Color.WHITE)
        ImmersionBar.with(this)
            .statusBarDarkFont(true)
            .init()
        rl_toobar.setBackgroundColor(Color.WHITE)
        fl_back.visibility = View.VISIBLE
        tv_title.text = getString(R.string.check_voucher)
        tv_title.setTextColor(resources.getColor(R.color.color_333333))
        fl_back.setOnClickListener(this)

        btn_submit_voucher.setOnClickListener(this)
        id = intent.getStringExtra("id")
        type = intent.getIntExtra("type", 0)
        if (type == 1) {
            btn_submit_voucher.visibility = View.GONE
        }
    }

    private fun initBannerView() {
        mBannerViewPager = findViewById(R.id.banner)
        showLoading()
        val hashMap = HashMap<String, String>()
        hashMap["token"] = ConfigPreferences.instance.getToken()
        hashMap["id"] = intent.getStringExtra("id")
        NetClient.getInstance()
            .create(IApiService::class.java)
            .checkVoucher(RequestBody.create("application/json".toMediaTypeOrNull(), Gson().toJson(hashMap)))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(bindUntilEvent(ActivityEvent.DESTROY))
            .subscribe(object : BaseObserver<CheckVoucherBean>() {
                override fun onSuccess(t: CheckVoucherBean?) {
                    hideLoading()
                    t?.data?.img?.let { dataBean ->
                        val split = dataBean.split(",")
                        mBannerViewPager!!.setIndicatorVisibility(View.VISIBLE)
                            .setIndicatorView(figure_indicator_view)
                            .setIndicatorColor(Color.GRAY, Color.WHITE)
                            .setCanLoop(true)
                            .setAutoPlay(false)
                            .setPageTransformerStyle(TransformerStyle.DEPTH)
                            .setScrollDuration(1000)
                            .setHolderCreator { VoucherViewHolder() }
                            .create(split)
                    }
                }

                override fun onError(msg: String?) {
                    hideLoading()
                    ToastUtils.showToast(msg)
                }
            })


    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fl_back -> {
                finish()
            }
            R.id.btn_submit_voucher -> {
                val builder = DialogUtils.Builder(this)
                choosePicImageDialog = builder.view(R.layout.dialog_selectsex)
                    .cancelable(true)
                    .gravity(Gravity.BOTTOM)
                    .cancelTouchout(false)
                    .style(R.style.Dialog)
                    .addViewOnclick(R.id.tv_man) {
                        selectPicFromCamera(id)
                        choosePicImageDialog?.dismiss()
                    }
                    .addViewOnclick(R.id.tv_women) {
                        selectPicFromAlbum(id)
                        choosePicImageDialog?.dismiss()
                    }
                    .addViewOnclick(R.id.tv_cancle) { choosePicImageDialog?.dismiss() }
                    .build()
                choosePicImageDialog!!.show()
            }
        }
    }

    private fun selectPicFromCamera(id: String) {
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
                            uploadImage(result, id)
                        }
                }
            }
            .subscribe()
    }

    private fun selectPicFromAlbum(id: String) {
        //选择图片
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
                //                        result?.forEachIndexed { index, localMedia ->
                uploadImage(result, id)
                //                        }
            }
    }

    private fun uploadImage(result: List<LocalMedia>, id: String) {
        //上传oss
        val map = HashMap<String, RequestBody>()
        val path =
            RequestBody.create("application/json".toMediaTypeOrNull(), "xstvip")
        map["path"] = path
        val file = File(result[0].compressPath)
        val body = RequestBody.create("multiart/form-data".toMediaTypeOrNull(), file)
        val formData = MultipartBody.Part.createFormData("file", file.name, body)

        showLoading()
        NetClient.getInstance()
            .create(IApiService::class.java)
            .uploadImg(map, formData)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(bindUntilEvent(ActivityEvent.DESTROY))
            .subscribe(object : BaseObserver<UploadImgBean>() {
                override fun onSuccess(t: UploadImgBean?) {
                    if (t?.data != null) {
                        val hashMap = HashMap<String, String>()
                        hashMap["token"] = ConfigPreferences.instance.getToken()
                        hashMap["id"] = id
                        hashMap["url"] = t.data.webUrl
                        NetClient.getInstance()
                            .create(IApiService::class.java)
                            .editVoucher(
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
                                    ToastUtils.showToast("上传成功")
                                    finish()
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

class VoucherViewHolder : ViewHolder<String> {
    private var imageView: ImageView? = null
    override fun createView(viewGroup: ViewGroup?, context: Context?, position: Int): View {
        val view = LayoutInflater.from(context).inflate(R.layout.item_ad_banner, viewGroup, false)
        imageView = view.findViewById(R.id.iv_ad_banner)
        return view
    }

    override fun onBind(context: Context?, data: String?, position: Int, size: Int) {
        Glide.with(context!!)
            .load(data!!)
            .into(imageView!!)
    }
}
