package com.xishitong.supermember.view.activity

import android.os.Bundle
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.gyf.immersionbar.ImmersionBar
import com.trello.rxlifecycle2.android.ActivityEvent
import com.xishitong.supermember.R
import com.xishitong.supermember.adapter.SearchAdapter
import com.xishitong.supermember.base.BaseActivity
import com.xishitong.supermember.util.ToastUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_search.*
import java.util.concurrent.TimeUnit


class SearchActivity : BaseActivity(), BaseQuickAdapter.OnItemClickListener, View.OnClickListener {
    var tags = arrayOf(
        "吃货", "逗比", "创业者", "上班这点事儿",
        "影视天堂", "大学生活", "单身狗", "运动和健身",
        "网购达人", "旅游", "程序员", "追星族", "短篇小说",
        "美食", "教育", "学生党", "汪星人"
    ).toList()


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
        layoutManager.justifyContent = JustifyContent.FLEX_START
        recycler_view.layoutManager = layoutManager
        val searchAdapter = SearchAdapter(null)
        searchAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN)
        searchAdapter.isFirstOnly(false)
        searchAdapter.onItemClickListener = this
        searchAdapter.bindToRecyclerView(recycler_view)

        //获取数据
        getHotData(searchAdapter)
    }

    private fun getHotData(searchAdapter: SearchAdapter) {
        Observable.just(1)
            .delay(2, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(bindUntilEvent(ActivityEvent.DESTROY))
            .doOnNext {
                searchAdapter.setNewData(tags)
            }
            .subscribe()
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        ToastUtils.showToast(tags[position])
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_close -> {
                finish()
            }
        }
    }
}
