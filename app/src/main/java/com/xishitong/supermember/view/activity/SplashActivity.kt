package com.xishitong.supermember.view.activity

import android.content.Intent
import android.os.Bundle
import com.gyf.immersionbar.ImmersionBar
import com.trello.rxlifecycle2.android.ActivityEvent
import com.xishitong.supermember.R
import com.xishitong.supermember.base.BaseActivity
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

class SplashActivity : BaseActivity() {

    override fun setContentView(): Int {
        return R.layout.activity_splash
    }

    override fun init(savedInstanceState: Bundle?) {
        ImmersionBar.with(this).init()
        Observable.just(1)
            .delay(3,TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .compose(bindUntilEvent(ActivityEvent.DESTROY))
            .doOnNext {
                startActivity(Intent(this,MainActivity::class.java))
                finish()
            }.subscribe()
    }
}
