package cn.cystal.app.view.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager.widget.ViewPager
import cn.cystal.app.R
import cn.cystal.app.adapter.DetailOfMembershipAdapter
import cn.cystal.app.adapter.FlashSaleAdapter
import cn.cystal.app.base.App
import cn.cystal.app.base.BaseFragment
import cn.cystal.app.base.LIMITED_SECKILL
import cn.cystal.app.bean.BannerBean
import cn.cystal.app.bean.SaleBean
import cn.cystal.app.event.WebEvent
import cn.cystal.app.network.BaseObserver
import cn.cystal.app.network.IApiService
import cn.cystal.app.network.NetClient
import cn.cystal.app.storage.ConfigPreferences
import cn.cystal.app.util.DateUtil
import cn.cystal.app.util.ToastUtils
import cn.cystal.app.util.UiUtils
import cn.cystal.app.view.activity.CommonWebActivity
import cn.cystal.app.view.activity.LoginActivity
import cn.cystal.app.view.activity.SearchActivity
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.gson.Gson
import com.trello.rxlifecycle2.android.FragmentEvent
import com.zhpan.bannerview.BannerViewPager
import com.zhpan.bannerview.constants.TransformerStyle
import com.zhpan.bannerview.holder.ViewHolder
import com.zhpan.indicator.enums.IndicatorSlideMode
import com.zhpan.indicator.enums.IndicatorStyle
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_privilege.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.greenrobot.eventbus.EventBus
import java.util.concurrent.TimeUnit


/**
 *  author : zhangbao
 *  date : 2020-02-07 17:59
 *  description :特权fragment
 */
