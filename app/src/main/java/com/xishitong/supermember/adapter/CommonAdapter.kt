package com.xishitong.supermember.adapter

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.xishitong.supermember.R
import com.xishitong.supermember.bean.CommonBean

/**
 * author : zhangbao
 * date : 2020-02-11 17:39
 * description :
 */
class CommonAdapter(data: MutableList<CommonBean.DataBean.ListBean>?):
    BaseQuickAdapter<CommonBean.DataBean.ListBean, BaseViewHolder>(R.layout.item_common,data){
    override fun convert(helper: BaseViewHolder, item: CommonBean.DataBean.ListBean?) {
        with(helper) {
            setText(R.id.tv_title, data[adapterPosition].title)
            val imageView = getView(R.id.iv) as ImageView
            Glide.with(mContext)
                .load(data[adapterPosition].imgUrl)
                .into(imageView)
        }
    }
}