package com.xishitong.supermember.storage

import android.content.Context
import android.content.SharedPreferences
import com.xishitong.supermember.base.App
import java.util.*

/**
 * Created by zhangb on 2018/4/3/003
 * sp工具类
 */
class ConfigPreferences private constructor() {
    private val mSharedPreferences: SharedPreferences
    private val LOGIN_STATE = "login_state"
    private val TOKEN = "token"
    private val IS_MEMBER = "is_member"
    private val PHONE = "phone"

    private object ConfigPreHolder {
        internal val PREFERENCES = ConfigPreferences()
    }

    companion object {
        private const val PREFERENCE = "settings"
        val instance: ConfigPreferences
            get() = ConfigPreHolder.PREFERENCES
    }

    init {
        mSharedPreferences = Objects.requireNonNull(App.getInstance())
            .getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE)
    }

    fun setLoginState(login: Boolean) {
        val edit = mSharedPreferences.edit()
        edit.putBoolean(LOGIN_STATE, login)
        edit.commit()
    }

    fun getLoginState(): Boolean {
        return mSharedPreferences.getBoolean(LOGIN_STATE, false)
    }

    fun setToken(token: String) {
        val edit = mSharedPreferences.edit()
        edit.putString(TOKEN, token)
        edit.commit()
    }

    fun getToken(): String {
        return mSharedPreferences.getString(TOKEN, "")
    }

    fun setIsMember(isMember:Boolean){
        val edit = mSharedPreferences.edit()
        edit.putBoolean(IS_MEMBER,isMember)
        edit.commit()
    }

    fun getISMember():Boolean {
        return mSharedPreferences.getBoolean(IS_MEMBER,false)
    }

    fun setPhone(phone:String) {
        val edit = mSharedPreferences.edit()
        edit.putString(PHONE,phone)
        edit.commit()
    }

    fun getPhone():String {
        return mSharedPreferences.getString(PHONE,"")
    }
}