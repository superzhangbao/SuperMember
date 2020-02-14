package com.xishitong.supermember.view.activity

import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.gyf.immersionbar.ImmersionBar
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import com.trello.rxlifecycle2.android.ActivityEvent
import com.xishitong.supermember.R
import com.xishitong.supermember.adapter.ProcessingOrderAdapter
import com.xishitong.supermember.base.BaseActivity
import com.xishitong.supermember.bean.OrderBean
import com.xishitong.supermember.util.LogUtil
import com.xishitong.supermember.util.ToastUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_processing_order.*
import kotlinx.android.synthetic.main.common_toolbar.*
import java.util.concurrent.TimeUnit

/**
 * 处理中订单
 */
class ProcessingOrderActivity : BaseActivity(), View.OnClickListener, OnRefreshListener,
    BaseQuickAdapter.OnItemClickListener, BaseQuickAdapter.OnItemChildClickListener {

    private var processingOrderAdapter:ProcessingOrderAdapter? = null
    private var listData: MutableList<OrderBean.DataBean.ListBean> = mutableListOf()
    override fun setContentView(): Int {
        return R.layout.activity_processing_order
    }

    override fun init(savedInstanceState: Bundle?) {
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
        tv_title.text = getString(R.string.receiving_address)
        tv_title.setTextColor(resources.getColor(R.color.color_333333))
        fl_back.setOnClickListener(this)
    }

    private fun initRecyclerView() {
        recycler_view.layoutManager = LinearLayoutManager(this)
        processingOrderAdapter = ProcessingOrderAdapter(listData)
        processingOrderAdapter!!.openLoadAnimation(BaseQuickAdapter.ALPHAIN)
        processingOrderAdapter!!.isFirstOnly(false)
        processingOrderAdapter!!.onItemClickListener = this
        processingOrderAdapter!!.onItemChildClickListener = this
        processingOrderAdapter?.emptyView =  layoutInflater.inflate(R.layout.empty, recycler_view.parent as ViewGroup, false)
        processingOrderAdapter?.bindToRecyclerView(recycler_view)
        processingOrderAdapter?.isUseEmpty(false)
    }

    private fun initSmartRefresh() {
        smart_refresh.setOnRefreshListener(this)
        smart_refresh.setEnableLoadMore(false)
        smart_refresh.autoRefresh()
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.fl_back->{
                finish()
            }
        }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        processingOrderAdapter?.isUseEmpty(true)
        Observable.just(1)
            .delay(2,TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .compose(bindUntilEvent(ActivityEvent.DESTROY))
            .doOnNext {
                smart_refresh.finishRefresh()
                repeat(30){
                    listData.add(OrderBean.DataBean.ListBean("15056006309","2019-12-04 10:58:22",9980,"169756033",10,1,9980,"https://www.seniornet.cn/images/lofter/quanyika.png","会费缴纳",10000,"备注",1,"15056006309"))
                }
                processingOrderAdapter!!.setNewData(listData)
            }.subscribe()
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        ToastUtils.showToast("$position")
    }

    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        LogUtil.e(TAG,"onItemChildClick")
        when(view?.id) {
            R.id.tv_apply_invoice->{
                ToastUtils.showToast("申请发票${position}")
            }
        }
    }
}
