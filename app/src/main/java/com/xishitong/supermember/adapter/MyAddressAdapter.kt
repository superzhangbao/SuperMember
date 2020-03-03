package com.xishitong.supermember.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.xishitong.supermember.R
import com.xishitong.supermember.bean.MyAddressBean
import com.xishitong.supermember.util.LogUtil

/**
 * author : zhangbao
 * date : 2020-02-10 12:49
 * description :我的地址的列表的adapter
 */
class MyAddressAdapter(data: MutableList<MyAddressBean.DataBean.ListBean>?):
    BaseQuickAdapter<MyAddressBean.DataBean.ListBean,BaseViewHolder>(R.layout.item_myaddress,data) {
    override fun convert(helper: BaseViewHolder, item: MyAddressBean.DataBean.ListBean?) {
        with(helper) {
            setText(R.id.tv_name,data[adapterPosition].name)
            setText(R.id.tv_phone,data[adapterPosition].phone)
            setText(R.id.tv_address,"${data[adapterPosition].gegion} ${data[adapterPosition].detailed}")
        }
    }
}