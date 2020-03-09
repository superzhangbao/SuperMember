package cn.cystal.app.adapter

import android.graphics.Paint
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import cn.cystal.app.bean.SaleBean
import cn.cystal.app.R
import cn.cystal.app.util.UtilsBigDecimal
import java.math.BigDecimal

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
            setText(R.id.tv_original_amount,"¥${UtilsBigDecimal.formatToNumber(BigDecimal(UtilsBigDecimal.div(data[adapterPosition].originalAmount.toDouble(),100.0)))}")
            setText(R.id.tv_market_amount,"¥${UtilsBigDecimal.formatToNumber(BigDecimal(UtilsBigDecimal.div(data[adapterPosition].marketAmount.toDouble(),100.0)))}")

            val imageView = getView(R.id.iv_goods_img) as ImageView
            Glide.with(mContext)
                .load(data[adapterPosition].headImgList[0])
                .into(imageView)
        }
    }
}