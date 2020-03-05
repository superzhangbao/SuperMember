package com.xishitong.supermember.view.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.gson.Gson
import com.gyf.immersionbar.ImmersionBar
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import com.trello.rxlifecycle2.android.ActivityEvent
import com.xishitong.supermember.R
import com.xishitong.supermember.adapter.DetailAllAdapter
import com.xishitong.supermember.adapter.RechargeDetailAdapter
import com.xishitong.supermember.base.BaseActivity
import com.xishitong.supermember.base.LIMIT
import com.xishitong.supermember.bean.BalanceBean
import com.xishitong.supermember.network.BaseObserver
import com.xishitong.supermember.network.IApiService
import com.xishitong.supermember.network.NetClient
import com.xishitong.supermember.storage.ConfigPreferences
import com.xishitong.supermember.util.DialogUtils
import com.xishitong.supermember.util.ToastUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_recharge_detail.*
import kotlinx.android.synthetic.main.common_toolbar.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody


/**
 * 会费充值activity
 */
class RechargeDetailActivity : BaseActivity(), BaseQuickAdapter.OnItemClickListener,
    BaseQuickAdapter.OnItemChildClickListener, OnRefreshListener, View.OnClickListener, OnLoadMoreListener {
    private var rechargeDetailAdapter: RechargeDetailAdapter? = null
    private var emptyView: View? = null
    private var listData: MutableList<BalanceBean.DataBean.ListBean> = ArrayList()
    private var page = 1

    override fun setContentView(): Int {
        return R.layout.activity_recharge_detail
    }

    override fun init(savedInstanceState: Bundle?) {
        v_state_bar.setBackgroundColor(Color.WHITE)
        ImmersionBar.with(this)
            .statusBarDarkFont(true)
            .init()
        rl_toobar.setBackgroundColor(Color.WHITE)
        fl_back.visibility = View.VISIBLE
        tv_title.text = "申请开票"
        tv_title.setTextColor(resources.getColor(R.color.color_333333))
        fl_back.setOnClickListener(this)
        initRecyclerView()
        initSmartRefresh()
    }

    private fun initRecyclerView() {
        recycler_view.layoutManager = LinearLayoutManager(this)
        rechargeDetailAdapter = RechargeDetailAdapter(listData)
        rechargeDetailAdapter!!.openLoadAnimation(BaseQuickAdapter.ALPHAIN)
        rechargeDetailAdapter!!.isFirstOnly(false)
        rechargeDetailAdapter!!.onItemClickListener = this
        rechargeDetailAdapter!!.onItemChildClickListener = this
        emptyView = layoutInflater.inflate(R.layout.empty, recycler_view.parent as ViewGroup, false)
        rechargeDetailAdapter!!.emptyView = emptyView
        rechargeDetailAdapter!!.bindToRecyclerView(recycler_view)
        rechargeDetailAdapter!!.isUseEmpty(false)
    }

    private fun initSmartRefresh() {
        smart_refresh.setEnableLoadMore(true)
        smart_refresh.setOnRefreshListener(this)
        smart_refresh.setOnLoadMoreListener(this)

    }

    override fun onResume() {
        super.onResume()
        smart_refresh.autoRefresh()
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
    }

    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        when (view?.id) {
            R.id.tv_all -> {
                val data = listData[position]
                if (data.status == 0 && data.type == 1) {//申请发票
                    val intent = Intent(this, ApplyInvoiceActivity::class.java)
                    intent.putExtra("orderNo", data.billId)
                    startActivity(intent)
                    return
                }
                if (data.status == 1 && data.courierNumber != null) {//快递单号
                    showCourierNumberDialog(data.courierNumber)
                }
            }
        }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        rechargeDetailAdapter?.isUseEmpty(true)
        page = 1
        val hashMap = HashMap<String, Any>()
        hashMap["token"] = ConfigPreferences.instance.getToken()
        hashMap["status"] = "1"
        hashMap["page"] = page
        hashMap["limit"] = LIMIT
        NetClient.getInstance()
            .create(IApiService::class.java)
            .getBalanceList(RequestBody.create("application/json".toMediaTypeOrNull(), Gson().toJson(hashMap)))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(bindUntilEvent(ActivityEvent.DESTROY))
            .subscribe(object : BaseObserver<BalanceBean>() {
                override fun onSuccess(t: BalanceBean?) {
                    t?.data?.list?.let { list ->
                        val dataList = list.filter { listBean ->
                            listBean.type == 1 && listBean.status != 99
                        }
                        if (dataList.size < LIMIT) {
                            smart_refresh.finishRefreshWithNoMoreData()
                        } else {
                            smart_refresh.finishRefresh()
                            smart_refresh.setNoMoreData(false)
                        }
                        listData = dataList.toMutableList()
                        rechargeDetailAdapter!!.setNewData(listData)
                    }
                }

                override fun onError(msg: String?) {
                    smart_refresh.finishRefresh()
                    ToastUtils.showToast(msg)
                }
            })
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        rechargeDetailAdapter?.isUseEmpty(true)
        page++
        val hashMap = HashMap<String, Any>()
        hashMap["token"] = ConfigPreferences.instance.getToken()
        hashMap["status"] = "1"
        hashMap["page"] = page
        hashMap["limit"] = LIMIT
        NetClient.getInstance()
            .create(IApiService::class.java)
            .getBalanceList(RequestBody.create("application/json".toMediaTypeOrNull(), Gson().toJson(hashMap)))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(bindUntilEvent(ActivityEvent.DESTROY))
            .subscribe(object : BaseObserver<BalanceBean>() {
                override fun onSuccess(t: BalanceBean?) {
                    t?.data?.list?.let { list ->
                        val dataList = list.filter { listBean ->
                            listBean.type == 1 && listBean.status != 99
                        }
                        if (dataList.size < LIMIT) {
                            smart_refresh.finishLoadMoreWithNoMoreData()
                        } else {
                            smart_refresh.finishLoadMore()
                        }
                        listData.addAll(dataList)
                        rechargeDetailAdapter!!.setNewData(listData)
                    }
                }

                override fun onError(msg: String?) {
                    smart_refresh.finishLoadMore()
                    ToastUtils.showToast(msg)
                }
            })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fl_back -> {
                finish()
            }
        }
    }
}
