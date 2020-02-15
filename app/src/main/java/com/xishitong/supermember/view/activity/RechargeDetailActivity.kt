package com.xishitong.supermember.view.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.gson.Gson
import com.gyf.immersionbar.ImmersionBar
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import com.trello.rxlifecycle2.android.ActivityEvent
import com.xishitong.supermember.R
import com.xishitong.supermember.adapter.DetailAllAdapter
import com.xishitong.supermember.base.BaseActivity
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
    BaseQuickAdapter.OnItemChildClickListener, OnRefreshListener, View.OnClickListener {
    private var detailAllAdapter: DetailAllAdapter? = null
    private var emptyView: View? = null
    private var listData: List<BalanceBean.DataBean.ListBean> = ArrayList()
    private var dialogUtils: DialogUtils? = null

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
        recycler_view.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
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
                    val builder = DialogUtils.Builder(this)
                    dialogUtils = builder.view(R.layout.dialog_courier_number)
                        .cancelable(true)
                        .gravity(Gravity.CENTER)
                        .cancelTouchout(true)
                        .addViewOnclick(R.id.tv_copy) {
                            //获取剪贴板管理器：
                            val cm: ClipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            // 创建普通字符型ClipData
                            val mClipData = ClipData.newPlainText("Label", data.courierNumber)
                            // 将ClipData内容放到系统剪贴板里。
                            cm.primaryClip = mClipData
                            ToastUtils.showToast("复制成功")
                            dialogUtils?.dismiss()
                        }
                        .style(R.style.Dialog)
                        .build()
                    dialogUtils!!.show()
                }
            }
        }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        detailAllAdapter?.isUseEmpty(true)
        val hashMap = HashMap<String, Any>()
        hashMap["token"] = ConfigPreferences.instance.getToken()
        hashMap["status"] = "1"
        hashMap["page"] = 1
        hashMap["limit"] = 10
        NetClient.getInstance()
            .create(IApiService::class.java)
            .getBalanceList(RequestBody.create("application/json".toMediaTypeOrNull(), Gson().toJson(hashMap)))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(bindUntilEvent(ActivityEvent.DESTROY))
            .subscribe(object : BaseObserver<BalanceBean>() {
                override fun onSuccess(t: BalanceBean?) {
                    smart_refresh.finishRefresh()
                    t?.data?.list?.let { list ->
                        listData = list.filter { listBean ->
                            listBean.type == 1
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

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fl_back -> {
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        dialogUtils?.let {
            if (it.isShowing) {
                it.dismiss()
            }
        }
    }
}
