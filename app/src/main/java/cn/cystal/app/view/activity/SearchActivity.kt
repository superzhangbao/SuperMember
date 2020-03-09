package cn.cystal.app.view.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.AbsoluteSizeSpan
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import cn.cystal.app.R
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.gson.Gson
import com.gyf.immersionbar.ImmersionBar
import com.trello.rxlifecycle2.android.ActivityEvent
import cn.cystal.app.adapter.SearchAdapter
import cn.cystal.app.base.BaseActivity
import cn.cystal.app.bean.BoutiqueSaleBean
import cn.cystal.app.event.WebEvent
import cn.cystal.app.network.BaseObserver
import cn.cystal.app.network.IApiService
import cn.cystal.app.network.NetClient
import cn.cystal.app.util.ToastUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_search.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.greenrobot.eventbus.EventBus


class SearchActivity : BaseActivity(), BaseQuickAdapter.OnItemClickListener, View.OnClickListener, TextWatcher,
    TextView.OnEditorActionListener {
    private var hotData = mutableListOf<BoutiqueSaleBean.DataBean>()
    private var searchAdapter: SearchAdapter? = null

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

        val searchHint = SpannableString("搜索特权")
        searchHint.setSpan(AbsoluteSizeSpan(15, true), 0, searchHint.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        et_search.hint = searchHint
        et_search.addTextChangedListener(this)
        et_search.setOnEditorActionListener(this)

        //获取数据
        getHotData()
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
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            showLoading()
            val hashMap = HashMap<String, String>()
            hashMap["name"] = v?.text.toString()
            hashMap["sort"] = "1"
            NetClient.getInstance()
                .create(IApiService::class.java)
                .search(RequestBody.create("application/json".toMediaTypeOrNull(), Gson().toJson(hashMap)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : BaseObserver<BoutiqueSaleBean>() {
                    override fun onSuccess(t: BoutiqueSaleBean?) {
                        hideLoading()
                        t?.data?.let { dataBean ->
                            searchAdapter?.setNewData(dataBean)
                        }
                    }

                    override fun onError(msg: String?) {
                        hideLoading()
                        ToastUtils.showToast(msg)
                    }
                })
            return true
        }
        return false
    }
}
