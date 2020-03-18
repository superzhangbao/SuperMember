package cn.cystal.app.view.activity

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.ViewTreeObserver
import cn.cystal.app.R
import cn.cystal.app.base.App
import cn.cystal.app.base.BaseActivity
import cn.cystal.app.util.LogUtil
import cn.cystal.app.util.UiUtils
import com.gyf.immersionbar.ImmersionBar
import com.trello.rxlifecycle2.android.ActivityEvent
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.android.synthetic.main.dialog_courier_number.*
import java.util.concurrent.TimeUnit

class SplashActivity : BaseActivity(), ViewTreeObserver.OnGlobalLayoutListener, Animator.AnimatorListener {
    private var animator: ObjectAnimator? = null
    private var animator1: ObjectAnimator? = null
    private var viewTreeObserver: ViewTreeObserver? = null

    override fun setContentView(): Int {
        setTheme(R.style.AppTheme)
        return R.layout.activity_splash
    }

    override fun init(savedInstanceState: Bundle?) {
        ImmersionBar.with(this).init()
        viewTreeObserver = iv_logo.viewTreeObserver
        viewTreeObserver!!.addOnGlobalLayoutListener(this)
    }

    override fun onGlobalLayout() {
        iv_logo.viewTreeObserver.removeOnGlobalLayoutListener(this)
        val height = iv_logo.height
        val displayHeight = UiUtils.getDisplayHeight(App.getInstance())
        val display = (((displayHeight - height) / 2) - UiUtils.dip2px(App.getInstance(), 255f)).toFloat()
        animator = ObjectAnimator.ofFloat(iv_logo, "translationY", 0f, -display)
        animator!!.duration = 500
        animator!!.addListener(this)
        animator!!.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        animator?.let {
            animator!!.cancel()
        }
        animator1?.let {
            animator1!!.cancel()
        }
    }

    override fun onAnimationEnd(animation: Animator?) {
        Observable.just(1)
            .delay(1, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .compose(bindUntilEvent(ActivityEvent.DESTROY))
            .doOnNext {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }.subscribe()
    }

    override fun onAnimationRepeat(animation: Animator?) {
    }

    override fun onAnimationCancel(animation: Animator?) {
    }

    override fun onAnimationStart(animation: Animator?) {
    }
}
