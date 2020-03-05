package cn.cystal.app.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.trello.rxlifecycle2.components.support.RxFragment

/**
 *  author : zhangbao
 *  date : 2020-02-07 17:59
 *  description :
 */
abstract class BaseFragment:RxFragment() {
    companion object{
        val TAG: String = this.javaClass.simpleName
    }
    private var rootView: View? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (rootView == null) {
            rootView = View.inflate(activity, setContentView(), null)
        }
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
        initData()
    }

    abstract fun setContentView(): Int

    abstract fun initView(view: View)

    abstract fun initData()
}