package com.xishitong.supermember.view.fragment

import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.gson.Gson
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import com.trello.rxlifecycle2.android.FragmentEvent
import com.xishitong.supermember.R
import com.xishitong.supermember.adapter.DetailAllAdapter
import com.xishitong.supermember.base.BaseFragment
import com.xishitong.supermember.base.DETAIL_TYPE
import com.xishitong.supermember.bean.BalanceBean
import com.xishitong.supermember.network.BaseObserver
import com.xishitong.supermember.network.IApiService
import com.xishitong.supermember.network.NetClient
import com.xishitong.supermember.storage.ConfigPreferences
import com.xishitong.supermember.util.ToastUtils
import com.xishitong.supermember.view.activity.ApplyInvoiceActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_detail_all.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody

/**
 *  author : zhangbao
 *  date : 2020-02-10 21:26
 *  description :
 */
class DetailAllFragment : BaseFragment(), BaseQuickAdapter.OnItemClickListener,
    BaseQuickAdapter.OnItemChildClickListener, OnRefreshListener {

    private var detailAllAdapter: DetailAllAdapter? = null
    private var emptyView: View? = null
    private var listData: List<BalanceBean.DataBean.ListBean> = ArrayList()
    private var type = "0"
    override fun setContentView(): Int {
        return R.layout.fragment_detail_all
    }

    override fun initView(view: View) {
        type = arguments?.getString(DETAIL_TYPE)!!
        initRecyclerView()
        initSmartRefresh()
    }

    override fun initData() {
        smart_refresh.autoRefresh()
    }

    private fun initRecyclerView() {
        recycler_view.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)
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

    private fun initSmartRefresh() {
        smart_refresh.setOnRefreshListener(this)
        smart_refresh.setEnableLoadMore(false)
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        ToastUtils.showToast("点击了$position")
    }

    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        when (view?.id) {
            R.id.tv_all -> {
                val data = listData[position]
                if (data.status == 0 && data.type ==1) {//申请发票
                    val intent = Intent(activity, ApplyInvoiceActivity::class.java)
                    intent.putExtra("orderNo", data.billId)
                    startActivity(intent)
                    return
                }
                if (data.status == 1 && data.courierNumber!= null) {//快递单号
                    ToastUtils.showToast("快递单号")
                }
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
                    smart_refresh.finishRefresh()
                    t?.data?.list?.let { list ->
                        listData = when (type) {
                            "0" -> {
                                list
                            }
                            "1" -> {
                                list.filter { listBean ->
                                    listBean.type == 1
                                }
                            }
                            "2" -> {
                                list.filter { listBean ->
                                    listBean.type == 2
                                }
                            }
                            else -> listData
                        }
                        detailAllAdapter!!.setNewData(listData)
                    }
                }

                override fun onError(msg: String?) {
                    smart_refresh.finishRefresh()
                    ToastUtils.showToast(msg)
                }
            })
    }
}