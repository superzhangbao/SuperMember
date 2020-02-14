package com.xishitong.supermember.view.fragment

import android.content.Context
import android.support.v4.view.ViewPager
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.xishitong.supermember.R
import com.xishitong.supermember.adapter.CommonAdapter
import com.xishitong.supermember.base.App
import com.xishitong.supermember.base.BaseFragment
import com.xishitong.supermember.bean.CommonBean
import com.xishitong.supermember.util.LogUtil
import com.xishitong.supermember.util.ToastUtils
import com.xishitong.supermember.util.UiUtils
import com.zhpan.bannerview.BannerViewPager
import com.zhpan.bannerview.holder.ViewHolder
import kotlinx.android.synthetic.main.fragment_recommend.*

/**
 * author : zhangbao
 * date : 2020-02-11 16:42
 * description :精品推荐fragment
 */
class RecommendFragment : BaseFragment(), ViewPager.OnPageChangeListener {

    private var mHomeViewPager: BannerViewPager<MutableList<CommonBean.DataBean.ListBean>, HomeViewHolder>? = null
    override fun setContentView(): Int {
        return R.layout.fragment_recommend
    }

    override fun initView(view: View) {
        mHomeViewPager = view.findViewById(R.id.view_pager)
        initHomeBanner()

    }

    override fun initData() {

    }

    private fun initHomeBanner() {
        val data = mutableListOf<MutableList<CommonBean.DataBean.ListBean>>()
        val list = mutableListOf<CommonBean.DataBean.ListBean>()
        val list1 = mutableListOf<CommonBean.DataBean.ListBean>()
        val list2 = mutableListOf<CommonBean.DataBean.ListBean>()
        repeat(15) {
            list.add(CommonBean.DataBean.ListBean("","","$it"))
        }
        repeat(15) {
            list1.add(CommonBean.DataBean.ListBean("","","$it"))
        }
        repeat(8) {
            list2.add(CommonBean.DataBean.ListBean("","","$it"))
        }
        data.add(list)
        LogUtil.e(TAG,list.toString())
        data.add(list1)
        data.add(list2)
        mHomeViewPager!!.setIndicatorVisibility(View.VISIBLE)
            .setIndicatorView(oval_indicator)
            .setIndicatorColor(resources.getColor(R.color.color_88F86024), resources.getColor(R.color.color_F86024))
            .setCanLoop(false)
            .setAutoPlay(false)
            .setIndicatorGap(UiUtils.dip2px(App.getInstance(),3.0f))
            .setIndicatorHeight(UiUtils.dip2px(App.getInstance(),4.0f))
            .setIndicatorWidth(UiUtils.dip2px(App.getInstance(),3.0f), UiUtils.dip2px(App.getInstance(),10.0f))
            .setHolderCreator{ HomeViewHolder() }
            .create(data)
    }

    override fun onPageScrollStateChanged(p0: Int) {

    }

    override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {

    }

    override fun onPageSelected(p0: Int) {
        LogUtil.e(TAG, "$p0")
    }
}

class HomeViewHolder: ViewHolder<MutableList<CommonBean.DataBean.ListBean>>, BaseQuickAdapter.OnItemClickListener {
    private var recyclerView: RecyclerView? = null
    private var commonAdapter:CommonAdapter? = null
    override fun onBind(context: Context?, data: MutableList<CommonBean.DataBean.ListBean>?, position: Int, size: Int) {
        recyclerView!!.layoutManager = GridLayoutManager(context, 5)
        commonAdapter = CommonAdapter(data)
        commonAdapter!!.openLoadAnimation(BaseQuickAdapter.SCALEIN)
        commonAdapter!!.isFirstOnly(false)
        commonAdapter!!.onItemClickListener = this
        commonAdapter!!.bindToRecyclerView(recyclerView)
    }

    override fun createView(viewGroup: ViewGroup?, context: Context?, position: Int): View {
        val view = LayoutInflater.from(context).inflate(R.layout.fragment_common, viewGroup, false)
        recyclerView = view.findViewById(R.id.recycler_view)
        return view
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        ToastUtils.showToast("$position")
    }
}