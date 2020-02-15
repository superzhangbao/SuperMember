package com.xishitong.supermember.adapter

import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.TextView
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
class OrderAdapter(data: MutableList<OrderBean.DataBean.ListBean>?) :
    BaseQuickAdapter<OrderBean.DataBean.ListBean, BaseViewHolder>(R.layout.item_order_processing, data) {
    override fun convert(helper: BaseViewHolder, item: OrderBean.DataBean.ListBean?) {
        with(helper) {
            val listBean = data[adapterPosition]
            val orderType = listBean.orderType
            //时间
            setText(R.id.tv_date, listBean.addTime)
            val orderStatus = when (listBean.status) {
                1 -> {
                    "待支付"
                }
                2 -> {
                    "待提交凭证"
                }
                3 -> {
                    "待确认到账"
                }
                4 -> {
                    "已完成"
                }
                5 -> {
                    "失败"
                }
                6 -> {
                    "充值中"
                }
                7 -> {
                    "充值成功"
                }
                8 -> {
                    "退款中"
                }
                9 -> {
                    "待发货"
                }
                10 -> {
                    "已发货"
                }
                88 -> {
                    "退款成功"
                }
                99 -> {
                    "订单关闭"
                }
                else -> ""
            }
            //订单状态
            setText(R.id.tv_order_state, orderStatus)
            //产品名称
            setText(R.id.tv_goods_name, listBean.productName)
            //会员卡号
            //item.orderType  1 权益  2其他  3 特卖
            setText(
                R.id.tv_card_number, when (orderType) {
                    3 -> {
                        "商品备注:${listBean.saleRemark ?: "无"}"
                    }
                    1 -> {
                        "会员号码:${listBean.account}"
                    }
                    else -> {
                        "充值号码:${listBean.account}"
                    }
                }
            )
            //订单编号
            setText(R.id.tv_order_number, "订单编号:${listBean.billId}")
            //积分
            setText(R.id.tv_integral, "获赠积分：${listBean.amount}")
            //商品数量
            setText(R.id.tv_count, "x${listBean.buyNum}")

            setText(R.id.tv_one_amount_left, "¥${(listBean.originalAmount / 100.0).toString().split(".")[0]}.")
            setText(R.id.tv_one_amount_right, (listBean.originalAmount / 100.0).toString().split(".")[1])
            setText(R.id.tv_all_goods_count, "共${listBean.buyNum}件商品,实付款:")
            setText(
                R.id.tv_all_amount_left,
                "¥${(listBean.amount * listBean.buyNum / 100.0).toString().split(".")[0]}."
            )
            setText(R.id.tv_all_amount_right, (listBean.amount * listBean.buyNum / 100.0).toString().split(".")[1])
            val imageView = getView(R.id.iv_goods_img) as ImageView
            Glide.with(mContext)
                .load(listBean.productImg)
                .into(imageView)

            //按钮部分
            when (listBean.status) {
                5 -> {
                    val tvBtn1 = getView(R.id.tv_fail_reason) as TextView
                    tvBtn1.visibility = View.VISIBLE
                    addOnClickListener(R.id.tv_fail_reason)
                    if (listBean.processStatus != "cw" && orderType == 1) {
                        val tvBtn2 = getView(R.id.tv_voucher) as TextView
                        tvBtn2.visibility = View.VISIBLE
                        tvBtn2.text = "查看凭证"
                        addOnClickListener(R.id.tv_voucher)
                    }
                }
                2 -> {
                    val tvBtn1 = getView(R.id.tv_voucher) as TextView
                    tvBtn1.visibility = View.VISIBLE
                    tvBtn1.text = "上传凭证"
                    addOnClickListener(R.id.tv_voucher)
                }
                3 -> {
                    if (orderType == 1) {
                        if (listBean.processStatus == "cw") {
                            val tvBtn1 = getView(R.id.tv_voucher) as TextView
                            tvBtn1.visibility = View.VISIBLE
                            tvBtn1.text = "查看/修改凭证"
                            addOnClickListener(R.id.tv_voucher)
                        } else {
                            val tvBtn1 = getView(R.id.tv_voucher) as TextView
                            tvBtn1.visibility = View.VISIBLE
                            tvBtn1.text = "查看凭证"
                            addOnClickListener(R.id.tv_voucher)
                        }
                    }
                }
                99 -> {
                    if (orderType == 1 && listBean.processStatus != "cw") {
                        val tvBtn1 = getView(R.id.tv_voucher) as TextView
                        tvBtn1.visibility = View.VISIBLE
                        tvBtn1.text = "查看凭证"
                        addOnClickListener(R.id.tv_voucher)
                    }
                }
            }
            if (listBean.courierNumber != null) {//快递单号
                val tvBtn1 = getView(R.id.tv_courier_number) as TextView
                tvBtn1.visibility = View.VISIBLE
                addOnClickListener(R.id.tv_courier_number)
            }
            if (orderType == 3) {//收货地址
                val tvBtn1 = getView(R.id.tv_receice_address) as TextView
                tvBtn1.visibility = View.VISIBLE
                addOnClickListener(R.id.tv_receice_address)
            }
            if (listBean.payType == "pos") {//订单二维码
                val tvBtn1 = getView(R.id.tv_order_qrcode) as TextView
                tvBtn1.visibility = View.VISIBLE
                addOnClickListener(R.id.tv_order_qrcode)
            }
        }
    }
}