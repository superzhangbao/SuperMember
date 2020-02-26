package com.xishitong.supermember.view.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.gson.Gson
import com.gyf.immersionbar.ImmersionBar
import com.trello.rxlifecycle2.android.ActivityEvent
import com.xishitong.supermember.R
import com.xishitong.supermember.adapter.SearchAdapter
import com.xishitong.supermember.base.BaseActivity
import com.xishitong.supermember.bean.BoutiqueSaleBean
import com.xishitong.supermember.event.WebEvent
import com.xishitong.supermember.network.BaseObserver
import com.xishitong.supermember.network.IApiService
import com.xishitong.supermember.network.NetClient
import com.xishitong.supermember.util.LogUtil
import com.xishitong.supermember.util.ToastUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_search.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.greenrobot.eventbus.EventBus


class SearchActivity : BaseActivity(), BaseQuickAdapter.OnItemClickListener, View.OnClickListener, TextWatcher {
    private var allData = mutableListOf<BoutiqueSaleBean.DataBean>()
    private var hotData = mutableListOf<BoutiqueSaleBean.DataBean>()
    private var searchedData = mutableListOf<BoutiqueSaleBean.DataBean>()
    private var searchAdapter:SearchAdapter? = null
    override fun setContentView(): Int {
        return R.layout.activity_search
    }

    override fun init(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .statusBarDarkFont(true)
            .init()
        tv_close.setOnClickListener(this)
        val layoutManager = FlexboxLayoutManager(this)
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.justifyContent = JustifyContent.CENTER
        recycler_view.layoutManager = layoutManager
        searchAdapter = SearchAdapter(null)
        searchAdapter!!.openLoadAnimation(BaseQuickAdapter.SCALEIN)
        searchAdapter!!.isFirstOnly(false)
        searchAdapter!!.onItemClickListener = this
        searchAdapter!!.bindToRecyclerView(recycler_view)

        et_search.addTextChangedListener(this)

        getAllData()
        //获取数据
        getHotData()
    }

    private fun getAllData() {
        repeat(3) {
            val hashMap = HashMap<String, Int>()
            hashMap["sort"] = it + 2
            NetClient.getInstance()
                .create(IApiService::class.java)
                .getBoutiqueSale(RequestBody.create("application/json".toMediaTypeOrNull(), Gson().toJson(hashMap)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(object : BaseObserver<BoutiqueSaleBean>() {
                    override fun onSuccess(t: BoutiqueSaleBean?) {
                        t?.data?.let { dataBean ->
                            allData.addAll(dataBean)
                        }
                    }

                    override fun onError(msg: String?) {
                        ToastUtils.showToast(msg)
                    }
                })
        }
    }

    private fun getHotData() {
        showLoading()
        val hashMap = HashMap<String, Int>()
        hashMap["sort"] = 1
        NetClient.getInstance()
            .create(IApiService::class.java)
            .getBoutiqueSale(RequestBody.create("application/json".toMediaTypeOrNull(), Gson().toJson(hashMap)))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(bindUntilEvent(ActivityEvent.DESTROY))
            .subscribe(object : BaseObserver<BoutiqueSaleBean>() {
                override fun onSuccess(t: BoutiqueSaleBean?) {
                    hideLoading()
                    t?.data?.let { dataBean ->
                        allData.addAll(dataBean)
                        if (dataBean.size <= 15) {
                            hotData = dataBean
                            searchAdapter!!.setNewData(dataBean)
                        } else {
                            hotData = dataBean.filterIndexed { index, _ -> index < 15 }.toMutableList()
                            searchAdapter!!.setNewData(hotData)
                        }
                    }
                }

                override fun onError(msg: String?) {
                    hideLoading()
                    ToastUtils.showToast(msg)
                }
            })
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        EventBus.getDefault()
            .postSticky(WebEvent((adapter?.data as MutableList<BoutiqueSaleBean.DataBean>)[position].url))
        val intent = Intent(this, CommonWebActivity::class.java)
        startActivity(intent)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_close -> {
                finish()
            }
        }
    }

    override fun afterTextChanged(s: Editable?) {
        val searchStr = s.toString()
        if (searchStr.isEmpty()) {
            searchAdapter?.setNewData(hotData)
            return
        }
        Observable.just(1)
            .subscribeOn(Schedulers.io())
            .compose(bindUntilEvent(ActivityEvent.DESTROY))
            .doOnNext {
                LogUtil.e(TAG,Thread.currentThread().name)
                searchedData.clear()
                allData.forEach {
                    if (it.name.contains(searchStr)) {
                        searchedData.add(it)
                    }
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                LogUtil.e(TAG,Thread.currentThread().name)
                searchAdapter?.setNewData(searchedData)
//                searchedData.clear()
            }
            .subscribe()
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }
}
