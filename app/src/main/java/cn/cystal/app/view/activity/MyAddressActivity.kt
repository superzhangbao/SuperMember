package cn.cystal.app.view.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import cn.cystal.app.R
import cn.cystal.app.adapter.MyAddressAdapter
import cn.cystal.app.base.ADD_OR_MODIFY
import cn.cystal.app.base.BaseActivity
import cn.cystal.app.base.CHOOSE
import cn.cystal.app.base.RESULT_CODE_MYADDRESS
import cn.cystal.app.bean.MyAddressBean
import cn.cystal.app.bean.MyAddressBean.DataBean
import cn.cystal.app.network.BaseObserver
import cn.cystal.app.network.IApiService
import cn.cystal.app.network.NetClient
import cn.cystal.app.storage.ConfigPreferences
import cn.cystal.app.util.ToastUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.gson.Gson
import com.gyf.immersionbar.ImmersionBar
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import com.trello.rxlifecycle2.android.ActivityEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_my_address.*
import kotlinx.android.synthetic.main.common_toolbar.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody

/**
 * 我的地址页面
 */
class MyAddressActivity : BaseActivity(), View.OnClickListener, BaseQuickAdapter.OnItemClickListener,
    BaseQuickAdapter.OnItemChildClickListener,OnRefreshListener {

    private var myAddressAdapter: MyAddressAdapter? = null
    private var listData: MutableList<DataBean.ListBean>? = null
    private var from:String = ""

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
        ImmersionBar.with(this)
            .statusBarDarkFont(true)
            .init()
        tv_title.text = getString(R.string.receiving_address)
        tb_toolbar.title = ""
        setSupportActionBar(tb_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)//左侧添加一个默认的返回图标
        supportActionBar?.setHomeButtonEnabled(true) //设置返回键可用

        add_address.setOnClickListener(this)
        from = intent.getStringExtra("from")
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            android.R.id.home->{
                finish()
            }
        }
        return true
    }

    private fun initRecyclerView() {
        recycler_view.layoutManager = LinearLayoutManager(this)
        myAddressAdapter = MyAddressAdapter(listData)
        myAddressAdapter!!.openLoadAnimation(BaseQuickAdapter.ALPHAIN)
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
            R.id.add_address->{
                val intent = Intent(this, ModifyAddressActivity::class.java)
                intent.putExtra(ADD_OR_MODIFY,0)
                startActivity(intent)
            }
        }
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        listData?.let {
            if (from == CHOOSE) {
                intent.putExtra("name", listData!![position].name)
                intent.putExtra("phone",listData!![position].phone)
                intent.putExtra("gegion",listData!![position].gegion)
                intent.putExtra("detailed",listData!![position].detailed)
                setResult(RESULT_CODE_MYADDRESS,intent)
                finish()
            }else{
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
    }

    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {

    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        val hashMap = mapOf(Pair("token", ConfigPreferences.instance.getToken()))
        NetClient.getInstance()
            .create(IApiService::class.java)
            .getAddressList(RequestBody.create("application/json".toMediaTypeOrNull(), Gson().toJson(hashMap)))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(bindUntilEvent(ActivityEvent.DESTROY))
            .subscribe(object :
                BaseObserver<MyAddressBean>(){
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
