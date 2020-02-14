package com.xishitong.supermember.base

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import com.xishitong.supermember.event.LogoutEvent
import com.xishitong.supermember.storage.ConfigPreferences
import com.xishitong.supermember.view.activity.LoginActivity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 *  author : zhangbao
 *  date : 2020-02-07 11:19
 *  description :
 */
abstract class BaseActivity : RxAppCompatActivity() {
    companion object{
        val TAG: String = this.javaClass.simpleName
    }
    var mAppManager: AppManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 将当前Activity加入栈管理
        mAppManager = AppManager.SINGLETON
        mAppManager?.addActivity(this)
        if (!isTaskRoot) {
            val intent = intent
            val action = intent.action
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && action != null && action == Intent.ACTION_MAIN) {
                mAppManager?.finishActivity(this)
                return
            }
        }
        setContentView(View.inflate(this, setContentView(), null))
        init(savedInstanceState)
        EventBus.getDefault().register(this)
    }



    abstract fun setContentView(): Int

    abstract fun init(savedInstanceState: Bundle?)

    @Subscribe(threadMode = ThreadMode.POSTING)
    fun onLogoutEvent(logoutEvent: LogoutEvent) {
        ConfigPreferences.instance.setLoginState(false)
        ConfigPreferences.instance.setToken("")
        ConfigPreferences.instance.setIsMember(false)
        ConfigPreferences.instance.setPhone("")
        startActivity(Intent(this,LoginActivity::class.java))
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}