package com.xishitong.supermember.view.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import com.google.gson.Gson
import com.trello.rxlifecycle2.android.FragmentEvent
import com.xishitong.supermember.R
import com.xishitong.supermember.base.APPLY_FOR_MEMBERSHIP
import com.xishitong.supermember.base.BaseFragment
import com.xishitong.supermember.base.PAY_MEMBERSHIP
import com.xishitong.supermember.base.RULE
import com.xishitong.supermember.bean.UserInfoBean
import com.xishitong.supermember.event.WebEvent
import com.xishitong.supermember.network.BaseObserver
import com.xishitong.supermember.network.IApiService
import com.xishitong.supermember.network.NetClient
import com.xishitong.supermember.storage.ConfigPreferences
import com.xishitong.supermember.util.ToastUtils
import com.xishitong.supermember.view.activity.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_mine.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.greenrobot.eventbus.EventBus

/**
 *  author : zhangbao
 *  date : 2020-02-07 17:59
 *  description :
 */
class MineFragment : BaseFragment(), View.OnClickListener {

    override fun setContentView(): Int {
        return R.layout.fragment_mine
    }

    override fun initView(view: View) {
        tv_rule.setOnClickListener(this)
        tv_vip_recharge.setOnClickListener(this)
        processing_order.setOnClickListener(this)
        completed_order.setOnClickListener(this)
        fail_order.setOnClickListener(this)
        all_order.setOnClickListener(this)

        apply_invoice.setOnClickListener(this)
        details_of_membership.setOnClickListener(this)
        my_address.setOnClickListener(this)

        logout.setOnClickListener(this)
    }

    override fun initData() {

    }

    override fun onResume() {
        super.onResume()
        //请求个人信息
        val hashMap = HashMap<String, String>()
        hashMap["token"] = ConfigPreferences.instance.getToken()
        NetClient.getInstance()
            .create(IApiService::class.java)
            .getBalanceInfo(RequestBody.create("application/json".toMediaTypeOrNull(), Gson().toJson(hashMap)))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(bindUntilEvent(FragmentEvent.DESTROY))
            .subscribe(object : BaseObserver<UserInfoBean>() {
                @SuppressLint("SetTextI18n")
                override fun onSuccess(t: UserInfoBean?) {
                    t?.data?.let {
                        tv_integral.text = "${it.money/100.0}"
                        tv_phone.text = "No.${ConfigPreferences.instance.getPhone()}"
                        ConfigPreferences.instance.setIsMember(it.isMember)
                        if (it.isMember) {
                            tv_vip_recharge.text = resources.getString(R.string.pay_membership)
                        } else {
                            tv_vip_recharge.text = resources.getString(R.string.apply_for_membership)
                        }
                    }
                }

                override fun onError(msg: String?) {
                    ToastUtils.showToast(msg)
                }
            })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_rule -> {
                EventBus.getDefault().postSticky(WebEvent(RULE,null))
                val intent = Intent(activity, CommonWebActivity::class.java)
                startActivity(intent)
            }
            R.id.tv_vip_recharge -> {
                if (!ConfigPreferences.instance.getLoginState()) {
                    startActivity(Intent(activity, LoginActivity::class.java))
                    return
                }
                if (ConfigPreferences.instance.getISMember()) {
                    //会费缴纳
                    EventBus.getDefault().postSticky(
                        WebEvent(PAY_MEMBERSHIP, ConfigPreferences.instance.getToken())
                    )
                } else {
                    //申请入会
                    EventBus.getDefault().postSticky(
                        WebEvent(APPLY_FOR_MEMBERSHIP, ConfigPreferences.instance.getToken())
                    )
                }
                val intent = Intent(activity, CommonWebActivity::class.java)
                startActivity(intent)
            }
            R.id.processing_order -> {
                //处理中订单
                val intent = Intent(activity, OrderActivity::class.java)
                intent.putExtra("type","100")
                startActivity(intent)
            }
            R.id.completed_order -> {
                //已完成订单
                val intent = Intent(activity, OrderActivity::class.java)
                intent.putExtra("type","200")
                startActivity(intent)
            }
            R.id.fail_order -> {
                //失败订单
                val intent = Intent(activity, OrderActivity::class.java)
                intent.putExtra("type","300")
                startActivity(intent)
            }
            R.id.all_order -> {
                //全部订单
                val intent = Intent(activity, OrderActivity::class.java)
                intent.putExtra("type","0")
                startActivity(intent)
            }
            R.id.apply_invoice -> {
                //申请开票
                startActivity(Intent(activity, RechargeDetailActivity::class.java))
            }
            R.id.details_of_membership -> {
                //会费明细
                startActivity(Intent(activity, DetailOfMembershipActivity::class.java))
            }
            R.id.my_address -> {
                //我的地址
                startActivity(Intent(activity, MyAddressActivity::class.java))
            }
            R.id.logout -> {
                ToastUtils.showToast("退出登陆")
                ConfigPreferences.instance.setLoginState(false)
                ConfigPreferences.instance.setToken("")
                ConfigPreferences.instance.setIsMember(false)
                ConfigPreferences.instance.setPhone("")
            }
        }
    }
}