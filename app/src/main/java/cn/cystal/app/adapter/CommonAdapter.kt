package cn.cystal.app.adapter

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import cn.cystal.app.bean.BoutiqueSaleBean
import cn.cystal.app.R

/**
 * author : zhangbao
 * date : 2020-02-11 17:39
 * description :
 */
class CommonAdapter(data: MutableList<BoutiqueSaleBean.DataBean>?):
    BaseQuickAdapter<BoutiqueSaleBean.DataBean, BaseViewHolder>(R.layout.item_common,data){
    override fun convert(helper: BaseViewHolder, item: BoutiqueSaleBean.DataBean?) {
        with(helper) {
            setText(R.id.tv_title, data[adapterPosition].name)
            val imageView = getView(R.id.iv) as ImageView
            Glide.with(mContext)
                .load(data[adapterPosition].icon)
                .into(imageView)
        }
    }
}