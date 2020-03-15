package cn.cystal.app.view.fragment

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.cystal.app.R
import cn.cystal.app.adapter.CommonAdapter
import cn.cystal.app.base.App
import cn.cystal.app.base.BaseFragment
import cn.cystal.app.bean.BoutiqueSaleBean
import cn.cystal.app.event.RefreshDataEvent
import cn.cystal.app.event.WebEvent
import cn.cystal.app.network.BaseObserver
import cn.cystal.app.network.IApiService
import cn.cystal.app.network.NetClient
import cn.cystal.app.util.LogUtil
import cn.cystal.app.util.ToastUtils
import cn.cystal.app.util.UiUtils
import cn.cystal.app.view.activity.CommonWebActivity
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.gson.Gson
import com.trello.rxlifecycle2.android.FragmentEvent
import com.zhpan.bannerview.BannerViewPager
import com.zhpan.bannerview.holder.ViewHolder
import com.zhpan.indicator.enums.IndicatorStyle
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_recommend.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * author : zhangbao
 * date : 2020-02-11 16:42
 * description :精品推荐fragment
 */
class RecommendFragment : BaseFragment() {

    private var mHomeViewPager: BannerViewPager<MutableList<BoutiqueSaleBean.DataBean>, HomeViewHolder>? = null
    private var type = 1
    var data: MutableList<MutableList<BoutiqueSaleBean.DataBean>> = mutableListOf()
    private var firstResume = true
    private var isVisibleToUser = false

    override fun setContentView(): Int {
        return R.layout.fragment_recommend
    }

    override fun initView(view: View) {
        EventBus.getDefault().register(this)
        mHomeViewPager = view.findViewById(R.id.view_pager)
        arguments?.let { bundle ->
            type = bundle.getInt("type")
        }
    }

    override fun initData() {
        initViewPager()
        initViewPagerData()
    }

    override fun onResume() {
        super.onResume()
        LogUtil.e(TAG, "onResume:$type")
//        if (firstResume) {
//            firstResume = false
//        }else{
//            LogUtil.e(TAG, "onResume:$type")
//            initViewPagerData()
//        }
    }

//    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
//        super.setUserVisibleHint(isVisibleToUser)
//        this.isVisibleToUser = isVisibleToUser
//        if (isVisibleToUser) {
//            initViewPagerData()
//        }
//    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    fun onRefreshDataEvent(refreshDataEvent: RefreshDataEvent) {
        LogUtil.e(TAG, "onRefreshDataEvent")
//        if (isVisibleToUser) {
//            LogUtil.e(TAG, "onRefreshDataEvent && isVisibleToUser")
//            initViewPagerData()
//        }
    }

    private fun initViewPager() {
        mHomeViewPager!!.setIndicatorVisibility(View.VISIBLE)
            .setIndicatorStyle(IndicatorStyle.ROUND_RECT)
            .setIndicatorView(oval_indicator)
            .setIndicatorSliderColor(
                resources.getColor(R.color.color_88F86024),
                resources.getColor(R.color.color_F86024)
            )
            .setCanLoop(false)
            .setAutoPlay(false)
            .setIndicatorSliderGap(UiUtils.dip2px(App.getInstance(), 4.0f))
            .setIndicatorHeight(UiUtils.dip2px(App.getInstance(), 4.0f))
            .setIndicatorSliderWidth(
                UiUtils.dip2px(App.getInstance(), 4.0f),
                UiUtils.dip2px(App.getInstance(), 10.0f)
            )
            .setHolderCreator { HomeViewHolder() }
    }

    private fun initViewPagerData() {
        val hashMap = HashMap<String, Int>()
        hashMap["sort"] = type
        NetClient.getInstance()
            .create(IApiService::class.java)
            .getBoutiqueSale(RequestBody.create("application/json".toMediaTypeOrNull(), Gson().toJson(hashMap)))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
            .subscribe(object : BaseObserver<BoutiqueSaleBean>() {
                override fun onSuccess(t: BoutiqueSaleBean?) {
                    t?.data?.let { dataBean ->
                        val i = dataBean.size / 15
                        val i1 = dataBean.size % 15
                        data.clear()
                        if (i == 0) {
                            data.add(dataBean)
                        } else {
                            repeat(i) {
                                val subList = dataBean.subList(it * 15, it * 15 + (15))
                                data.add(subList)
                            }
                            if (i1 != 0) {
                                val subList = dataBean.subList(i * 15, dataBean.size)
                                data.add(subList)
                            }
                        }
                        mHomeViewPager?.create(data)
                    }
                }

                override fun onError(msg: String?) {
                    ToastUtils.showToast(msg)
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
    }
}

class HomeViewHolder : ViewHolder<MutableList<BoutiqueSaleBean.DataBean>>, BaseQuickAdapter.OnItemClickListener {
    private var recyclerView: RecyclerView? = null
    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        EventBus.getDefault()
            .postSticky(WebEvent((adapter?.data as MutableList<BoutiqueSaleBean.DataBean>)[position].url))
        val intent = Intent(view?.context, CommonWebActivity::class.java)
        view?.context?.startActivity(intent)
    }

    override fun onBind(itemView: View?, data: MutableList<BoutiqueSaleBean.DataBean>?, position: Int, size: Int) {
//        if (firstBind) {
//            firstBind = false
        LogUtil.e("HomeViewHolder", "onBind:${data?.size}")
        if (recyclerView == null) {
            recyclerView = itemView?.findViewById(R.id.recycler_view)
            recyclerView?.layoutManager = GridLayoutManager(itemView?.context, 5)
            val commonAdapter = CommonAdapter(data)
            commonAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN)
            commonAdapter.isFirstOnly(true)
            commonAdapter.onItemClickListener = this
            LogUtil.e("HomeViewHolder","recyclerView == $recyclerView")
            commonAdapter.bindToRecyclerView(recyclerView)
        }
//        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_common
    }
}