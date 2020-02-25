package com.xishitong.supermember.web

import android.content.Context
import android.webkit.JavascriptInterface
import com.just.agentweb.AgentWeb
import com.xishitong.supermember.event.GoToDetailEvent
import com.xishitong.supermember.event.LogoutEvent
import com.xishitong.supermember.util.ToastUtils
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
    fun goToDetail(url: String?, needToken: Boolean) {
        EventBus.getDefault().post(GoToDetailEvent(url,true))
    }

    @JavascriptInterface
    fun login() {
        EventBus.getDefault().post(LogoutEvent())
    }
}