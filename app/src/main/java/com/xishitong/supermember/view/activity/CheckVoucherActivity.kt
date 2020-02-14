package com.xishitong.supermember.view.activity

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.gyf.immersionbar.ImmersionBar
import com.xishitong.supermember.R
import com.xishitong.supermember.base.App
import com.xishitong.supermember.base.BaseActivity
import com.xishitong.supermember.util.ToastUtils
import com.xishitong.supermember.util.UiUtils
import com.xishitong.supermember.view.fragment.NetViewHolder
import com.xishitong.supermember.widget.FigureIndicatorView
import com.zhpan.bannerview.BannerViewPager
import com.zhpan.bannerview.constants.TransformerStyle
import com.zhpan.bannerview.holder.ViewHolder
import com.zhpan.bannerview.utils.BannerUtils
import kotlinx.android.synthetic.main.activity_check_voucher.*
import kotlinx.android.synthetic.main.common_toolbar.*

/**
 * 查看凭证
 */
class CheckVoucherActivity : BaseActivity(), View.OnClickListener {
    private var mBannerViewPager: BannerViewPager<String, VoucherViewHolder>? = null
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
        tv_title.text = getString(R.string.receiving_address)
        tv_title.setTextColor(resources.getColor(R.color.color_333333))
        fl_back.setOnClickListener(this)

        btn_submit_voucher.setOnClickListener(this)
    }

    private fun initBannerView() {
        mBannerViewPager = findViewById(R.id.banner)
        val list = ArrayList<String>()
        list.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1581624790013&di=0b27d1413429a18ff0bf09ade272d787&imgtype=0&src=http%3A%2F%2Fa2.att.hudong.com%2F08%2F72%2F01300000165476121273722687045.jpg")
        list.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1581624790013&di=281dc9966c32691ef582810285754371&imgtype=0&src=http%3A%2F%2Fe.hiphotos.baidu.com%2Fzhidao%2Fpic%2Fitem%2Fd62a6059252dd42a1c362a29033b5bb5c9eab870.jpg")
        list.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1581624790013&di=d0f093b586c414932fb9c9c848c89c67&imgtype=0&src=http%3A%2F%2Ffile02.16sucai.com%2Fd%2Ffile%2F2014%2F0617%2Fbe2f5973a60156df0c6aeb2aace791c6.jpg")

        mBannerViewPager!!.setIndicatorVisibility(View.VISIBLE)
            .setIndicatorView(figure_indicator_view)
            .setIndicatorColor(Color.GRAY, Color.WHITE)
            .setCanLoop(true)
            .setAutoPlay(false)
            .setPageTransformerStyle(TransformerStyle.DEPTH)
            .setScrollDuration(1000)
            .setOnPageClickListener { ToastUtils.showToast("点击了$it") }
            .setHolderCreator { VoucherViewHolder() }
            .create(list)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.fl_back->{
                finish()
            }
            R.id.btn_submit_voucher->{
                ToastUtils.showToast("传")
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
