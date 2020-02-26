package com.xishitong.supermember.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.xishitong.supermember.R
import com.xishitong.supermember.bean.BoutiqueSaleBean

/**
 * author : zhangbao
 * date : 2020-02-14 16:43
 * description :
 */
class SearchAdapter(data: List<BoutiqueSaleBean.DataBean>?):
    BaseQuickAdapter<BoutiqueSaleBean.DataBean, BaseViewHolder>(R.layout.item_search,data) {
    override fun convert(helper: BaseViewHolder, item: BoutiqueSaleBean.DataBean?) {
        with(helper) {
            setText(R.id.tv,data[adapterPosition].name)
        }
    }
}