package cn.cystal.app.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import cn.cystal.app.bean.BalanceBean
import cn.cystal.app.R

/**
 *  author : zhangbao
 *  date : 2020-02-10 21:31
 *  description : 会费明细列表数据适配器
 */
class DetailAllAdapter(data: List<BalanceBean.DataBean.ListBean>) :
    BaseQuickAdapter<BalanceBean.DataBean.ListBean, BaseViewHolder>(R.layout.item_detail_all, data) {
    override fun convert(helper: BaseViewHolder, item: BalanceBean.DataBean.ListBean?) {
        with(helper) {
            addOnClickListener(R.id.tv_all)
            setText(R.id.tv_product_name, data[adapterPosition].productName)
            setText(R.id.tv_time, "时间:" + data[adapterPosition].addTime)
            setText(R.id.tv_order_number, "订单编号:" + data[adapterPosition].billId)
            val type = data[adapterPosition].type
            val text =
                if (type == 1) "+¥${data[adapterPosition].money / 100.00}个积分" else "-¥${data[adapterPosition].money / 100.00}个积分"
            setText(R.id.tv_integral, text)
            if (type == 1) {
                val amount = data[adapterPosition].amount
                if (amount == null) {
                    setText(R.id.tv_money, "")
                }else{
                    setText(R.id.tv_money, "（缴纳金额：¥${amount}）")
                }
                if (data[adapterPosition].status == 0) {
                    setVisible(R.id.tv_all, true)
                    setText(R.id.tv_all, "申请发票")
                } else if (data[adapterPosition].status == 1) {
                    setVisible(R.id.tv_all, true)
                    if (data[adapterPosition].courierNumber != null) {
                        setText(R.id.tv_all, "发票快递单号")
                    } else {
                        setText(R.id.tv_all, "发票申请中")
                    }
                }else{
                    setVisible(R.id.tv_all, false)
                }
            } else {
                setText(R.id.tv_money, "")
                setVisible(R.id.tv_all, false)
            }
        }
    }
}