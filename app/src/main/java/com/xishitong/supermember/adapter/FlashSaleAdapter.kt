package com.xishitong.supermember.adapter

import android.graphics.Paint
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.xishitong.supermember.R
import com.xishitong.supermember.bean.SaleBean

/**
 * author : zhangbao
 * date : 2020-02-13 23:04
 * description :限时秒杀数据适配器
 */
class FlashSaleAdapter(data: MutableList<SaleBean.DataBean.ListBean>?) :
    BaseQuickAdapter<SaleBean.DataBean.ListBean, BaseViewHolder>(R.layout.item_flash_sale, data) {
    override fun convert(helper: BaseViewHolder, item: SaleBean.DataBean.ListBean?) {
        with(helper) {
            val textView = getView<TextView>(R.id.tv_market_amount)
            textView.paint.flags = Paint. STRIKE_THRU_TEXT_FLAG
            setText(R.id.tv_original_amount,"¥${data[adapterPosition].originalAmount}")
            setText(R.id.tv_market_amount,"¥${data[adapterPosition].marketAmount}")

//            val imageView = getView(R.id.iv_goods_img) as ImageView
//            Glide.with(mContext)
//                .load(data[adapterPosition].headImgList[0])
//                .into(imageView)
        }
    }
}