class PrivilegeFragment : BaseFragment(), View.OnClickListener, ViewPager.OnPageChangeListener,
    BaseQuickAdapter.OnItemClickListener, BannerViewPager.OnPageClickListener {

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

        val recommendFragment1 = RecommendFragment()
        val bundle1 = Bundle()
        bundle1.putInt("type", 1)
        recommendFragment1.arguments = bundle1
        val recommendFragment2 = RecommendFragment()
        val bundle2 = Bundle()
        bundle2.putInt("type", 2)
        recommendFragment2.arguments = bundle2
        val recommendFragment3 = RecommendFragment()
        val bundle3 = Bundle()
        bundle3.putInt("type", 3)
        recommendFragment3.arguments = bundle3
        val recommendFragment4 = RecommendFragment()
        val bundle4 = Bundle()
        bundle4.putInt("type", 4)
        recommendFragment4.arguments = bundle4
        fragments.add(recommendFragment1)
        fragments.add(recommendFragment2)
        fragments.add(recommendFragment3)
        fragments.add(recommendFragment4)

        view_pager.adapter = DetailOfMembershipAdapter(
            activity?.supportFragmentManager,
            fragments
        )
        view_pager.addOnPageChangeListener(this)
    }

    override fun initData() {
        getAdBannerData()
        initFlashSale()
    }

    /**
     * 初始化广告banner
     */
    private fun getAdBannerData() {
        val hashMapOf = hashMapOf(Pair("status", "1"))
        NetClient.getInstance()
            .create(IApiService::class.java)
            .getBanner(RequestBody.Companion.create("application/json".toMediaTypeOrNull(), Gson().toJson(hashMapOf)))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(bindUntilEvent(FragmentEvent.DESTROY))
            .subscribe(object : BaseObserver<BannerBean>() {
                override fun onSuccess(t: BannerBean?) {
                    t?.data?.let { data ->
                        bannerData = data
                        initAdBanner(data)
                    }
                }

                override fun onError(msg: String?) {
                    ToastUtils.showToast(msg)
                }
            })
    }

    private fun initAdBanner(data: MutableList<BannerBean.DataBean>) {
        mBannerViewPager!!.setIndicatorVisibility(View.VISIBLE)
            .setIndicatorSliderColor(Color.GRAY, Color.WHITE)
            .setIndicatorStyle(IndicatorStyle.ROUND_RECT)
            .setInterval(3000)
            .setCanLoop(true)
            .setAutoPlay(true)
            .setPageTransformerStyle(TransformerStyle.DEPTH)
            .setIndicatorSliderGap(UiUtils.dip2px(App.getInstance(), 4.0f))
            .setIndicatorHeight(resources.getDimensionPixelOffset(R.dimen.dp_4))
            .setIndicatorSliderWidth(
                UiUtils.dip2px(App.getInstance(), 4.0f),
                UiUtils.dip2px(App.getInstance(), 10.0f)
            )
            .setIndicatorSlideMode(IndicatorSlideMode.NORMAL)
            .setRoundCorner(UiUtils.dip2px(App.getInstance(), 7.0f))
            .setScrollDuration(500)
            .setOnPageClickListener(this)
            .setHolderCreator { NetViewHolder() }
            .create(data)
    }

    override fun onPageClick(position: Int) {
        //banner点击事件处理
        bannerData[position].jumpUrl?.let { url ->
            EventBus.getDefault().postSticky(WebEvent(url))
            val intent = Intent(activity, CommonWebActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * 初始化秒杀数据
     */
    @SuppressLint("SetTextI18n")
    private fun initFlashSale() {
        recycler_view.layoutManager = GridLayoutManager(activity, 3)
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
                in 0 until 10 -> {
                    starTime = "${split1[0]} " + startTimeList[0]
                    endTime = "${split1[0]} " + endTimeList[0]
                }
                in 10 until 14 -> {
                    starTime = "${split1[0]} " + startTimeList[1]
                    endTime = "${split1[0]} " + endTimeList[1]
                }
                in 14 until 18 -> {
                    starTime = "${split1[0]} " + startTimeList[2]
                    endTime = "${split1[0]} " + endTimeList[2]
                }
                in 18 until 20 -> {
                    starTime = "${split1[0]} " + startTimeList[3]
                    endTime = "${split1[0]} " + endTimeList[3]
                }
                in 20 until 22 -> {
                    starTime = "${split1[0]} " + startTimeList[4]
                    endTime = "${split1[0]} " + endTimeList[4]
                }
                in 22 until 24 -> {
                    starTime = "${split1[0]} " + startTimeList[5]
                    endTime = "${split1[0]} " + endTimeList[5]
                }
            }
        } catch (e: Exception) {
        }

        val hashMap = HashMap<String, Any>()
        hashMap["token"] = ConfigPreferences.instance.getToken()
        hashMap["productName"] = ""
        hashMap["startTime"] = starTime
        hashMap["endTime"] = endTime
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
        //开始倒计时
        try {
            val endTimeStamp = DateUtil.dateToStamp(endTime)
            val currentTimeMillis = System.currentTimeMillis()
            Observable.interval(0, 1, TimeUnit.SECONDS)
                .take(endTimeStamp.minus(currentTimeMillis).div(1000))
                .map { (endTimeStamp.minus(currentTimeMillis).div(1000).minus(it).minus(1)) }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext {
                    val totalMin = it.div(60)
                    val hour = totalMin.div(60)
                    val mins = totalMin.rem(60)

                    tv_end_time_hour.text = "${if (hour < 10) {
                        "0${hour}"
                    } else {
                        "$hour"
                    }}:"
                    tv_end_time_min.text = "${if (mins < 10) {
                        "0${mins}"
                    } else {
                        "$mins"
                    }}:"

                    val sec = it.rem(60)
                    if (sec < 10) {
                        tv_end_time_sec.text = "0$sec"
                    } else {
                        tv_end_time_sec.text = sec.toString()
                    }
                }
                .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe()
        } catch (e: Exception) {
        }
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
        //判断是否登陆
        if (!ConfigPreferences.instance.getLoginState()) {
            startActivity(Intent(activity, LoginActivity::class.java))
            return
        }
        val data = adapter?.data as MutableList<SaleBean.DataBean.ListBean>?
        val id = data?.get(position)?.id
        val url = "$LIMITED_SECKILL$id"
        EventBus.getDefault().postSticky(WebEvent(url))
        val intent = Intent(activity, CommonWebActivity::class.java)
        startActivity(intent)
    }
}

class NetViewHolder : ViewHolder<BannerBean.DataBean> {
    private var imageView: ImageView? = null
    override fun getLayoutId(): Int {
        return R.layout.item_ad_banner
    }

    override fun onBind(itemView: View?, data: BannerBean.DataBean?, position: Int, size: Int) {
        imageView = itemView?.findViewById(R.id.iv_ad_banner)
        Glide.with(imageView!!)
            .load(data!!.photo)
            .into(imageView!!)
    }
}