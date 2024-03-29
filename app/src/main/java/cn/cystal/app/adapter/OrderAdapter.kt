package cn.cystal.app.adapter

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import cn.cystal.app.bean.OrderBean
import cn.cystal.app.util.LogUtil
import cn.cystal.app.R
import cn.cystal.app.util.UtilsBigDecimal
import java.math.BigDecimal

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
            LogUtil.e(TAG, listBean.toString())
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
            if (listBean.status == 5 || listBean.status == 99) {
                setTextColor(R.id.tv_order_state, mContext.resources.getColor(R.color.color_999999))
            } else {
                setTextColor(R.id.tv_order_state, mContext.resources.getColor(R.color.color_6BB467))
            }
            //产品名称
            setText(R.id.tv_goods_name, listBean.productName)
            //会员卡号
            //item.orderType  1 权益  2其他  3 特卖
            setText(
                R.id.tv_content1, when (orderType) {
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
            setText(R.id.tv_content2, "订单编号:${listBean.billId}")
            if (orderType == 1) {
                //获赠积分
                setText(
                    R.id.tv_content3,
                    "获赠积分:${UtilsBigDecimal.formatToNumber(BigDecimal(UtilsBigDecimal.div(listBean.productValue.toDouble(),100.0)))}"
                )
                setVisible(R.id.tv_content3, true)
            } else {
                if (listBean.deductAmount != 0) {
                    //积分抵扣
                    setText(R.id.tv_content3, "积分抵扣:¥${listBean.deductAmount / 100.0}")
                    setVisible(R.id.tv_content3, true)
                } else {
                    setGone(R.id.tv_content3, false)
                }
            }

            //商品数量
            setText(R.id.tv_count, "x${listBean.buyNum}")

//            val amount = UtilsBigDecimal.formatToNumber(BigDecimal(UtilsBigDecimal.div(listBean.originalAmount.toDouble(),100.0)))
            val amount = UtilsBigDecimal.formatToNumber(BigDecimal(UtilsBigDecimal.div(((listBean.amount/listBean.buyNum).toDouble()),100.0)))
            val amountSplit = amount?.split(".")
            setText(R.id.tv_one_amount_left, "¥${amountSplit?.get(0) ?: 0}.")
            setText(R.id.tv_one_amount_right, amountSplit?.get(1) ?: "00")
            setText(R.id.tv_all_goods_count, "共${listBean.buyNum}件商品,实付款:")

            val allAmount = UtilsBigDecimal.formatToNumber(
                BigDecimal(
//                    UtilsBigDecimal.div(
//                        (listBean.amount * listBean.buyNum).toDouble(),
//                        100.0
//                    )
                    UtilsBigDecimal.div((listBean.amount).toDouble(), 100.0)
                )
            )
            val allAmountSplit = allAmount?.split(".")
            setText(R.id.tv_all_amount_left, "¥${allAmountSplit?.get(0) ?: 0}.")
            setText(R.id.tv_all_amount_right, allAmountSplit?.get(1) ?: "00")
            val imageView = getView(R.id.iv_goods_img) as ImageView
            Glide.with(mContext)
                .load(listBean.productImg)
                .into(imageView)

            //按钮部分
            when (listBean.status) {
                5 -> {
                    setVisible(R.id.tv_fail_reason, true)
                    addOnClickListener(R.id.tv_fail_reason)
                    if (listBean.processStatus != "cw" && orderType == 1 && listBean.payType == "bank") {
                        setVisible(R.id.tv_voucher, true)
                        setText(R.id.tv_voucher, "查看凭证")
                        addOnClickListener(R.id.tv_voucher)
                    } else {
                        setGone(R.id.tv_voucher, false)
                    }
                }
                2 -> {
                    setVisible(R.id.tv_voucher, true)
                    setText(R.id.tv_voucher, "上传凭证")
                    addOnClickListener(R.id.tv_voucher)
                    setGone(R.id.tv_fail_reason, false)
                }
                3 -> {
                    if (orderType == 1) {
                        if (listBean.processStatus == "cw") {
                            setVisible(R.id.tv_voucher, true)
                            addOnClickListener(R.id.tv_voucher)
                            setText(R.id.tv_voucher, "查看/修改凭证")
                        } else {
                            if (listBean.payType == "bank") {
                                setVisible(R.id.tv_voucher, true)
                                addOnClickListener(R.id.tv_voucher)
                                setText(R.id.tv_voucher, "查看凭证")
                            } else {
                                setGone(R.id.tv_voucher, false)
                            }
                        }
                    } else {
                        setGone(R.id.tv_voucher, false)
                    }
                    setGone(R.id.tv_fail_reason, false)
                }
                99 -> {
                    if (orderType == 1 && listBean.processStatus != "cw" && listBean.payType == "bank") {
                        setVisible(R.id.tv_voucher, true)
                        setText(R.id.tv_voucher, "查看凭证")
                        addOnClickListener(R.id.tv_voucher)
                    } else {
                        setGone(R.id.tv_voucher, false)
                    }
                    setGone(R.id.tv_fail_reason, false)
                }
                else -> {
                    setGone(R.id.tv_voucher, false)
                    setGone(R.id.tv_fail_reason, false)
                }
            }
            if (listBean.courierNumber != null) {//快递单号
                setVisible(R.id.tv_courier_number, true)
                addOnClickListener(R.id.tv_courier_number)
            } else {
                setGone(R.id.tv_courier_number, false)
            }
            if (orderType == 3) {//收货地址
                setVisible(R.id.tv_receice_address, true)
                addOnClickListener(R.id.tv_receice_address)
            } else {
                setGone(R.id.tv_receice_address, false)
            }
            if (listBean.payType == "pos") {//订单二维码
                LogUtil.e(TAG, listBean.payType)
                setVisible(R.id.tv_order_qrcode, true)
                addOnClickListener(R.id.tv_order_qrcode)
            } else {
                setGone(R.id.tv_order_qrcode, false)
            }
        }
    }
}