package cn.cystal.app.view.fragment

import android.content.Intent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import cn.cystal.app.adapter.DetailAllAdapter
import cn.cystal.app.base.BaseFragment
import com.chad.library.adapter.base.BaseQuickAdapter
import com.cystal.app.R
import cn.cystal.app.base.DETAIL_TYPE
import cn.cystal.app.base.LIMIT
import cn.cystal.app.bean.BalanceBean
import cn.cystal.app.network.BaseObserver
import cn.cystal.app.network.IApiService
import cn.cystal.app.network.NetClient
import cn.cystal.app.storage.ConfigPreferences
import cn.cystal.app.util.ToastUtils
import cn.cystal.app.view.activity.ApplyInvoiceActivity
import cn.cystal.app.view.activity.DetailOfMembershipActivity
import com.google.gson.Gson
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import com.trello.rxlifecycle2.android.FragmentEvent
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
    BaseQuickAdapter.OnItemChildClickListener, OnRefreshListener, OnLoadMoreListener {

    private var detailAllAdapter: DetailAllAdapter? = null
    private var emptyView: View? = null
    private var listData: MutableList<BalanceBean.DataBean.ListBean> = ArrayList()
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

    override fun initData() {
        smart_refresh.autoRefresh()
    }

    private fun initRecyclerView() {
        recycler_view.layoutManager = LinearLayoutManager(activity)
        detailAllAdapter = DetailAllAdapter(listData)
        detailAllAdapter!!.openLoadAnimation(BaseQuickAdapter.ALPHAIN)
        detailAllAdapter!!.isFirstOnly(false)
        detailAllAdapter!!.onItemClickListener = this
        detailAllAdapter!!.onItemChildClickListener = this
        emptyView = layoutInflater.inflate(R.layout.empty, recycler_view.parent as ViewGroup, false)
        detailAllAdapter!!.emptyView = emptyView
        detailAllAdapter!!.bindToRecyclerView(recycler_view)
        detailAllAdapter!!.isUseEmpty(false)
    }

    private fun initSmartRefresh() {
        smart_refresh.setEnableLoadMore(true)
        smart_refresh.setOnRefreshListener(this)
        smart_refresh.setOnLoadMoreListener(this)
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
    }

    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        when (view?.id) {
            R.id.tv_all -> {
                val data = listData[position]
                if (data.status == 0 && data.type == 1) {//申请发票
                    val intent = Intent(activity, ApplyInvoiceActivity::class.java)
                    intent.putExtra("orderNo", data.billId)
                    startActivity(intent)
                    return
                }
                if (data.status == 1 && data.courierNumber != null) {//快递单号
                    val membershipActivity = activity as DetailOfMembershipActivity
                    membershipActivity.showBaseCourierNumberDialog(data.courierNumber)
                }
            }
        }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        detailAllAdapter?.isUseEmpty(true)
        page = 1
        val hashMap = HashMap<String, Any>()
        hashMap["token"] = ConfigPreferences.instance.getToken()
        hashMap["status"] = type
        hashMap["page"] = page
        hashMap["limit"] = LIMIT
        NetClient.getInstance()
            .create(IApiService::class.java)
            .getBalanceList(RequestBody.create("application/json".toMediaTypeOrNull(), Gson().toJson(hashMap)))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
            .subscribe(object : BaseObserver<BalanceBean>() {
                override fun onSuccess(t: BalanceBean?) {
//                    smart_refresh.finishRefresh()
                    t?.data?.list?.let { list ->
                        listData = when (type) {
                            "0" -> {
                                list
                            }
                            "1" -> {
                                list.filter { listBean ->
                                    listBean.type == 1
                                }.toMutableList()
                            }
                            "2" -> {
                                list.filter { listBean ->
                                    listBean.type == 2
                                }.toMutableList()
                            }
                            else -> listData
                        }
                        if (listData.size < LIMIT) {
                            smart_refresh.finishRefreshWithNoMoreData()
                        } else {
                            smart_refresh.finishRefresh()
                            smart_refresh.setNoMoreData(false)
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

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        detailAllAdapter?.isUseEmpty(true)
        page++
        val hashMap = HashMap<String, Any>()
        hashMap["token"] = ConfigPreferences.instance.getToken()
        hashMap["status"] = type
        hashMap["page"] = page
        hashMap["limit"] = LIMIT
        NetClient.getInstance()
            .create(IApiService::class.java)
            .getBalanceList(RequestBody.create("application/json".toMediaTypeOrNull(), Gson().toJson(hashMap)))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
            .subscribe(object : BaseObserver<BalanceBean>() {
                override fun onSuccess(t: BalanceBean?) {
                    t?.data?.list?.let { list ->
                        val newList = when (type) {
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
                            else -> list
                        }
                        if (newList.size < LIMIT) {
                            smart_refresh.finishLoadMoreWithNoMoreData()
                        } else {
                            smart_refresh.finishLoadMore()
                        }
                        listData.addAll(newList)
                        detailAllAdapter!!.setNewData(listData)
                    }
                }

                override fun onError(msg: String?) {
                    smart_refresh.finishLoadMore()
                    ToastUtils.showToast(msg)
                }
            })
    }
}