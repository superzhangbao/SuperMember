package com.xishitong.supermember.view.fragment

import android.content.Intent
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.just.agentweb.AgentWeb
import com.just.agentweb.AgentWebConfig
import com.xishitong.supermember.R
import com.xishitong.supermember.base.BaseFragment
import com.xishitong.supermember.base.SPECIAL_SALE
import com.xishitong.supermember.base.VIP_AGREEMENT
import com.xishitong.supermember.event.GoToDetailEvent
import com.xishitong.supermember.event.WebEvent
import com.xishitong.supermember.storage.ConfigPreferences
import com.xishitong.supermember.view.activity.CommonWebActivity
import com.xishitong.supermember.web.AndroidInterface
import com.xishitong.supermember.web.FragmentKeyDown
import kotlinx.android.synthetic.main.fragment_specialsale.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


/**
 *  author : zhangbao
 *  date : 2020-02-07 17:59
 *  description :特卖fragment
 */
class SpecialsaleFragment:BaseFragment(), FragmentKeyDown {

    private var mAgentWeb:AgentWeb? = null
    override fun setContentView(): Int {
        return R.layout.fragment_specialsale
    }

    override fun initView(view: View) {
        EventBus.getDefault().register(this)
        AgentWebConfig.clearDiskCache(activity)
        AgentWebConfig.removeAllCookies()
        mAgentWeb = AgentWeb.with(this)
            .setAgentWebParent(container, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
            .useDefaultIndicator()
            .setMainFrameErrorView(R.layout.webview_error,R.id.tv_reload)
            .createAgentWeb()
            .ready()
            .go(SPECIAL_SALE+"?token=${ConfigPreferences.instance.getToken()}&inType=app")
        mAgentWeb!!.jsInterfaceHolder.addJavaObject("android", AndroidInterface(mAgentWeb!!, activity))
    }

    override fun initData() {

    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    fun onGoToDetailEvent(goToDetailEvent: GoToDetailEvent) {
        EventBus.getDefault().postSticky(WebEvent(goToDetailEvent.url, if (goToDetailEvent.needToken) ConfigPreferences.instance.getToken() else null))
        val intent = Intent(activity, CommonWebActivity::class.java)
        startActivity(intent)
    }

    override fun onFragmentKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return if (mAgentWeb == null) false else mAgentWeb!!.handleKeyEvent(keyCode, event)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
    }
}