package com.xishitong.supermember.view.activity

import android.Manifest
import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.view.KeyEvent
import android.view.MenuItem
import com.gyf.immersionbar.ImmersionBar
import com.just.agentweb.AgentWebConfig
import com.tbruyelle.rxpermissions2.RxPermissions
import com.xishitong.supermember.R
import com.xishitong.supermember.base.BaseActivity
import com.xishitong.supermember.base.BaseFragment
import com.xishitong.supermember.storage.ConfigPreferences
import com.xishitong.supermember.util.ToastUtils
import com.xishitong.supermember.view.fragment.MineFragment
import com.xishitong.supermember.view.fragment.PrivilegeFragment
import com.xishitong.supermember.view.fragment.SpecialsaleFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.common_toolbar.*

class MainActivity : BaseActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    private var lastfragment = 0
    private var fragments: Array<BaseFragment>? = null

    override fun setContentView(): Int {
        return R.layout.activity_main
    }

    override fun init(savedInstanceState: Bundle?) {
        ImmersionBar.with(this).init()
        bottom_navigation.setOnNavigationItemSelectedListener(this)
        initFragment()
        requestPermission()
    }

    private fun requestPermission() {
        AgentWebConfig.debug()
        RxPermissions(this)
            .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .doOnNext {
                if (it) {
                    ToastUtils.showToast("已获得权限")
                } else {
                    ToastUtils.showToast("未获得权限")
                }
            }
            .subscribe()
    }

    private fun initFragment() {
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
        tv_title.text = getString(R.string.xishitong_vip)
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
                    startActivity(Intent(this,LoginActivity::class.java))
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

    fun selectNavigationItem() {
        bottom_navigation.selectedItemId = bottom_navigation.menu.getItem(1).itemId
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
}
