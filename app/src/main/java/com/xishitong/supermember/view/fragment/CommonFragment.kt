package com.xishitong.supermember.view.fragment

import androidx.recyclerview.widget.GridLayoutManager
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.xishitong.supermember.R
import com.xishitong.supermember.adapter.CommonAdapter
import com.xishitong.supermember.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_common.*

/**
 * author : zhangbao
 * date : 2020-02-11 17:23
 * description :
 */
class CommonFragment: BaseFragment(), BaseQuickAdapter.OnItemClickListener {

    private var commonAdapter: CommonAdapter? = null

    override fun setContentView(): Int {
        return R.layout.fragment_common
    }

    override fun initView(view: View) {
        recycler_view.layoutManager = androidx.recyclerview.widget.GridLayoutManager(activity, 5)
        commonAdapter = CommonAdapter(null)
        commonAdapter!!.openLoadAnimation(BaseQuickAdapter.SCALEIN)
        commonAdapter!!.isFirstOnly(false)
        commonAdapter!!.onItemClickListener = this
        commonAdapter!!.bindToRecyclerView(recycler_view)
    }

    override fun initData() {

    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {

    }
}