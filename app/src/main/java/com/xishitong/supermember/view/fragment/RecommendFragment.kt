package com.xishitong.supermember.view.fragment

import android.content.Context
import android.content.Intent
import androidx.viewpager.widget.ViewPager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.gson.Gson
import com.trello.rxlifecycle2.android.FragmentEvent
import com.xishitong.supermember.R
import com.xishitong.supermember.adapter.CommonAdapter
import com.xishitong.supermember.base.App
import com.xishitong.supermember.base.BaseFragment
import com.xishitong.supermember.base.BaseFragment.Companion.TAG
import com.xishitong.supermember.bean.BoutiqueSaleBean
import com.xishitong.supermember.bean.CommonBean
import com.xishitong.supermember.event.WebEvent
import com.xishitong.supermember.network.BaseObserver
import com.xishitong.supermember.network.IApiService
import com.xishitong.supermember.network.NetClient
import com.xishitong.supermember.storage.ConfigPreferences
import com.xishitong.supermember.util.LogUtil
import com.xishitong.supermember.util.ToastUtils
import com.xishitong.supermember.util.UiUtils
import com.xishitong.supermember.view.activity.CommonWebActivity
import com.zhpan.bannerview.BannerViewPager
import com.zhpan.bannerview.holder.ViewHolder
import com.zhpan.indicator.enums.IndicatorStyle
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_recommend.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.greenrobot.eventbus.EventBus

/**
 * author : zhangbao
 * date : 2020-02-11 16:42
 * description :精品推荐fragment
 */
class RecommendFragment : BaseFragment() {

    private var mHomeViewPager: BannerViewPager<MutableList<BoutiqueSaleBean.DataBean>, HomeViewHolder>? = null
    private var type = 1
    var data: MutableList<MutableList<BoutiqueSaleBean.DataBean>> = mutableListOf()

    override fun setContentView(): Int {
        return R.layout.fragment_recommend
    }

    override fun initView(view: View) {
        mHomeViewPager = view.findViewById(R.id.view_pager)
        arguments?.let { bundle ->
            type = bundle.getInt("type")
        }
    }

    override fun initData() {
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
                            .create(data)
                    }

                }

                override fun onError(msg: String?) {
                    ToastUtils.showToast(msg)
                }
            })
    }
}

class HomeViewHolder : ViewHolder<MutableList<BoutiqueSaleBean.DataBean>>, BaseQuickAdapter.OnItemClickListener {
//    private var recyclerView: RecyclerView? = null
//    private var commonAdapter: CommonAdapter? = null
//    private var context:Context =

//    override fun onBind(context: Context?, data: MutableList<BoutiqueSaleBean.DataBean>?, position: Int, size: Int) {
//        this.context = context
//        recyclerView!!.layoutManager = GridLayoutManager(context, 5)
//        commonAdapter = CommonAdapter(data)
//        commonAdapter!!.openLoadAnimation(BaseQuickAdapter.SCALEIN)
//        commonAdapter!!.isFirstOnly(false)
//        commonAdapter!!.onItemClickListener = this
//        commonAdapter!!.bindToRecyclerView(recyclerView)
//    }
//
//    override fun createView(viewGroup: ViewGroup?, context: Context?, position: Int): View {
//        val view = LayoutInflater.from(context).inflate(R.layout.fragment_common, viewGroup, false)
//        recyclerView = view.findViewById(R.id.recycler_view)
//        return view
//    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        EventBus.getDefault()
            .postSticky(WebEvent((adapter?.data as MutableList<BoutiqueSaleBean.DataBean>)[position].url))
        val intent = Intent(view?.context, CommonWebActivity::class.java)
        view?.context?.startActivity(intent)
    }

    override fun onBind(itemView: View?, data: MutableList<BoutiqueSaleBean.DataBean>?, position: Int, size: Int) {
        val recyclerView: RecyclerView? = itemView?.findViewById(R.id.recycler_view)
        recyclerView?.layoutManager = GridLayoutManager(itemView?.context, 5)
        val commonAdapter = CommonAdapter(data)
        commonAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN)
        commonAdapter.isFirstOnly(true)
        commonAdapter.onItemClickListener = this
        commonAdapter.bindToRecyclerView(recyclerView)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_common
    }
}