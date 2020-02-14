package com.xishitong.supermember.view.fragment

import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.just.agentweb.AgentWeb
import com.just.agentweb.AgentWebConfig
import com.xishitong.supermember.R
import com.xishitong.supermember.base.BaseFragment
import com.xishitong.supermember.base.SPECIAL_SALE
import kotlinx.android.synthetic.main.fragment_specialsale.*


/**
 *  author : zhangbao
 *  date : 2020-02-07 17:59
 *  description :特卖fragment
 */
class SpecialsaleFragment:BaseFragment(),FragmentKeyDown {

    private var mAgentWeb:AgentWeb? = null
    override fun setContentView(): Int {
        return R.layout.fragment_specialsale
    }

    override fun initView(view: View) {
        AgentWebConfig.clearDiskCache(activity)
        mAgentWeb = AgentWeb.with(this)
            .setAgentWebParent(container, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
            .useDefaultIndicator()
            .setMainFrameErrorView(R.layout.webview_error,R.id.tv_reload)
            .createAgentWeb()
            .ready()
            .go(SPECIAL_SALE)
    }

    override fun initData() {

    }

    override fun onFragmentKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return if (mAgentWeb == null) false else mAgentWeb!!.handleKeyEvent(keyCode, event)
    }
}