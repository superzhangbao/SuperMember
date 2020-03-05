package cn.cystal.app.web

import android.content.Context
import android.webkit.JavascriptInterface
import com.just.agentweb.AgentWeb
import cn.cystal.app.event.CloseCurrentPageEvent
import cn.cystal.app.event.GoToDetailEvent
import cn.cystal.app.event.LogoutEvent
import cn.cystal.app.util.ToastUtils
import org.greenrobot.eventbus.EventBus

/**
 * author : zhangbao
 * date : 2020-02-17 16:40
 * description :
 */
class AndroidInterface(private val agent: AgentWeb, private val context: Context?) {
    @JavascriptInterface
    fun logout(needToken: Boolean?) {
        ToastUtils.showToast("logout")
    }

    @JavascriptInterface
    fun goToDetail(url: String?) {
        EventBus.getDefault().post(GoToDetailEvent(url))
    }

    @JavascriptInterface
    fun toLogin() {
        EventBus.getDefault().post(LogoutEvent())
    }

    @JavascriptInterface
    fun closeCurrentPage(){
        EventBus.getDefault().post(CloseCurrentPageEvent())
    }
}