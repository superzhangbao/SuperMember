package com.xishitong.supermember.view.activity

import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.view.View
import android.widget.LinearLayout
import com.gyf.immersionbar.ImmersionBar
import com.xishitong.supermember.R
import com.xishitong.supermember.adapter.DetailOfMembershipAdapter
import com.xishitong.supermember.base.App
import com.xishitong.supermember.base.BaseActivity
import com.xishitong.supermember.base.DETAIL_TYPE
import com.xishitong.supermember.util.LogUtil
import com.xishitong.supermember.util.UiUtils
import com.xishitong.supermember.view.fragment.DetailAllFragment
import kotlinx.android.synthetic.main.activity_detail_of_membership.*
import kotlinx.android.synthetic.main.common_toolbar.*
import java.lang.reflect.Field


/**
 * 会费明细activity
 */
class DetailOfMembershipActivity : BaseActivity(), TabLayout.BaseOnTabSelectedListener<TabLayout.Tab>,
    View.OnClickListener {

    private val fragments: ArrayList<Fragment> = ArrayList()
    override fun setContentView(): Int {
            return R.layout.activity_detail_of_membership
    }

    override fun init(savedInstanceState: Bundle?) {
        initToolBar()
        val (detailAllFragment1, detailAllFragment2, detailAllFragment3) = initFragment()
        initTabLayoutAndViewPager(detailAllFragment1, detailAllFragment2, detailAllFragment3)
    }

    private fun initToolBar() {
        v_state_bar.setBackgroundColor(Color.WHITE)
        ImmersionBar.with(this)
            .statusBarDarkFont(true)
            .init()
        rl_toobar.setBackgroundColor(Color.WHITE)
        fl_back.visibility = View.VISIBLE
        tv_title.text = getString(R.string.details_of_membership)
        tv_title.setTextColor(resources.getColor(R.color.color_333333))
        fl_back.setOnClickListener(this)
    }

    private fun initFragment(): Triple<DetailAllFragment, DetailAllFragment, DetailAllFragment> {
        val detailAllFragment1 = DetailAllFragment()
        val bundle1 = Bundle()
        bundle1.putString(DETAIL_TYPE, "0")
        detailAllFragment1.arguments = bundle1

        val detailAllFragment2 = DetailAllFragment()
        val bundle2 = Bundle()
        bundle2.putString(DETAIL_TYPE, "1")
        detailAllFragment2.arguments = bundle2

        val detailAllFragment3 = DetailAllFragment()
        val bundle3 = Bundle()
        bundle3.putString(DETAIL_TYPE, "2")
        detailAllFragment3.arguments = bundle3
        return Triple(detailAllFragment1, detailAllFragment2, detailAllFragment3)
    }

    private fun initTabLayoutAndViewPager(detailAllFragment1: DetailAllFragment,
                                          detailAllFragment2: DetailAllFragment,
                                          detailAllFragment3: DetailAllFragment) {
        fragments.add(detailAllFragment1)
        fragments.add(detailAllFragment2)
        fragments.add(detailAllFragment3)

        view_pager.adapter = DetailOfMembershipAdapter(supportFragmentManager, fragments)
        tablayout.addOnTabSelectedListener(this)
        setTabLayoutIndicatorWidth(20.0f)
        tablayout.setupWithViewPager(view_pager)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.fl_back->{
                finish()
            }
        }
    }

    override fun onTabReselected(p0: TabLayout.Tab?) {

    }

    override fun onTabUnselected(p0: TabLayout.Tab?) {

    }

    override fun onTabSelected(p0: TabLayout.Tab?) {
        LogUtil.e("onTabSelected${p0!!.position}")
    }

    /**
     * 设置tablayout的下划线的宽度
     * @param width
     */
    private fun setTabLayoutIndicatorWidth(width: Float) {
        tablayout.post {
            try { //拿到tabLayout的mTabStrip属性
                val mTabStrip = tablayout.getChildAt(0) as LinearLayout
                //这里是要设置的宽度(dp)
                val dp: Int = UiUtils.dip2px(App.getInstance(), width)
                for (i in 0 until mTabStrip.childCount) {
                    val tabView: View = mTabStrip.getChildAt(i)
                    //拿到tabView的mTextView属性
                    val mTextViewField: Field = tabView::class.java.getDeclaredField("textView")
                    mTextViewField.isAccessible = true
                    tabView.setPadding(0, 0, 0, 0)
                    //设置tab左右间距为10dp  注意这里不能使用Padding 因为源码中线的宽度是根据 tabView的宽度来设置的
                    val params = tabView.layoutParams as LinearLayout.LayoutParams
                    params.width = width.toInt()
                    params.leftMargin = dp
                    params.rightMargin = dp
                    tabView.layoutParams = params
                    tabView.invalidate()
                }
            } catch (e: NoSuchFieldException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
        }
    }
}
