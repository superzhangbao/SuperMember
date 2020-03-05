package cn.cystal.app.view.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.style.AbsoluteSizeSpan
import android.view.View
import android.widget.AdapterView
import cn.cystal.app.R
import cn.cystal.app.base.BaseActivity
import cn.cystal.app.base.*
import cn.cystal.app.bean.MyAddressBean
import cn.cystal.app.bean.UserBean
import cn.cystal.app.network.BaseObserver
import cn.cystal.app.network.IApiService
import cn.cystal.app.network.NetClient
import cn.cystal.app.storage.ConfigPreferences
import cn.cystal.app.util.LogUtil
import cn.cystal.app.util.ToastUtils
import com.google.gson.Gson
import com.gyf.immersionbar.ImmersionBar
import com.trello.rxlifecycle2.android.ActivityEvent
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
    private var userInfo: UserBean.DataBean? = null
    private var orderNo: String = ""
    private var addressInfo: MyAddressBean.DataBean.ListBean? = null

    companion object {
        var textSize = AbsoluteSizeSpan(16, true)
    }

    override fun setContentView(): Int {
        return R.layout.activity_apply_invoice
    }

    override fun init(savedInstanceState: Bundle?) {
        v_state_bar.setBackgroundColor(Color.WHITE)
        ImmersionBar.with(this)
            .statusBarDarkFont(true)
            .keyboardEnable(true)
            .init()
        rl_toobar.setBackgroundColor(Color.WHITE)
        fl_back.visibility = View.VISIBLE
        tv_title.text = getString(R.string.apply_invoice)
        tv_title.setTextColor(resources.getColor(R.color.color_333333))
        fl_back.setOnClickListener(this)

        ll_add_addr.setOnClickListener(this)
        ll_receive_addr.setOnClickListener(this)
        btn_submit.setOnClickListener(this)
        spinner.onItemSelectedListener = this
        orderNo = intent.getStringExtra("orderNo")
        tv_order_number.text = orderNo

        //设置hint的大小
        initEditText()
        getUserInfo()
        getAddress()
    }

    //获取用户的收件地址
    private fun getAddress() {
        val hashMap = mapOf(Pair("token", ConfigPreferences.instance.getToken()))
        NetClient.getInstance()
            .create(IApiService::class.java)
            .getAddressList(RequestBody.create("application/json".toMediaTypeOrNull(), Gson().toJson(hashMap)))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(bindUntilEvent(ActivityEvent.DESTROY))
            .subscribe(object : BaseObserver<MyAddressBean>() {
                override fun onSuccess(t: MyAddressBean?) {
                    t?.data?.list?.let { lists ->
                        if (lists.size > 0) {
                            addressInfo = lists[0]
                            setAddress()
                        }
                    }
                }

                override fun onError(msg: String?) {
                    ToastUtils.showToast(msg)
                }
            })
    }

    private fun initEditText() {
//        val s2 = SpannableString("请输入真实名称")
//        s2.setSpan(textSize, 0, s2.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
//        val s3 = SpannableString("请输入身份证号")
//        s3.setSpan(textSize, 0, s3.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
//        val s4 = SpannableString("请输入家庭地址")
//        s4.setSpan(textSize, 0, s4.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
//        val s5 = SpannableString("请输入电话号码")
//        s5.setSpan(textSize, 0, s5.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
//        val s6 = SpannableString("请输入开户银行")
//        s6.setSpan(textSize, 0, s6.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
//        val s7 = SpannableString("请输入银行账户")
//        s7.setSpan(textSize, 0, s7.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

//        et_name.hint = s2
//        et_idcard_number.hint = s3
//        et_home_addr.hint = s4
//        et_phone_number.hint = s5
//        et_deposit_bank.hint = s6
//        et_bank_account.hint = s7
    }

    private fun getUserInfo() {
        val hashMap = HashMap<String, Any>()
        hashMap["token"] = ConfigPreferences.instance.getToken()
        NetClient.getInstance()
            .create(IApiService::class.java)
            .getUserInfo(RequestBody.create("application/json".toMediaTypeOrNull(), Gson().toJson(hashMap)))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(bindUntilEvent(ActivityEvent.DESTROY))
            .subscribe(object : BaseObserver<UserBean>() {
                override fun onSuccess(t: UserBean?) {
                    t?.data?.let { userBean ->
                        userInfo = userBean
                        setUserData()
                    }
                }

                override fun onError(msg: String?) {
                    ToastUtils.showToast(msg)
                }
            })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fl_back -> {
                finish()
            }
            R.id.ll_add_addr -> {
                startActivity(Intent(this, ModifyAddressActivity::class.java))
            }
            R.id.ll_receive_addr -> {
                val intent = Intent(this, MyAddressActivity::class.java)
                intent.putExtra("from", CHOOSE)
                startActivityForResult(intent, REQUEST_CODE_APPLYINVOICE)
            }
            R.id.btn_submit -> {
                //校验数据
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
                showLoading()
                val hashMap = HashMap<String, String>()
                hashMap["token"] = ConfigPreferences.instance.getToken()
                hashMap["orderNo"] = orderNo
                hashMap["type"] = "2"//发票类型 1增值税专用发票 2普通发票 3专业发票
                hashMap["content"] = "信息技术服务类"
                hashMap["title"] = "$type"
                hashMap["addressName"] = addressInfo?.name ?: ""
                hashMap["addressPhone"] = addressInfo?.phone ?: ""
                hashMap["addressGegion"] = addressInfo?.gegion ?: ""
                hashMap["addressDetailed"] = addressInfo?.detailed ?: ""
                hashMap["invoiceName"] = if (type == 1) {
                    name
                } else {
                    ""
                }
                hashMap["companyName"] = if (type == 2) {
                    name
                } else {
                    ""
                }
                hashMap["idCard"] = if (type == 1) {
                    idCardNo
                } else {
                    ""
                }
                hashMap["companyNumber"] = if (type == 2) {
                    idCardNo
                } else {
                    ""
                }
                hashMap["moreAddress"] = homeAddr
                hashMap["moreMobile"] = phone
                hashMap["moreBank"] = depositBank
                hashMap["moreBankAccount"] = bankAccount
                NetClient.getInstance()
                    .create(IApiService::class.java)
                    .applyInvoice(RequestBody.create("application/json".toMediaTypeOrNull(), Gson().toJson(hashMap)))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .compose(bindUntilEvent(ActivityEvent.DESTROY))
                    .subscribe(object : BaseObserver<BaseModel>() {
                        override fun onSuccess(t: BaseModel?) {
                            hideLoading()
                            ToastUtils.showToast("申请成功")
                            finish()
                        }

                        override fun onError(msg: String?) {
                            hideLoading()
                            ToastUtils.showToast(msg)
                        }
                    })
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null && resultCode == RESULT_CODE_MYADDRESS) {
            if (requestCode == REQUEST_CODE_APPLYINVOICE) {
                val name = data.getStringExtra("name")
                val phone = data.getStringExtra("phone")
                val gegion = data.getStringExtra("gegion")
                val detailed = data.getStringExtra("detailed")
                LogUtil.e(TAG, name)
                tv_receice_name.text = "$name ($phone)"
                tv_receice_address.text = "$gegion $detailed"
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
                changeEditTextHint()
                setUserData()
            }
            "企业" -> {
                type = 2
                changeUi()
                changeEditTextHint()
                setUserData()
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

            }
            2 -> {//企业
                tv2.text = "单位名称"
                tv3.text = "税号"
                tv4.text = "单位地址"
                tv_xing4.visibility = View.VISIBLE
                tv_xing5.visibility = View.VISIBLE
                tv_xing6.visibility = View.VISIBLE
                tv_xing7.visibility = View.VISIBLE
            }
        }
    }

    private fun changeEditTextHint() {
        when (type) {
            1 -> {
//                val s2 = SpannableString("请输入真实名称")
//                s2.setSpan(textSize, 0, s2.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
//                val s3 = SpannableString("请输入身份证号")
//                s3.setSpan(textSize, 0, s3.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
//                val s4 = SpannableString("请输入家庭地址")
//                s4.setSpan(textSize, 0, s4.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                et_name.hint = "请输入真实名称"
                et_idcard_number.hint = "请输入身份证号"
                et_home_addr.hint = "请输入家庭地址"
            }
            2 -> {
//                val s2 = SpannableString("请输入单位名称")
//                s2.setSpan(textSize, 0, s2.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
//                val s3 = SpannableString("请输入单位税号")
//                s3.setSpan(textSize, 0, s3.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
//                val s4 = SpannableString("请输入单位地址")
//                s4.setSpan(textSize, 0, s4.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                et_name.hint = "请输入单位名称"
                et_idcard_number.hint = "请输入单位税号"
                et_home_addr.hint = "请输入单位地址"
            }
        }
    }

    //设置用户信息
    private fun setUserData() {
        when (type) {
            1 -> {
                userInfo?.let {
                    et_name.setText(userInfo!!.name)
                    et_idcard_number.setText(userInfo!!.idCard)
                    et_phone_number.setText(userInfo!!.userPhone)
                    et_bank_account.setText(userInfo!!.bankCard)
                }
            }
            2 -> {
                et_name.setText("")
                et_idcard_number.setText("")
                et_phone_number.setText("")
                et_bank_account.setText("")
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setAddress() {
        ll_add_addr.visibility = View.GONE
        ll_receive_addr.visibility = View.VISIBLE
        tv_receice_name.text = "${addressInfo?.name}(${addressInfo?.phone})"
        tv_receice_address.text = "${addressInfo?.gegion} ${addressInfo?.detailed}"
    }
}
