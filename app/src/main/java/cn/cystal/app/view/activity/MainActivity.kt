package cn.cystal.app.view.activity

import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import cn.cystal.app.R
import cn.cystal.app.base.BaseActivity
import cn.cystal.app.base.BaseFragment
import cn.cystal.app.base.PRIVACY_POLICY
import cn.cystal.app.base.USER_AGREEMENT
import cn.cystal.app.event.WebEvent
import cn.cystal.app.storage.ConfigPreferences
import cn.cystal.app.view.fragment.MineFragment
import cn.cystal.app.view.fragment.PrivilegeFragment
import cn.cystal.app.view.fragment.SpecialsaleFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.gyf.immersionbar.ImmersionBar
import com.just.agentweb.AgentWebConfig
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.common_toolbar.*
import org.greenrobot.eventbus.EventBus

class MainActivity : BaseActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    private var lastfragment = 0
    private var fragments: Array<BaseFragment>? = null

    override fun setContentView(): Int {
        return R.layout.activity_main
    }

    override fun init(savedInstanceState: Bundle?) {
        initTitle()
        initFragment()

        AgentWebConfig.debug()
        if (!ConfigPreferences.instance.getAgreementState()) {
            val agreement = getString(R.string.agreement)
            val finalAgreement = getClickableSpan(agreement)
            showAgreementDialog(finalAgreement,
                View.OnClickListener {
                    mAgreementDialog?.dismiss()
                    mAppManager?.finishAllActivity()
                },
                View.OnClickListener {
                    mAgreementDialog?.dismiss()
                    ConfigPreferences.instance.setAgreementState(true)
                    requestPermission()
                })
        } else {
            requestPermission()
        }
    }

    private fun initTitle() {
        ImmersionBar.with(this).init()
//        v_state_bar.setBackgroundColor(resources.getColor(R.color.color_6BB467))
        v_state_bar.setBackgroundColor(ContextCompat.getColor(this,R.color.color_6BB467))
        tb_toolbar.setBackgroundColor(ContextCompat.getColor(this,R.color.color_6BB467))
        tv_title.text = getString(R.string.xishitong_vip)
        tv_title.setTextColor(Color.WHITE)
    }

    private fun requestPermission() {
        //申请权限
        RxPermissions(this)
            .request(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            .subscribe()
    }

    private fun initFragment() {
        bottom_navigation.setOnNavigationItemSelectedListener(this)
        val privilegeFragment = PrivilegeFragment()
        val specialsaleFragment = SpecialsaleFragment()
        val mineFragment = MineFragment()
        fragments = arrayOf(privilegeFragment, specialsaleFragment, mineFragment)
        lastfragment = 0
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fl_home, privilegeFragment)
            .show(privilegeFragment)
            .commit()

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_bottom_privilege -> {
                if (lastfragment != 0) {
                    switchFragment(0)
                    lastfragment = 0
                    tv_title.text = getString(R.string.xishitong_vip)
                }
                return true
            }
            R.id.item_bottom_specialsale -> {
                if (lastfragment != 1) {
                    switchFragment(1)
                    lastfragment = 1
                    tv_title.text = getString(R.string.specialsale)
                }
                return true
            }
            R.id.item_bottom_mine -> {
                //判断是否登陆
                if (!ConfigPreferences.instance.getLoginState()) {
                    startActivity(Intent(this, LoginActivity::class.java))
                    return false
                }
                if (lastfragment != 2) {
                    switchFragment(2)
                    lastfragment = 2
                    tv_title.text = getString(R.string.mine)
                }
                return true
            }
        }
        return false
    }

    fun selectNavigationItem(index: Int) {
        bottom_navigation.selectedItemId = bottom_navigation.menu.getItem(index).itemId
    }

    private fun switchFragment(index: Int) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.hide(fragments?.get(lastfragment)!!)//隐藏上个Fragment
        if (!fragments!![index].isAdded) {
            transaction.add(R.id.fl_home, fragments!![index])
        }
        transaction.show(fragments!![index]).commitAllowingStateLoss()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        fragments?.get(1)?.let {
            val specialsaleFragment = it as SpecialsaleFragment
            return if (specialsaleFragment.onFragmentKeyDown(keyCode, event)) {
                true
            } else {
                super.onKeyDown(keyCode, event)
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun getClickableSpan(text: String): SpannableStringBuilder {
        val firstIndex = text.indexOf("《")
        val firstIndex2 = text.indexOf('》') + 1
        val lastIndex = text.lastIndexOf('《')
        val lastIndex2 = text.lastIndexOf('》') + 1
        return SpannableStringBuilder(text).also {
            it.setSpan(UnderlineSpan(), firstIndex, firstIndex2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            it.setSpan(UnderlineSpan(), lastIndex, lastIndex2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            it.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    EventBus.getDefault().postSticky(WebEvent(PRIVACY_POLICY))
                    val intent = Intent(this@MainActivity, CommonWebActivity::class.java)
                    startActivity(intent)
                }
            }, firstIndex, firstIndex2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            it.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    EventBus.getDefault().postSticky(WebEvent(USER_AGREEMENT))
                    val intent = Intent(this@MainActivity, CommonWebActivity::class.java)
                    startActivity(intent)
                }
            }, lastIndex, lastIndex2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            it.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(this,R.color.color_6BB467)),
                firstIndex,
                firstIndex2,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            it.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(this,R.color.color_6BB467)),
                lastIndex,
                lastIndex2,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }
}
