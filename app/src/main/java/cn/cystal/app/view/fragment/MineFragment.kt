package cn.cystal.app.view.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import cn.cystal.app.R
import cn.cystal.app.base.*
import cn.cystal.app.bean.UserInfoBean
import cn.cystal.app.event.CloseCurrentPageEvent
import cn.cystal.app.event.LoginEvent
import cn.cystal.app.event.WebEvent
import cn.cystal.app.network.BaseObserver
import cn.cystal.app.network.IApiService
import cn.cystal.app.network.NetClient
import cn.cystal.app.storage.ConfigPreferences
import cn.cystal.app.util.LogUtil
import cn.cystal.app.util.ToastUtils
import cn.cystal.app.view.activity.*
import com.google.gson.Gson
import com.trello.rxlifecycle2.android.FragmentEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_mine.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 *  author : zhangbao
 *  date : 2020-02-07 17:59
 *  description :
 */
class MineFragment : BaseFragment(), View.OnClickListener {

    private var hidden = false
    override fun setContentView(): Int {
        return R.layout.fragment_mine
    }

    override fun initView(view: View) {
        EventBus.getDefault().register(this)
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
                        tv_integral.text = "${it.money / 100.0}"
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

    override fun onResume() {
        super.onResume()
        if (hidden) return
        LogUtil.e(TAG,"onResume")
    }



    //重新登录后刷新数据
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLoginEvent(loginEvent: LoginEvent) {
        initData()
    }

    //关闭其他页面，刷新数据mineFragment
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onCloseCurrentPageEvent(closeCurrentPageEvent: CloseCurrentPageEvent){
        initData()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_rule -> {
                EventBus.getDefault().postSticky(WebEvent(RULE))
                val intent = Intent(activity, CommonWebActivity::class.java)
                startActivity(intent)
            }
            R.id.tv_vip_recharge -> {
                if (checkLogin()) return
                if (ConfigPreferences.instance.getISMember()) {
                    //会费缴纳
                    EventBus.getDefault().postSticky(WebEvent(PAY_MEMBERSHIP))
                } else {
                    //申请入会
                    EventBus.getDefault().postSticky(
                        WebEvent(
                            APPLY_FOR_MEMBERSHIP
                        )
                    )
                }
                val intent = Intent(activity, CommonWebActivity::class.java)
                startActivity(intent)
            }
            R.id.processing_order -> {
                //处理中订单
                if (checkLogin()) return
                val intent = Intent(activity, OrderActivity::class.java)
                intent.putExtra("type", "100")
                startActivity(intent)
            }
            R.id.completed_order -> {
                //已完成订单
                if (checkLogin()) return
                val intent = Intent(activity, OrderActivity::class.java)
                intent.putExtra("type", "200")
                startActivity(intent)
            }
            R.id.fail_order -> {
                //失败订单
                if (checkLogin()) return
                val intent = Intent(activity, OrderActivity::class.java)
                intent.putExtra("type", "300")
                startActivity(intent)
            }
            R.id.all_order -> {
                //全部订单
                if (checkLogin()) return
                val intent = Intent(activity, OrderActivity::class.java)
                intent.putExtra("type", "0")
                startActivity(intent)
            }
            R.id.apply_invoice -> {
                //申请开票
                if (checkLogin()) return
                startActivity(Intent(activity, RechargeDetailActivity::class.java))
            }
            R.id.details_of_membership -> {
                //会费明细
                if (checkLogin()) return
                startActivity(Intent(activity, DetailOfMembershipActivity::class.java))
            }
            R.id.my_address -> {
                //我的地址
                if (checkLogin()) return
                val intent = Intent(activity, MyAddressActivity::class.java)
                intent.putExtra("from", MINE)
                startActivity(intent)
            }
            R.id.logout -> {
                ToastUtils.showToast("退出登陆")
                ConfigPreferences.instance.setLoginState(false)
                ConfigPreferences.instance.setToken("")
                ConfigPreferences.instance.setIsMember(false)
                ConfigPreferences.instance.setPhone("")
                tv_vip_recharge.text = resources.getString(R.string.apply_for_membership)
                tv_integral.text = "0"
                tv_phone.text = ""
                val mainActivity = activity as MainActivity
                mainActivity.selectNavigationItem(0)
            }
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        this.hidden = hidden
        if (!hidden) {
            LogUtil.e(TAG,"onHiddenChanged")
            initData()
        }
    }

    private fun checkLogin(): Boolean {
        if (!ConfigPreferences.instance.getLoginState()) {
            startActivity(Intent(activity, LoginActivity::class.java))
            return true
        }
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}