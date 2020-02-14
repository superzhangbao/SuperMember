package com.xishitong.supermember.view.fragment

import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.gson.Gson
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import com.trello.rxlifecycle2.RxLifecycle
import com.trello.rxlifecycle2.RxLifecycle.bindUntilEvent
import com.trello.rxlifecycle2.android.ActivityEvent
import com.trello.rxlifecycle2.android.FragmentEvent
import com.xishitong.supermember.R
import com.xishitong.supermember.adapter.DetailAllAdapter
import com.xishitong.supermember.base.BaseFragment
import com.xishitong.supermember.base.DETAIL_TYPE
import com.xishitong.supermember.bean.BalanceBean
import com.xishitong.supermember.bean.DetailAllBean
import com.xishitong.supermember.network.BaseObserver
import com.xishitong.supermember.network.IApiService
import com.xishitong.supermember.network.NetClient
import com.xishitong.supermember.storage.ConfigPreferences
import com.xishitong.supermember.util.LogUtil
import com.xishitong.supermember.util.ToastUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_detail_all.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import java.util.concurrent.TimeUnit

/**
 *  author : zhangbao
 *  date : 2020-02-10 21:26
 *  description :
 */
class DetailAllFragment : BaseFragment(), BaseQuickAdapter.OnItemClickListener,
    BaseQuickAdapter.OnItemChildClickListener, OnRefreshListener {

    private var detailAllAdapter: DetailAllAdapter? = null
    private var emptyView: View? = null
    private var listData: MutableList<BalanceBean.DataBean.ListBean> = mutableListOf()
    private var type = "0"
    private var page = 1
    override fun setContentView(): Int {
        return R.layout.fragment_detail_all
    }

    override fun initView(view: View) {
        type = arguments?.getString(DETAIL_TYPE)!!
        initRecyclerView()
        initSmartRefresh()
    }

    private fun initSmartRefresh() {
        smart_refresh.setOnRefreshListener(this)
        smart_refresh.setEnableLoadMore(false)
        smart_refresh.autoRefresh()
    }

    private fun initRecyclerView() {
        recycler_view.layoutManager = LinearLayoutManager(activity)
        detailAllAdapter = DetailAllAdapter(listData)
        detailAllAdapter!!.openLoadAnimation(BaseQuickAdapter.SCALEIN)
        detailAllAdapter!!.isFirstOnly(false)
        detailAllAdapter!!.onItemClickListener = this
        detailAllAdapter!!.onItemChildClickListener = this
        emptyView = layoutInflater.inflate(R.layout.empty, recycler_view.parent as ViewGroup, false)
        detailAllAdapter!!.emptyView = emptyView
        detailAllAdapter!!.bindToRecyclerView(recycler_view)
        detailAllAdapter!!.isUseEmpty(false)
    }

    override fun initData() {

    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        ToastUtils.showToast("点击了$position")
    }

    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        when (view?.id) {
            R.id.tv_all -> {
                ToastUtils.showToast("按钮响应$position")
            }
        }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        detailAllAdapter?.isUseEmpty(true)
        val hashMap = HashMap<String, Any>()
        hashMap["token"] = ConfigPreferences.instance.getToken()
        hashMap["status"] = type
        hashMap["page"] = 1
        hashMap["limit"] = 10
        NetClient.getInstance()
            .create(IApiService::class.java)
            .getBalanceList(RequestBody.create("application/json".toMediaTypeOrNull(), Gson().toJson(hashMap)))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
            .subscribe(object : BaseObserver<BalanceBean>() {
                override fun onSuccess(t: BalanceBean?) {
                    t?.data?.let {
                        smart_refresh.finishRefresh()
                        detailAllAdapter!!.setNewData(it.list)
                    }
                }

                override fun onError(msg: String?) {
                    smart_refresh.finishRefresh()
                    ToastUtils.showToast(msg)
                }
            })
    }
}