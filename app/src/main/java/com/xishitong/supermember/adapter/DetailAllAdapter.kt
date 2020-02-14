package com.xishitong.supermember.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.xishitong.supermember.R
import com.xishitong.supermember.bean.BalanceBean

/**
 *  author : zhangbao
 *  date : 2020-02-10 21:31
 *  description : 会费明细列表数据适配器
 */
class DetailAllAdapter(data: List<BalanceBean.DataBean.ListBean>):
    BaseQuickAdapter<BalanceBean.DataBean.ListBean, BaseViewHolder>(R.layout.item_detail_all,data){
    override fun convert(helper: BaseViewHolder, item: BalanceBean.DataBean.ListBean?) {
        with(helper) {
            addOnClickListener(R.id.tv_all)
            setText(R.id.tv_product_name,data[adapterPosition].productName)
            setText(R.id.tv_time,"时间:"+data[adapterPosition].addTime)
            setText(R.id.tv_order_number,"订单编号:"+data[adapterPosition].billId)
            setText(R.id.tv_money,"（缴纳金额：¥${data[adapterPosition].money}）")
            val i = (data[adapterPosition].money / 100.00)
            val split = i.toString().split(".")
            val a = if (i>=0) "+" else "-"
            setText(R.id.tv_integral_left,"$a${split[0]}")
            setText(R.id.tv_integral_right,".${split[1]}个积分")
            if (data[adapterPosition].status == 0 && data[adapterPosition].type == 1) {//申请发票
                setText(R.id.tv_all,"申请发票")
                return@with
            }
            if (data[adapterPosition].status == 1) {
                if (data[adapterPosition].courierNumber != null) {
                    setText(R.id.tv_all,"发票快递单号")
                }else{
                    setText(R.id.tv_all,"发票申请中")
                }
            }
        }
    }
}