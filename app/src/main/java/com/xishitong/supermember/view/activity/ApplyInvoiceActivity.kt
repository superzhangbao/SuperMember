package com.xishitong.supermember.view.activity

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.view.View
import android.widget.AdapterView
import com.google.gson.Gson
import com.gyf.immersionbar.ImmersionBar
import com.trello.rxlifecycle2.android.ActivityEvent
import com.xishitong.supermember.R
import com.xishitong.supermember.base.BaseActivity
import com.xishitong.supermember.base.BaseModel
import com.xishitong.supermember.network.BaseObserver
import com.xishitong.supermember.network.IApiService
import com.xishitong.supermember.network.NetClient
import com.xishitong.supermember.storage.ConfigPreferences
import com.xishitong.supermember.util.ToastUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_apply_invoice.*
import kotlinx.android.synthetic.main.common_toolbar.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody

/**
 * 申请开票
 */
class ApplyInvoiceActivity : BaseActivity(), View.OnClickListener, AdapterView.OnItemSelectedListener {

    private var type = 1//0个人  1企业

    companion object {
        var textSize = AbsoluteSizeSpan(13, true)
    }

    override fun setContentView(): Int {
        return R.layout.activity_apply_invoice
    }

    override fun init(savedInstanceState: Bundle?) {
        v_state_bar.setBackgroundColor(Color.WHITE)
        ImmersionBar.with(this)
            .statusBarDarkFont(true)
            .init()
        rl_toobar.setBackgroundColor(Color.WHITE)
        fl_back.visibility = View.VISIBLE
        tv_title.text = getString(R.string.apply_invoice)
        tv_title.setTextColor(resources.getColor(R.color.color_333333))
        fl_back.setOnClickListener(this)

        btn_submit.setOnClickListener(this)
        spinner.onItemSelectedListener = this

        //设置hint的大小
        initEditText()

    }

