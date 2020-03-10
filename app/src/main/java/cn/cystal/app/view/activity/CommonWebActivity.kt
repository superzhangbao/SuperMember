package cn.cystal.app.view.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.LinearLayout
import cn.cystal.app.R
import cn.cystal.app.base.BaseActivity
import cn.cystal.app.base.JS_NAME
import cn.cystal.app.event.CloseCurrentPageEvent
import cn.cystal.app.event.WebEvent
import cn.cystal.app.storage.ConfigPreferences
import cn.cystal.app.web.AndroidInterface
import com.gyf.immersionbar.ImmersionBar
import com.just.agentweb.AgentWeb
import com.just.agentweb.AgentWebConfig
import com.just.agentweb.MiddlewareWebChromeBase
import kotlinx.android.synthetic.main.activity_common_web.*
import kotlinx.android.synthetic.main.common_toolbar.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.reflect.Method


/**
 * web页面
 */
class CommonWebActivity : BaseActivity() {
    private var mAgentWeb: AgentWeb? = null
    private var mMiddleWareWebChrome: MiddlewareWebChromeBase? = null

    override fun setContentView(): Int {
        return R.layout.activity_common_web
    }

    override fun init(savedInstanceState: Bundle?) {
        initToolBar()
    }

    private fun initToolBar() {
        ImmersionBar.with(this)
            .statusBarDarkFont(true)
            .keyboardEnable(true)
            .init()
        tb_toolbar.title = ""
        setSupportActionBar(tb_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)//左侧添加一个默认的返回图标
        supportActionBar?.setHomeButtonEnabled(true) //设置返回键可用
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                finish()
            }
            R.id.item_refresh -> {
                mAgentWeb?.urlLoader?.reload()
            }
        }
        return true
    }

    @Subscribe(threadMode = ThreadMode.POSTING, sticky = true)
    fun onWebEvent(webEvent: WebEvent) {
        val url =
            if (webEvent.url.contains("?")) {
                if (webEvent.url.contains("inType=app")) {
                    "${webEvent.url}&token=${ConfigPreferences.instance.getToken()}&phone=${ConfigPreferences.instance.getPhone()}"
                } else {
                    "${webEvent.url}&token=${ConfigPreferences.instance.getToken()}&inType=app&phone=${ConfigPreferences.instance.getPhone()}"
                }
            } else {
                if (webEvent.url.contains("inType=app")) {
                    "${webEvent.url}?token=${ConfigPreferences.instance.getToken()}&phone=${ConfigPreferences.instance.getPhone()}"
                } else {
                    "${webEvent.url}?token=${ConfigPreferences.instance.getToken()}&inType=app&phone=${ConfigPreferences.instance.getPhone()}"
                }
            }
        initAgentWeb(url)
    }

    private fun initAgentWeb(url: String) {
        AgentWebConfig.clearDiskCache(this)
        AgentWebConfig.removeAllCookies()
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
        mAgentWeb!!.jsInterfaceHolder.addJavaObject(JS_NAME, AndroidInterface(mAgentWeb!!, this))
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
        } else {
            tv_title.text = title
        }
    }

    //显示toolbar menu的图标（在图标是never的模式下）
    override fun onMenuOpened(featureId: Int, menu: Menu?): Boolean {
        if (menu != null) {
            if (menu.javaClass.simpleName.equals("MenuBuilder", ignoreCase = true)) {
                try {
                    val method: Method =
                        menu.javaClass.getDeclaredMethod("setOptionalIconsVisible", java.lang.Boolean.TYPE)
                    method.isAccessible = true
                    method.invoke(menu, true)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return super.onMenuOpened(featureId, menu)
    }

    //收到H5端关闭页面的消息
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onCloseCurrentPageEvent(closeCurrentPageEvent: CloseCurrentPageEvent) {
        finish()
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
