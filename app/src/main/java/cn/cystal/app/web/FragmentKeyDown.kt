package cn.cystal.app.web

import android.view.KeyEvent

/**
 * Created by cenxiaozhong on 2017/5/23.
 * source code  https://github.com/Justson/AgentWeb
 */
interface FragmentKeyDown {
    fun onFragmentKeyDown(keyCode: Int, event: KeyEvent?): Boolean
}