    private fun initEditText() {
        val s1 = SpannableString("请输入订单编号")
        s1.setSpan(textSize, 0, s1.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        val s2 = SpannableString("请输入真实名称")
        s2.setSpan(textSize, 0, s2.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        val s3 = SpannableString("请输入身份证号")
        s3.setSpan(textSize, 0, s3.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        val s4 = SpannableString("请输入家庭地址")
        s4.setSpan(textSize, 0, s4.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        val s5 = SpannableString("请输入电话号码")
        s5.setSpan(textSize, 0, s5.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        val s6 = SpannableString("请输入开户银行")
        s6.setSpan(textSize, 0, s6.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        val s7 = SpannableString("请输入银行账户")
        s7.setSpan(textSize, 0, s7.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        et_order_number.hint = s1
        et_name.hint = s2
        et_idcard_number.hint = s3
        et_home_addr.hint = s4
        et_phone_number.hint = s5
        et_deposit_bank.hint = s6
        et_bank_account.hint = s7

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fl_back -> {
                finish()
            }
            R.id.btn_submit -> {
                //校验数据
                val orderNo = et_order_number.text.toString().trim()
                if (orderNo.isEmpty()) {
                    ToastUtils.showToast("请输入订单编号")
                    return
                }
                val name = et_name.text.toString().trim()
                if (name.isEmpty()) {
                    when (type) {
                        1 -> {
                            ToastUtils.showToast("请输入真实姓名")
                        }
                        2 -> {
                            ToastUtils.showToast("请输入单位名称")
                        }
                    }
                    return
                }
                val idCardNo = et_idcard_number.text.toString().trim()
                if (idCardNo.isEmpty()) {
                    when (type) {
                        1 -> {
                            ToastUtils.showToast("请输入身份证号")
                        }
                        2 -> {
                            ToastUtils.showToast("请输入单位税号")
                        }
                    }
                    return
                }

                val homeAddr = et_home_addr.text.toString().trim()
                if (type == 2 && homeAddr.isEmpty()) {
                    ToastUtils.showToast("请输入单位地址")
                    return
                }
                val phone = et_phone_number.text.toString().trim()
                if (type == 2 && phone.isEmpty()) {
                    ToastUtils.showToast("请输入电话号码")
                    return
                }
                val depositBank = et_deposit_bank.text.toString().trim()
                if (type == 2 && depositBank.isEmpty()) {
                    ToastUtils.showToast("请输入开户银行")
                    return
                }
                val bankAccount = et_bank_account.text.toString().trim()
                if (type == 2 && bankAccount.isEmpty()) {
                    ToastUtils.showToast("请输入银行账户")
                    return
                }
                val hashMap = HashMap<String, Any>()
                hashMap["token"] = ConfigPreferences.instance.getToken()
                hashMap["orderNo"] = orderNo
                hashMap["type"] = "2"//发票类型 1增值税专用发票 2普通发票 3专业发票
                hashMap["content"] = "信息技术服务类"
                hashMap["title"] = "$type"
                hashMap["addressName"] = name
                hashMap["addressPhone"] = phone
                hashMap["addressGegion"] = ""
                hashMap["addressDetailed"] = ""
                hashMap["companyName"] = ""
                hashMap["companyNumber"] = ""
                hashMap["moreAddress"] = ""
                hashMap["moreMobile"] = ""
                hashMap["moreBank"] = ""
                hashMap["moreBankAccount"] = ""
                NetClient.getInstance()
                    .create(IApiService::class.java)
                    .applyInvoice(RequestBody.create("application/json".toMediaTypeOrNull(), Gson().toJson(hashMap)))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .compose(bindUntilEvent(ActivityEvent.DESTROY))
                    .subscribe(object : BaseObserver<BaseModel>() {
                        override fun onSuccess(t: BaseModel?) {
                            ToastUtils.showToast("申请成功")
                            finish()
                        }

                        override fun onError(msg: String?) {
                            ToastUtils.showToast(msg)
                        }
                    })
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (parent?.getItemAtPosition(position) as String) {
            "个人" -> {
                type = 1
                changeUi()
            }
            "企业" -> {
                type = 2
                changeUi()
            }
        }
    }

    private fun changeUi() {
        when (type) {
            1 -> {//个人
                tv2.text = "名称"
                tv3.text = "身份证号"
                tv4.text = "家庭地址"
                tv_xing4.visibility = View.INVISIBLE
                tv_xing5.visibility = View.INVISIBLE
                tv_xing6.visibility = View.INVISIBLE
                tv_xing7.visibility = View.INVISIBLE
                changeEditTextHint()
            }
            2 -> {//企业
                tv2.text = "单位名称"
                tv3.text = "税号"
                tv4.text = "单位地址"
                tv_xing4.visibility = View.VISIBLE
                tv_xing5.visibility = View.VISIBLE
                tv_xing6.visibility = View.VISIBLE
                tv_xing7.visibility = View.VISIBLE
                changeEditTextHint()
            }
        }
    }

    private fun changeEditTextHint() {
        when (type) {
            1 -> {
                val s2 = SpannableString("请输入真实名称")
                s2.setSpan(textSize, 0, s2.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                val s3 = SpannableString("请输入身份证号")
                s3.setSpan(textSize, 0, s3.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                val s4 = SpannableString("请输入家庭地址")
                s4.setSpan(textSize, 0, s4.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                et_name.hint = s2
                et_idcard_number.hint = s3
                et_home_addr.hint = s4
            }
            2 -> {
                val s2 = SpannableString("请输入单位名称")
                s2.setSpan(textSize, 0, s2.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                val s3 = SpannableString("请输入单位税号")
                s3.setSpan(textSize, 0, s3.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                val s4 = SpannableString("请输入单位地址")
                s4.setSpan(textSize, 0, s4.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                et_name.hint = s2
                et_idcard_number.hint = s3
                et_home_addr.hint = s4
            }
        }
    }
}
