package com.xishitong.supermember.view.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.google.gson.Gson
import com.gyf.immersionbar.ImmersionBar
import com.trello.rxlifecycle2.android.ActivityEvent
import com.xishitong.supermember.R
import com.xishitong.supermember.base.BaseActivity
import com.xishitong.supermember.base.PHONE_NUMBER
import com.xishitong.supermember.bean.LoginBean
import com.xishitong.supermember.event.LoginEvent
import com.xishitong.supermember.network.BaseObserver
import com.xishitong.supermember.network.IApiService
import com.xishitong.supermember.network.NetClient
import com.xishitong.supermember.storage.ConfigPreferences
import com.xishitong.supermember.util.LogUtil
import com.xishitong.supermember.util.ToastUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login_verification_code.*
import kotlinx.android.synthetic.main.common_toolbar.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.greenrobot.eventbus.EventBus
import java.util.concurrent.TimeUnit

/**
 *  author : zhangbao
 *  date : 2020-02-08 13:20
 *  description : 输入登录验证码页面
 */
class LoginVerificationCodeActivity : BaseActivity(), View.OnClickListener {

    private var et: Array<EditText>? = null
    private var flag = true
    private var phone:String = ""

    override fun setContentView(): Int {
        return R.layout.activity_login_verification_code
    }

    override fun init(savedInstanceState: Bundle?) {
        //初始化UI
        initTitle()
        initEditText()
        //开始60s倒计时
        timeCountDown()
        phone = intent.getStringExtra(PHONE_NUMBER)
    }

    /**
     * 初始化状态栏
     */
    @SuppressLint("SetTextI18n")
    private fun initTitle() {
        v_state_bar.setBackgroundColor(Color.WHITE)
        ImmersionBar.with(this)
            .statusBarDarkFont(true)
            .init()
        rl_toobar.setBackgroundColor(Color.WHITE)
        fl_back.setOnClickListener(this)
        tv_title.text = getString(R.string.login_title)
        tv_title.setTextColor(resources.getColor(R.color.color_333333))
        tv_send_message_to.text = getString(R.string.sended_message)+intent.getStringExtra(PHONE_NUMBER)
    }

    /**
     * 弹起软键盘
     */
    private fun showSoftInputFromWindow(activity: Activity, et: EditText) {
        et.isFocusable = true
        et.isFocusableInTouchMode = true
        et.requestFocus()
        activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    }

    private fun initEditText() {
        //自动弹起软键盘
        showSoftInputFromWindow(this, et_one)
        et = arrayOf(et_one, et_two, et_three, et_four, et_five, et_six)
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS)
        et!!.forEachIndexed { index, _ ->
            val x = index
            LogUtil.e(TAG,"addTextChangedListener:$index")
            et!![index].addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    LogUtil.e(TAG,"s:$s====")
                    if (s.toString().length == 1) {
                        if (x == et!!.size-1) {
                            et!![et!!.size - 1].isFocusable = true
                            et!![et!!.size - 1].isFocusableInTouchMode = true
                            et!![et!!.size - 1].requestFocus()
                            et!![et!!.size - 1].isCursorVisible = false
                            //隐藏软键盘
                            inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS)
                            login()
                        } else {
                            et!![x + 1].isFocusable = true
                            et!![x + 1].isFocusableInTouchMode = true
                            et!![x + 1].requestFocus()
                        }
                    }
                }

                override fun afterTextChanged(s: Editable?) {

                }
            })

            et!![x].setOnKeyListener { v, keyCode, event ->
                LogUtil.e(TAG,"keyCode:$keyCode====event${event.keyCode}")
                if (event.keyCode == KeyEvent.KEYCODE_BACK) {
                    finish()
                    return@setOnKeyListener false
                }else{
                    if (flag) {
                        flag = false
                        if (event.keyCode == KeyEvent.KEYCODE_DEL) {
                            if (et!![x].text.toString().length == 1) {
                                et!![x].text.clear()
                            }else if (et!![x].text.toString().isEmpty()) {
                                if (x == 0) {
                                    et!![x].text.clear()
                                }else{
                                    et!![x-1].text.clear()
                                    et!![x-1].isFocusable = true
                                    et!![x-1].isFocusableInTouchMode = true
                                    et!![x-1].requestFocus()
                                }
                            }
                        }
                        return@setOnKeyListener true
                    }else{
                        flag = true
                        return@setOnKeyListener false
                    }
                }
            }
        }


    }

    /**
     * 登陆
     */
    private fun login() {
        showLoading()
        val code1 = et_one.text.toString().trim()
        val code2 = et_two.text.toString().trim()
        val code3 = et_three.text.toString().trim()
        val code4 = et_four.text.toString().trim()
        val code5 = et_five.text.toString().trim()
        val code6 = et_six.text.toString().trim()
        val code = code1 + code2 + code3 + code4 + code5 + code6
        val hashMap = mutableMapOf("phone" to phone, "code" to code)
        NetClient.getInstance()
            .create(IApiService::class.java)
            .login(RequestBody.create("application/json".toMediaTypeOrNull(), Gson().toJson(hashMap)))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(bindUntilEvent(ActivityEvent.DESTROY))
            .subscribe(object : BaseObserver<LoginBean>() {
                override fun onSuccess(t: LoginBean?) {
                    hideLoading()
                    t?.data?.let {
                        ConfigPreferences.instance.setLoginState(true)
                        //保存token
                        ConfigPreferences.instance.setToken(it.token ?: "")
                        //保存是否是会员
                        ConfigPreferences.instance.setIsMember(it.isMember)
                        //保存手机号
                        ConfigPreferences.instance.setPhone(phone)
                        EventBus.getDefault().post(LoginEvent())
                        startActivity(Intent(this@LoginVerificationCodeActivity, MainActivity::class.java))

                    }
                }

                override fun onError(msg: String?) {
                    hideLoading()
                    ToastUtils.showToast(msg)
                }
            })
    }

    /**
     * 倒计时60s
     */
    @SuppressLint("SetTextI18n")
    private fun timeCountDown() {
        Observable.interval(1, 1, TimeUnit.SECONDS)
            .take(59.minus(1))
            .map { 59.minus(it).minus(1) }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                tv_time.text = "重新发送${it}s"
                LogUtil.e(it.toString())
            }
            .doOnComplete {
                tv_time.text = "重新发送0s"
            }
            .compose(bindUntilEvent(ActivityEvent.DESTROY))
            .subscribe()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fl_back -> {
                finish()
            }
        }
    }
}
