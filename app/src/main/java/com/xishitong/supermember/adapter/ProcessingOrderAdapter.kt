package com.xishitong.supermember.adapter

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.xishitong.supermember.R
import com.xishitong.supermember.bean.OrderBean

/**
 * author : zhangbao
 * date : 2020-02-12 00:53
 * description :处理中订单数据适配器
 */
class ProcessingOrderAdapter(data: MutableList<OrderBean.DataBean.ListBean>?):
    BaseQuickAdapter<OrderBean.DataBean.ListBean, BaseViewHolder>(R.layout.item_order_processing,data){
    override fun convert(helper: BaseViewHolder, item: OrderBean.DataBean.ListBean?) {
        with(helper) {
            addOnClickListener(R.id.tv_apply_invoice)
            //时间
            setText(R.id.tv_date,data[adapterPosition].addTime)
            val orderStatus = when(data[adapterPosition].status) {
                1->{ "待支付" }
                2->{"待提交凭证" }
                3->{"待确认到账"}
                4->{"完成"}
                5->{"失败"}
                6->{"充值中"}
                7->{"充值成功"}
                8->{"充值失败"}
                9->{"待发货"}
                10->{"已发货"}
                99->{"订单关闭"}
                else -> ""
            }
            //订单状态
            setText(R.id.tv_order_state,orderStatus)
            //产品名称
            setText(R.id.tv_goods_name,data[adapterPosition].productName)
            //会员卡号
            setText(R.id.tv_card_number,"会员卡号:${data[adapterPosition].bankCard}")
            //订单编号
            setText(R.id.tv_order_number,"订单编号:${data[adapterPosition].billId}")
            //积分
            setText(R.id.tv_integral,"获赠积分：${data[adapterPosition].amount}")
            //
            setText(R.id.tv_one_amount_left,"${data[adapterPosition].productValue}.")
            setText(R.id.tv_one_amount_right,"33")
            setText(R.id.tv_all_amount_left,"${data[adapterPosition].amount}.")
            setText(R.id.tv_all_amount_right,"33")
            val imageView = getView(R.id.iv_goods_img) as ImageView
            Glide.with(mContext)
                .load(data[adapterPosition].productImg)
                .into(imageView)
        }
    }

}