package com.xishitong.supermember.view.fragment

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.gson.Gson
import com.trello.rxlifecycle2.android.FragmentEvent
import com.xishitong.supermember.R
import com.xishitong.supermember.adapter.DetailOfMembershipAdapter
import com.xishitong.supermember.adapter.FlashSaleAdapter
import com.xishitong.supermember.base.App
import com.xishitong.supermember.base.BaseFragment
import com.xishitong.supermember.bean.BannerBean
import com.xishitong.supermember.bean.BannerDataBean
import com.xishitong.supermember.bean.CommonBean
import com.xishitong.supermember.bean.SaleBean
import com.xishitong.supermember.network.BaseObserver
import com.xishitong.supermember.network.IApiService
import com.xishitong.supermember.network.NetClient
import com.xishitong.supermember.storage.ConfigPreferences
import com.xishitong.supermember.util.LogUtil
import com.xishitong.supermember.util.ToastUtils
import com.xishitong.supermember.util.UiUtils
import com.xishitong.supermember.view.activity.CheckVoucherActivity
import com.xishitong.supermember.widget.OvalIndicatorView
import com.zhpan.bannerview.BannerViewPager
import com.zhpan.bannerview.constants.TransformerStyle
import com.zhpan.bannerview.holder.ViewHolder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_privilege.*
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody


/**
 *  author : zhangbao
 *  date : 2020-02-07 17:59
 *  description :特权fragment
 */
