package cn.cystal.app.view.fragment

import android.content.Intent
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.WebStorage
import android.widget.LinearLayout
import cn.cystal.app.R
import cn.cystal.app.base.BaseFragment
import cn.cystal.app.base.JS_NAME
import cn.cystal.app.base.SPECIAL_SALE
import com.just.agentweb.AgentWeb
import com.just.agentweb.AgentWebConfig
import cn.cystal.app.event.GoToDetailEvent
import cn.cystal.app.event.WebEvent
import cn.cystal.app.storage.ConfigPreferences
import cn.cystal.app.util.LogUtil
import cn.cystal.app.view.activity.CommonWebActivity
import cn.cystal.app.web.AndroidInterface
import cn.cystal.app.web.FragmentKeyDown
import kotlinx.android.synthetic.main.fragment_specialsale.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


/**
 *  author : zhangbao
 *  date : 2020-02-07 17:59
 *  description :特卖fragment
 */
class SpecialsaleFragment : BaseFragment(), FragmentKeyDown {

    private var mAgentWeb: AgentWeb? = null
    override fun setContentView(): Int {
        return R.layout.fragment_specialsale
    }

    override fun initView(view: View) {
        EventBus.getDefault().register(this)
    }

    override fun initData() {
        WebStorage.getInstance().deleteAllData() //清空WebView的localStorage
    }

    override fun onResume() {
        super.onResume()
        LogUtil.e(TAG,"onResume")
        AgentWebConfig.clearDiskCache(activity)
        AgentWebConfig.removeAllCookies()
        WebStorage.getInstance().deleteAllData() //清空WebView的localStorage
        if (mAgentWeb != null) {
            mAgentWeb!!.clearWebCache()
            mAgentWeb!!.urlLoader.loadUrl(getUrl())
        }else{
            mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent(
                    container,
                    LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                )
                .useDefaultIndicator()
                .setMainFrameErrorView(R.layout.webview_error, R.id.tv_reload)
                .createAgentWeb()
                .ready()
                .go(getUrl())
            mAgentWeb!!.jsInterfaceHolder.addJavaObject(JS_NAME, AndroidInterface(mAgentWeb!!, activity))
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            WebStorage.getInstance().deleteAllData() //清空WebView的localStorage
            AgentWebConfig.clearDiskCache(activity)
            AgentWebConfig.removeAllCookies()
            LogUtil.e(TAG,"onHiddenChanged")
            mAgentWeb?.clearWebCache()
            mAgentWeb?.urlLoader?.loadUrl(getUrl())
        }
    }

    private fun getUrl(): String {
        return if (SPECIAL_SALE.contains("?")) {
            if (ConfigPreferences.instance.getToken() == "") {
                "${SPECIAL_SALE}&inType=app"
            }else{
                "${SPECIAL_SALE}&token=${ConfigPreferences.instance.getToken()}&inType=app"
            }
        } else {
            if (ConfigPreferences.instance.getToken() == "") {
                "${SPECIAL_SALE}?inType=app"
            }else{
                "${SPECIAL_SALE}?token=${ConfigPreferences.instance.getToken()}&inType=app"
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onGoToDetailEvent(goToDetailEvent: GoToDetailEvent) {
        LogUtil.e(TAG, "url:${goToDetailEvent.url}")
        EventBus.getDefault().postSticky(WebEvent(goToDetailEvent.url))
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