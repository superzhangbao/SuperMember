package com.xishitong.supermember.view.activity

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.LinearLayout
import com.gyf.immersionbar.ImmersionBar
import com.just.agentweb.AgentWeb
import com.just.agentweb.AgentWebConfig
import com.just.agentweb.MiddlewareWebChromeBase
import com.xishitong.supermember.R
import com.xishitong.supermember.base.BaseActivity
import com.xishitong.supermember.event.WebEvent
import kotlinx.android.synthetic.main.activity_common_web.*
import kotlinx.android.synthetic.main.common_toolbar.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


/**
 * web页面
 */
class CommonWebActivity : BaseActivity(), View.OnClickListener {
    private var mAgentWeb: AgentWeb? = null
    private var mMiddleWareWebChrome: MiddlewareWebChromeBase? = null

    override fun setContentView(): Int {
        return R.layout.activity_common_web
    }

    override fun init(savedInstanceState: Bundle?) {
        initToolBar()
    }

    private fun initToolBar() {
        v_state_bar.setBackgroundColor(Color.WHITE)
        ImmersionBar.with(this).statusBarDarkFont(true).init()
        rl_toobar.setBackgroundColor(Color.WHITE)
        fl_back.visibility = View.VISIBLE
        tv_title.setTextColor(resources.getColor(R.color.color_333333))
        fl_back.setOnClickListener(this)
    }

    @Subscribe(threadMode = ThreadMode.POSTING, sticky = true)
    fun onWebEvent(webEvent: WebEvent) {
        val url = if (webEvent.token == null) {
            webEvent.url
        } else {
            if (webEvent.url.contains("?")) {
                "${webEvent.url}&token=${webEvent.token}"
            } else {
                "${webEvent.url}?token=${webEvent.token}"
            }
        }
        initAgentWeb(url)
    }

    private fun initAgentWeb(url: String) {
        AgentWebConfig.clearDiskCache(this)
        mAgentWeb = AgentWeb.with(this)
            .setAgentWebParent(
                container, LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            )
            .useDefaultIndicator()
            .setMainFrameErrorView(R.layout.webview_error, R.id.tv_reload)
            .useMiddlewareWebChrome(getMiddleWareWebChrome())
            .createAgentWeb()
            .ready()
            .go(url)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fl_back -> {
                finish()
            }
        }
    }

    private fun getMiddleWareWebChrome(): MiddlewareWebChromeBase {
        return object : MiddlewareWebChromeBase() {
            override fun onReceivedTitle(view: WebView, title: String) {
                super.onReceivedTitle(view, title)
                setTitle(view, title)
            }
        }.also { this.mMiddleWareWebChrome = it }
    }

    @SuppressLint("SetTextI18n")
    private fun setTitle(view: WebView, title: String) {
        if (!TextUtils.isEmpty(title) && title.length > 10) {
            tv_title.text = "${title.substring(0, 10)}..."
        }else{
            tv_title.text = title
        }
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return if (mAgentWeb?.handleKeyEvent(keyCode, event)!!) {
            true
        } else super.onKeyDown(keyCode, event)
    }

    override fun onPause() {
        mAgentWeb!!.webLifeCycle.onPause()
        super.onPause()
    }

    override fun onResume() {
        mAgentWeb?.webLifeCycle?.onResume()
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        mAgentWeb?.webLifeCycle?.onDestroy()
    }
}
