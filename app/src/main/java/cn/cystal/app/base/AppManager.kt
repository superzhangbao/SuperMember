package cn.cystal.app.base

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import cn.cystal.app.util.LogUtil
import java.util.*
import kotlin.system.exitProcess

/**
 * Created by zhangb on 2018/3/14/014
 */

class AppManager private constructor() {

    /**
     * 添加Activity到堆栈
     */
    fun addActivity(activity: Activity) {
        if (activityStack == null) {
            activityStack = Stack()
        }
        LogUtil.e(TAG,"add")
        activityStack?.add(activity)
        for (ac in activityStack!!) {
            LogUtil.e(TAG,ac.localClassName)
        }
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    fun currentActivity(): Activity? {
        return activityStack?.lastElement()
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    fun finishActivity() {
        val activity = activityStack?.lastElement()
        finishActivity(activity)
    }

    /**
     * 结束指定的Activity
     */
    fun finishActivity(activity: Activity?) {
        LogUtil.e(TAG,"remove")
        activityStack?.remove(activity)
        activity?.finish()
    }

    /***
     * 获取指定类名的Activity实例
     * @param cls
     * @return
     */
    fun getByActivity(cls: Class<*>): Activity? {
        var activity: Activity? = null
        for (a in activityStack!!) {
            if (a.javaClass == cls) {
                activity = a
            }
        }
        return activity
    }

    /**
     * 结束指定类名的Activity
     */
    fun finishActivity(cls: Class<*>) {
        for (activity in activityStack!!) {
            if (activity.javaClass == cls) {
                finishActivity(activity)
            }
        }
    }

    /**
     * 结束所有Activity
     */
    fun finishAllActivity() {
        var i = 0
        val size = activityStack?.size
        while (i < size ?: 0) {
            if (null != activityStack!![i]) {
                activityStack!![i].finish()
            }
            i++
        }
        activityStack?.clear()
    }

    /**
     * 退出应用程序
     */
    fun appExit(context: Context) {
        LogUtil.e("退出应用")
        try {
            finishAllActivity()
            val activityMgr = context
                    .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            activityMgr.restartPackage(context.packageName)
            exitProcess(0)
        } catch (e: Exception) {
        }

    }
    private var activityStack: Stack<Activity>? = null // 保存activity的栈
    companion object {
//        /**
//         * 返回当前类的实例（单例模式）
//         */
//        val appManager: AppManager
//            get() {
//                if (instance == null) {
//                    instance = AppManager()
//                }
//                return instance as AppManager
//            }
        val TAG = this.javaClass.simpleName
        val SINGLETON by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            AppManager()
        }
    }
}
