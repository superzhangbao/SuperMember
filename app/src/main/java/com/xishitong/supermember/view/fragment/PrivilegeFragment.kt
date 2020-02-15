package com.xishitong.supermember.view.fragment

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.SystemClock
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.trello.rxlifecycle2.android.FragmentEvent
import com.xishitong.supermember.R
import com.xishitong.supermember.adapter.DetailOfMembershipAdapter
import com.xishitong.supermember.adapter.FlashSaleAdapter
import com.xishitong.supermember.base.APPLY_FOR_MEMBERSHIP
import com.xishitong.supermember.base.App
import com.xishitong.supermember.base.BaseFragment
import com.xishitong.supermember.bean.BannerBean
import com.xishitong.supermember.bean.BannerDataBean
import com.xishitong.supermember.bean.CommonBean
import com.xishitong.supermember.bean.SaleBean
import com.xishitong.supermember.event.WebEvent
import com.xishitong.supermember.network.BaseObserver
import com.xishitong.supermember.network.IApiService
import com.xishitong.supermember.network.NetClient
import com.xishitong.supermember.storage.ConfigPreferences
import com.xishitong.supermember.util.DateUtil
import com.xishitong.supermember.util.LogUtil
import com.xishitong.supermember.util.ToastUtils
import com.xishitong.supermember.util.UiUtils
import com.xishitong.supermember.view.activity.CheckVoucherActivity
import com.xishitong.supermember.view.activity.CommonWebActivity
import com.xishitong.supermember.view.activity.MainActivity
import com.xishitong.supermember.view.activity.SearchActivity
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
import org.greenrobot.eventbus.EventBus
import java.net.URLEncoder


/**
 *  author : zhangbao
 *  date : 2020-02-07 17:59
 *  description :特权fragment
 */
class PrivilegeFragment : BaseFragment(), View.OnClickListener, ViewPager.OnPageChangeListener,
    BaseQuickAdapter.OnItemClickListener {

    private var mBannerViewPager: BannerViewPager<BannerBean.DataBean, NetViewHolder>? = null
    private var bannerData: List<BannerBean.DataBean> = ArrayList()
    private val fragments: ArrayList<Fragment> = ArrayList()
    private var startTimeList = listOf("00:00:00", "10:00:00", "14:00:00", "18:00:00", "20:00:00", "22:00:00")
    private var endTimeList = listOf("09:59:59", "13:59:59", "17:59:59", "19:59:59", "21:59:59", "23:59:59")

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
    }

    override fun initData() {
        initAdBanner()
        initFlashSale()
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
            .subscribe(object : BaseObserver<BannerBean>(), BannerViewPager.OnPageClickListener {
                override fun onSuccess(t: BannerBean?) {
                    t?.data?.let { data ->
                        bannerData = data
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
                            .setOnPageClickListener(this)
                            .setHolderCreator { NetViewHolder() }
                            .create(data)
                    }
                }

                override fun onError(msg: String?) {
                    ToastUtils.showToast(msg)
                }

                override fun onPageClick(position: Int) {
                    //banner点击事件处理
                    val enName = bannerData[position].enName
                    val pName = bannerData[position].parentName
                    if (enName == "creditCard") {
                        EventBus.getDefault().postSticky(
                            WebEvent("http://web.yunjuhe.vip/credit/list/v1.0/500696", "信用卡", null)
                        )
                        val intent = Intent(activity, CommonWebActivity::class.java)
                        startActivity(intent)
                        return
                    }
                    if (enName == "temai") {
                        //跳转到特卖tab
                        (activity as MainActivity).selectNavigationItem()
                        return
                    }
                    if (enName == "chezhubang") {
                        EventBus.getDefault().postSticky(
                            WebEvent("https://st.czb365.com/v3_prod/?pft=92656476", "车租帮", null)
                        )
                        val intent = Intent(activity, CommonWebActivity::class.java)
                        startActivity(intent)
                        return
                    }
                    val url =
                        "http://www.seniornet.cn/js/sjh5test/pages/recharge/recharge2?pname=${pName}&enName=${enName}"
                    EventBus.getDefault().postSticky(
                        WebEvent(url, "车租帮", null)
                    )
                    val intent = Intent(activity, CommonWebActivity::class.java)
                    startActivity(intent)
                }
            })
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

        //计算参数
        val nowTime = DateUtil.getYearTime(System.currentTimeMillis())
        val split1 = nowTime.split(" ")
        val split = split1[1].split(":")

        var starTime = ""
        var endTime = ""
        try {
            when (split[0].toInt()) {
                in 0..10 -> {
                    starTime = "${split1[0]} " + startTimeList[0]
                    endTime = "${split1[0]} " + endTimeList[0]
                }
                in 10..14 -> {
                    starTime = "${split1[0]} " + startTimeList[1]
                    endTime = "${split1[0]} " + endTimeList[1]

                }
                in 14..18 -> {
                    starTime = "${split1[0]} " + startTimeList[2]
                    endTime = "${split1[0]} " + endTimeList[2]
                }
                in 18..20 -> {
                    starTime = "${split1[0]} " + startTimeList[3]
                    endTime = "${split1[0]} " + endTimeList[3]
                }
                in 20..22 -> {
                    starTime = "${split1[0]} " + startTimeList[4]
                    endTime = "${split1[0]} " + endTimeList[4]
                }
            }
        } catch (e: Exception) {
        }

        val hashMap = HashMap<String, Any>()
        hashMap["token"] = ConfigPreferences.instance.getToken()
        hashMap["productName"] = ""
        hashMap["startTime"] = starTime
        hashMap["endTime"] = endTime
        LogUtil.e(TAG,starTime)
        LogUtil.e(TAG,endTime)
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
                    t?.data?.let {
                        flashSaleAdapter.setNewData(it.list)
                    }
                }

                override fun onError(msg: String?) {
                    flashSaleAdapter.setNewData(null)
                }
            })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.search -> {
                startActivity(Intent(activity, SearchActivity::class.java))
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
        val data = adapter?.data as MutableList<SaleBean.DataBean.ListBean>?
        val encode = URLEncoder.encode(Gson().toJson(data?.get(position)))
        val url = "http://www.seniornet.cn/js/sjh5test/pages/temaidetail/temaidetail?good=$encode"
        EventBus.getDefault().postSticky(WebEvent(url, "限时秒杀", null))
        val intent = Intent(activity, CommonWebActivity::class.java)
        startActivity(intent)
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