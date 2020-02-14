package com.xishitong.supermember.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.xishitong.supermember.R
import com.xishitong.supermember.util.LogUtil

/**
 * author : zhangbao
 * date : 2020-02-14 16:43
 * description :
 */
class SearchAdapter(data: List<String>?):
    BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_search,data) {
    override fun convert(helper: BaseViewHolder, item: String?) {
        with(helper) {
            LogUtil.e(TAG, data[adapterPosition])
            setText(R.id.tv,data[adapterPosition])
        }
    }
}