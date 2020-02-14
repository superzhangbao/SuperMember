package com.xishitong.supermember.view.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.ContactsContract
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.gson.Gson
import com.gyf.immersionbar.ImmersionBar
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import com.trello.rxlifecycle2.android.ActivityEvent
import com.xishitong.supermember.R
import com.xishitong.supermember.adapter.MyAddressAdapter
import com.xishitong.supermember.base.ADD_OR_MODIFY
import com.xishitong.supermember.base.BaseActivity
import com.xishitong.supermember.bean.MyAddressBean
import com.xishitong.supermember.bean.MyAddressBean.DataBean
import com.xishitong.supermember.network.BaseObserver
import com.xishitong.supermember.network.IApiService
import com.xishitong.supermember.network.NetClient
import com.xishitong.supermember.storage.ConfigPreferences
import com.xishitong.supermember.util.LogUtil
import com.xishitong.supermember.util.ToastUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_my_address.*
import kotlinx.android.synthetic.main.common_toolbar.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import java.util.concurrent.TimeUnit

/**
 * 我的地址页面
 */
class MyAddressActivity : BaseActivity(), View.OnClickListener, BaseQuickAdapter.OnItemClickListener,
    BaseQuickAdapter.OnItemChildClickListener,OnRefreshListener {

    private var myAddressAdapter: MyAddressAdapter? = null
    private var listData: MutableList<DataBean.ListBean>? = null

    override fun setContentView(): Int {
        return R.layout.activity_my_address
    }

    override fun init(savedInstanceState: Bundle?) {
        initToolbar()
        initRecyclerView()
        initSmartRefresh()
    }

    override fun onResume() {
        super.onResume()
        smart_refresh.autoRefresh()
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
        add_address.setOnClickListener(this)
    }

    private fun initRecyclerView() {
        recycler_view.layoutManager = LinearLayoutManager(this)
        myAddressAdapter = MyAddressAdapter(listData)
        myAddressAdapter!!.openLoadAnimation(BaseQuickAdapter.SCALEIN)
        myAddressAdapter!!.isFirstOnly(false)
        myAddressAdapter!!.onItemClickListener = this
        myAddressAdapter!!.onItemChildClickListener = this
        myAddressAdapter?.bindToRecyclerView(recycler_view)
    }

    private fun initSmartRefresh() {
        smart_refresh.setOnRefreshListener(this)
        smart_refresh.setEnableLoadMore(false)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.fl_back->{
                finish()
            }
            R.id.add_address->{
                val intent = Intent(this, ModifyAddressActivity::class.java)
                intent.putExtra(ADD_OR_MODIFY,0)
                startActivity(intent)
            }
        }
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        listData?.let {
            val intent = Intent(this, ModifyAddressActivity::class.java)
            intent.putExtra("name", listData!![position].name)
            intent.putExtra("phone",listData!![position].phone)
            intent.putExtra("gegion",listData!![position].gegion)
            intent.putExtra("detailed",listData!![position].detailed)
            intent.putExtra("id","${listData!![position].id}")
            intent.putExtra(ADD_OR_MODIFY,1)
            startActivity(intent)
        }
    }

    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {

    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        val hashMap = mapOf(Pair("token",ConfigPreferences.instance.getToken()))
        NetClient.getInstance()
            .create(IApiService::class.java)
            .getAddressList(RequestBody.create("application/json".toMediaTypeOrNull(), Gson().toJson(hashMap)))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(bindUntilEvent(ActivityEvent.DESTROY))
            .subscribe(object :BaseObserver<MyAddressBean>(){
                override fun onSuccess(t: MyAddressBean?) {
                    smart_refresh.finishRefresh()
                    t?.data?.let {
                        listData = it.list
                        myAddressAdapter!!.setNewData(listData)
                    }
                }

                override fun onError(msg: String?) {
                    smart_refresh.finishRefresh()
                    ToastUtils.showToast(msg)
                }
            })
    }
}