class PrivilegeFragment : BaseFragment(), View.OnClickListener, ViewPager.OnPageChangeListener,
    BaseQuickAdapter.OnItemClickListener {
    private var mBannerViewPager: BannerViewPager<BannerBean.DataBean, NetViewHolder>? = null

    private val fragments: ArrayList<Fragment> = ArrayList()
    override fun setContentView(): Int {
        return R.layout.fragment_privilege
    }

    override fun initView(view: View) {
        mBannerViewPager = view.findViewById(R.id.banner_view)

        search.setOnClickListener(this)
        tab1.setOnClickListener(this)
        tab2.setOnClickListener(this)
        tab3.setOnClickListener(this)
        tab4.setOnClickListener(this)

        fragments.add(RecommendFragment())
        fragments.add(RecommendFragment())
        fragments.add(RecommendFragment())
        fragments.add(RecommendFragment())

        view_pager.adapter = DetailOfMembershipAdapter(activity?.supportFragmentManager, fragments)
        view_pager.addOnPageChangeListener(this)
        initAdBanner()
        initFlashSale()
    }

    /**
     * 初始化秒杀数据
     */
    private fun initFlashSale() {
        recycler_view.layoutManager = GridLayoutManager(App.getInstance(), 3)
        val flashSaleAdapter = FlashSaleAdapter(null)
        flashSaleAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN)
        flashSaleAdapter.isFirstOnly(false)
        flashSaleAdapter.onItemClickListener = this
        flashSaleAdapter.bindToRecyclerView(recycler_view)

        val hashMap = HashMap<String, Any>()
        hashMap["token"] = ConfigPreferences.instance.getToken()
        hashMap["productName"] = "特卖"
        hashMap["startTime"] = ""
        hashMap["endTime"] = ""
        hashMap["status"] = "1"
        hashMap["page"] = "1"
        hashMap["limit"] = "6"
        NetClient.getInstance()
            .create(IApiService::class.java)
            .getSale(RequestBody.create("application/json".toMediaTypeOrNull(), Gson().toJson(hashMap)))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
            .subscribe(object : BaseObserver<SaleBean>() {
                override fun onSuccess(t: SaleBean?) {
                    val list = ArrayList<SaleBean.DataBean.ListBean>()
                    list.add(SaleBean.DataBean.ListBean(10, "", 300, 310, "", "", "", 1, null, listOf()))
                    list.add(SaleBean.DataBean.ListBean(20, "", 300, 320, "", "", "", 1, null, listOf()))
                    list.add(SaleBean.DataBean.ListBean(30, "", 300, 330, "", "", "", 1, null, listOf()))
                    list.add(SaleBean.DataBean.ListBean(40, "", 300, 340, "", "", "", 1, null, listOf()))
                    list.add(SaleBean.DataBean.ListBean(50, "", 300, 350, "", "", "", 1, null, listOf()))
                    list.add(SaleBean.DataBean.ListBean(60, "", 300, 360, "", "", "", 1, null, listOf()))
                    t?.data?.let {
                        it.list = list
                        flashSaleAdapter.setNewData(it.list)
                    }
                }

                override fun onError(msg: String?) {
                    flashSaleAdapter.setNewData(null)
                }
            })
    }

    /**
     * 初始化广告banner
     */
    private fun initAdBanner() {
        val hashMapOf = hashMapOf(Pair("status", "1"))
        NetClient.getInstance()
            .create(IApiService::class.java)
            .getBanner(RequestBody.Companion.create("application/json".toMediaTypeOrNull(), Gson().toJson(hashMapOf)))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(bindUntilEvent(FragmentEvent.DESTROY))
            .subscribe(object : BaseObserver<BannerBean>() {
                override fun onSuccess(t: BannerBean?) {
                    mBannerViewPager!!.setIndicatorVisibility(View.VISIBLE)
                        .setIndicatorView(OvalIndicatorView(App.getInstance()))
                        .setIndicatorColor(Color.GRAY, Color.WHITE)
                        .setInterval(3000)
                        .setCanLoop(true)
                        .setAutoPlay(true)
                        .setPageTransformerStyle(TransformerStyle.DEPTH)
                        .setIndicatorGap(UiUtils.dip2px(App.getInstance(), 3.0f))
                        .setIndicatorHeight(UiUtils.dip2px(App.getInstance(), 4.0f))
                        .setIndicatorWidth(
                            UiUtils.dip2px(App.getInstance(), 3.0f),
                            UiUtils.dip2px(App.getInstance(), 10.0f)
                        )
                        .setRoundCorner(UiUtils.dip2px(App.getInstance(), 7.0f))
                        .setScrollDuration(1000)
                        .setOnPageClickListener {
                            startActivity(Intent(activity,CheckVoucherActivity::class.java))
                        }
                        .setHolderCreator { NetViewHolder() }
                        .create(t?.data)
                }

                override fun onError(msg: String?) {
                    ToastUtils.showToast(msg)
                }
            })
    }

    override fun initData() {
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.search -> {
                ToastUtils.showToast("搜索")
            }
            R.id.tab1 -> {
                tv_tab1.background = resources.getDrawable(R.drawable.btn_login_bg)
                tv_tab1.setTextColor(resources.getColor(R.color.white))
                tv_tab2.background = null
                tv_tab3.background = null
                tv_tab4.background = null
                tv_tab2.setTextColor(resources.getColor(R.color.color_333333))
                tv_tab3.setTextColor(resources.getColor(R.color.color_333333))
                tv_tab4.setTextColor(resources.getColor(R.color.color_333333))
                view_pager.setCurrentItem(0, true)
            }
            R.id.tab2 -> {
                tv_tab2.background = resources.getDrawable(R.drawable.btn_login_bg)
                tv_tab2.setTextColor(resources.getColor(R.color.white))
                tv_tab1.background = null
                tv_tab3.background = null
                tv_tab4.background = null
                tv_tab1.setTextColor(resources.getColor(R.color.color_333333))
                tv_tab3.setTextColor(resources.getColor(R.color.color_333333))
                tv_tab4.setTextColor(resources.getColor(R.color.color_333333))
                view_pager.setCurrentItem(1, true)
            }
            R.id.tab3 -> {
                tv_tab3.background = resources.getDrawable(R.drawable.btn_login_bg)
                tv_tab3.setTextColor(resources.getColor(R.color.white))
                tv_tab1.background = null
                tv_tab2.background = null
                tv_tab4.background = null
                tv_tab1.setTextColor(resources.getColor(R.color.color_333333))
                tv_tab2.setTextColor(resources.getColor(R.color.color_333333))
                tv_tab4.setTextColor(resources.getColor(R.color.color_333333))
                view_pager.setCurrentItem(2, true)
            }
            R.id.tab4 -> {
                tv_tab4.background = resources.getDrawable(R.drawable.btn_login_bg)
                tv_tab4.setTextColor(resources.getColor(R.color.white))
                tv_tab1.background = null
                tv_tab2.background = null
                tv_tab3.background = null
                tv_tab1.setTextColor(resources.getColor(R.color.color_333333))
                tv_tab2.setTextColor(resources.getColor(R.color.color_333333))
                tv_tab3.setTextColor(resources.getColor(R.color.color_333333))
                view_pager.setCurrentItem(3, true)
            }
        }
    }

    override fun onPageScrollStateChanged(p0: Int) {

    }

    override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
    }

    override fun onPageSelected(p0: Int) {
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {

    }


}

class NetViewHolder : ViewHolder<BannerBean.DataBean> {
    private var imageView: ImageView? = null
    override fun createView(viewGroup: ViewGroup?, context: Context?, position: Int): View {
        val view = LayoutInflater.from(context).inflate(R.layout.item_ad_banner, viewGroup, false)
        imageView = view.findViewById(R.id.iv_ad_banner)
        return view
    }

    override fun onBind(context: Context?, data: BannerBean.DataBean?, position: Int, size: Int) {
        Glide.with(context!!)
            .load(data!!.photo)
            .into(imageView!!)
    }
}