package cn.cystal.app.view.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import cn.cystal.app.R
import cn.cystal.app.base.BaseActivity
import cn.cystal.app.base.DEBUG_BASE_URL
import cn.cystal.app.base.PHONE_NUMBER
import cn.cystal.app.base.RELEASE_BASE_URL
import cn.cystal.app.bean.LoginBean
import cn.cystal.app.bean.LoginErrorBean
import cn.cystal.app.event.LoginEvent
import cn.cystal.app.network.IApiService
import cn.cystal.app.storage.ConfigPreferences
import cn.cystal.app.util.LogUtil
import cn.cystal.app.util.ToastUtils
import cn.cystal.app.BuildConfig
import com.google.gson.Gson
import com.gyf.immersionbar.ImmersionBar
import com.trello.rxlifecycle2.android.ActivityEvent
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login_verification_code.*
import kotlinx.android.synthetic.main.common_toolbar.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.greenrobot.eventbus.EventBus
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

/**
 *  author : zhangbao
 *  date : 2020-02-08 13:20
 *  description : 输入登录验证码页面
 */
class LoginVerificationCodeActivity : BaseActivity(){

    private var et: Array<EditText>? = null
    private var flag = true
    private var phone: String = ""

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
        ImmersionBar.with(this)
            .statusBarDarkFont(true)
            .init()
        tv_title.text = getString(R.string.login_title)

        tb_toolbar.title = ""
        setSupportActionBar(tb_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)//左侧添加一个默认的返回图标
        supportActionBar?.setHomeButtonEnabled(true) //设置返回键可用

        tv_send_message_to.text = getString(R.string.sended_message) + intent.getStringExtra(PHONE_NUMBER)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            android.R.id.home->{
                finish()
            }
        }
        return true
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
            LogUtil.e(TAG, "addTextChangedListener:$index")
            et!![index].addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s.toString().length == 1) {
                        if (x == et!!.size - 1) {
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
                if (event.keyCode == KeyEvent.KEYCODE_BACK) {
                    finish()
                    return@setOnKeyListener false
                } else {
                    if (flag) {
                        flag = false
                        if (event.keyCode == KeyEvent.KEYCODE_DEL) {
                            if (et!![x].text.toString().length == 1) {
                                et!![x].text.clear()
                            } else if (et!![x].text.toString().isEmpty()) {
                                if (x == 0) {
                                    et!![x].text.clear()
                                } else {
                                    et!![x - 1].text.clear()
                                    et!![x - 1].isFocusable = true
                                    et!![x - 1].isFocusableInTouchMode = true
                                    et!![x - 1].requestFocus()
                                }
                            }
                        }
                        return@setOnKeyListener true
                    } else {
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

        val builder = Retrofit.Builder()
        val retrofit = builder
            .baseUrl(if (BuildConfig.DEBUG) DEBUG_BASE_URL else RELEASE_BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
        retrofit.create(IApiService::class.java)
            .login(RequestBody.create("application/json".toMediaTypeOrNull(), Gson().toJson(hashMap)))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(bindUntilEvent(ActivityEvent.DESTROY))
            .subscribe(object : Observer<String> {
                override fun onComplete() {
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(t: String) {
                    hideLoading()
                    val gson = Gson()
                    try {
                        val loginError = gson.fromJson(t, LoginErrorBean::class.java)
                        if (loginError.code == 200) {
                            val loginBean = gson.fromJson(t, LoginBean::class.java)
                            if (loginBean.code == 200 && loginBean.data != null) {
                                loginBean.data?.let {
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
                            } else {
                                ToastUtils.showToast(loginBean.message)
                            }
                        } else {
                            ToastUtils.showToast(loginError.message)
                        }
                    } catch (e: Exception) {
                    }
                }

                override fun onError(e: Throwable) {
                    hideLoading()
                    ToastUtils.showToast(e.message)
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
}
