package cn.cystal.app.view.activity

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import cn.cystal.app.R
import cn.cystal.app.base.ADD_OR_MODIFY
import cn.cystal.app.base.BaseActivity
import cn.cystal.app.base.BaseModel
import cn.cystal.app.bean.JsonBean
import cn.cystal.app.network.BaseObserver
import cn.cystal.app.network.IApiService
import cn.cystal.app.network.NetClient
import cn.cystal.app.storage.ConfigPreferences
import cn.cystal.app.util.GetJsonDataUtil
import cn.cystal.app.util.ToastUtils
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.listener.OnOptionsSelectListener
import com.google.gson.Gson
import com.gyf.immersionbar.ImmersionBar
import com.trello.rxlifecycle2.android.ActivityEvent
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_modify.*
import kotlinx.android.synthetic.main.common_toolbar.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.json.JSONArray


/**
 * 新增/修改地址页面
 */
class ModifyAddressActivity : BaseActivity(), View.OnClickListener {

    private var options1Items: MutableList<JsonBean> = ArrayList()
    private val options2Items: MutableList<MutableList<String>> = ArrayList()
    private val options3Items: MutableList<MutableList<MutableList<String>>> = ArrayList()
    private var addOrModify: Int = 0
    private var id = ""

    companion object {
        var textSize = AbsoluteSizeSpan(15, true)
    }

    override fun setContentView(): Int {
        return R.layout.activity_modify
    }

    override fun init(savedInstanceState: Bundle?) {
        initToolBar()
        initProvinceData()
        initEditHint()
    }

    private fun initEditHint() {
        val hint1 = SpannableString(getString(R.string.input_receiver_name))
        hint1.setSpan(textSize, 0, hint1.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        val hint2 = SpannableString(getString(R.string.input_receiver_phone))
        hint2.setSpan(textSize, 0, hint2.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        val hint3 = SpannableString(getString(R.string.location))
        hint3.setSpan(textSize, 0, hint3.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        val hint4 = SpannableString(getString(R.string.input_detail_address))
        hint4.setSpan(textSize, 0, hint4.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        et_addr_receiver_name.hint = hint1
        et_addr_receiver_phone.hint = hint2
        et_home_addr.hint = hint3
        et_addr_detail.hint = hint4
    }

    private fun initToolBar() {
        ImmersionBar.with(this)
            .statusBarDarkFont(true)
            .keyboardEnable(true)
            .init()
        tb_toolbar.title = ""
        setSupportActionBar(tb_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)//左侧添加一个默认的返回图标
        supportActionBar?.setHomeButtonEnabled(true) //设置返回键可用

        addOrModify = intent.getIntExtra(ADD_OR_MODIFY, 0)
        if (addOrModify == 0) {
            tv_title.text = getString(R.string.add_address)
            btn_delete.visibility = View.GONE
        } else if (addOrModify == 1) {
            val name = intent.getStringExtra("name")
            val phone = intent.getStringExtra("phone")
            val gegion = intent.getStringExtra("gegion")
            val detailed = intent.getStringExtra("detailed")
            id = intent.getStringExtra("id")
            tv_title.text = getString(R.string.modify_address)
            et_addr_receiver_name.setText(name)
            et_addr_receiver_phone.setText(phone)
            et_home_addr.setText(gegion)
            et_addr_detail.setText(detailed)
            btn_delete.setOnClickListener(this)
        }
        tv_title.setTextColor(resources.getColor(R.color.color_333333))
        et_home_addr.setOnClickListener(this)
        btn_submit.setOnClickListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return true
    }

    private fun initProvinceData() {
        Observable.just(1)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(bindUntilEvent(ActivityEvent.DESTROY))
            .doOnNext {
                initJsonData()
            }
            .subscribe()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.et_home_addr -> {
                showPickerView()
            }
            R.id.btn_submit, R.id.btn_delete -> {
                val name = et_addr_receiver_name.text.toString().trim()
                if (name.isEmpty()) {
                    ToastUtils.showToast("请输入收件人姓名")
                    return
                }
                val phone = et_addr_receiver_phone.text.toString().trim()
                if (phone.isEmpty()) {
                    ToastUtils.showToast("请输入收件人联系电话")
                    return
                }
                val location = et_home_addr.text.toString().trim()
                if (location.isEmpty()) {
                    ToastUtils.showToast("请选择所在地区")
                    return
                }
                val detailAddr = et_addr_detail.text.toString().trim()
                if (detailAddr.isEmpty()) {
                    ToastUtils.showToast("请输入详细地址")
                    return
                }
                if (v.id == R.id.btn_submit) {
                    //提交
                    if (addOrModify == 0) {
                        submitNewAddr(name, phone, location, detailAddr)
                    } else {
                        modifyOrDeleteAddr(name, phone, location, detailAddr, 1)//修改
                    }
                } else {
                    modifyOrDeleteAddr(name, phone, location, detailAddr, 0)//删除
                }

            }
        }
    }

    private fun modifyOrDeleteAddr(name: String, phone: String, location: String, detailAddr: String, status: Int) {
        showLoading()
        val hashMap = HashMap<String, Any>()
        hashMap["token"] = ConfigPreferences.instance.getToken()
        hashMap["addressName"] = name
        hashMap["addressPhone"] = phone
        hashMap["addressGegion"] = location
        hashMap["addressDetailed"] = detailAddr
        hashMap["status"] = status
        hashMap["id"] = id
        NetClient.getInstance()
            .create(IApiService::class.java)
            .editAddress(RequestBody.create("application/json".toMediaTypeOrNull(), Gson().toJson(hashMap)))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(bindUntilEvent(ActivityEvent.DESTROY))
            .subscribe(object : BaseObserver<BaseModel>() {
                override fun onSuccess(t: BaseModel?) {
                    hideLoading()
                    finish()
                }

                override fun onError(msg: String?) {
                    hideLoading()
                    ToastUtils.showToast(msg)
                }
            })
    }

    /**
     * 提交新地址
     */
    private fun submitNewAddr(name: String, phone: String, location: String, detailAddr: String) {
        showLoading()
        val hashMap = HashMap<String, String>()
        hashMap["token"] = ConfigPreferences.instance.getToken()
        hashMap["addressName"] = name
        hashMap["addressPhone"] = phone
        hashMap["addressGegion"] = location
        hashMap["addressDetailed"] = detailAddr
        NetClient.getInstance()
            .create(IApiService::class.java)
            .addAddress(RequestBody.create("application/json".toMediaTypeOrNull(), Gson().toJson(hashMap)))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(bindUntilEvent(ActivityEvent.DESTROY))
            .subscribe(object : BaseObserver<BaseModel>() {
                override fun onSuccess(t: BaseModel?) {
                    hideLoading()
                    finish()
                }

                override fun onError(msg: String?) {
                    hideLoading()
                    ToastUtils.showToast(msg)
                }
            })
    }

    private fun showPickerView() {// 弹出选择器
        //收起键盘
        val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(window.decorView.windowToken, 0)
        OptionsPickerBuilder(this,
            OnOptionsSelectListener { options1, options2, options3, _ ->
                //返回的分别是三个级别的选中位置
                val opt1tx =
                    if (options1Items.isNotEmpty()) options1Items[options1].pickerViewText else ""
                val opt2tx = if (options2Items.size > 0
                    && options2Items[options1].size > 0
                ) options2Items[options1][options2] else ""
                val opt3tx =
                    if (options2Items.size > 0 && options3Items[options1].size > 0 && options3Items[options1][options2].size > 0
                    ) options3Items[options1][options2][options3] else ""
                val tx = opt1tx + opt2tx + opt3tx
                et_home_addr.setText(tx)
            })
            .setTitleText("城市选择")
            .setDividerColor(Color.BLACK)
            .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
            .setContentTextSize(20)
            .build<Any>().also {
                it.setPicker(
                    options1Items as List<Any>?, options2Items as List<MutableList<Any>>?,
                    options3Items as List<MutableList<MutableList<Any>>>?
                ) //三级选择器
                it.show()
            }
    }

    private fun initJsonData() { //解析数据
        /**
         * 注意：assets 目录下的Json文件仅供参考，实际使用可自行替换文件
         * 关键逻辑在于循环体
         */
        val jsonData = GetJsonDataUtil().getJson(this, "province.json") //获取assets目录下的json文件数据
        val jsonBean = parseData(jsonData) //用Gson 转成实体
        /**
         * 添加省份数据
         *
         * 注意：如果是添加的JavaBean实体，则实体类需要实现 IPickerViewData 接口，
         * PickerView会通过getPickerViewText方法获取字符串显示出来。
         */
        options1Items = jsonBean
        for (i in 0 until jsonBean.size) { //遍历省份
            val cityList: MutableList<String> = ArrayList() //该省的城市列表（第二级）
            val provinceAreaList: MutableList<MutableList<String>> = ArrayList() //该省的所有地区列表（第三极）
            for (c in jsonBean[i].cityList?.indices!!) { //遍历该省份的所有城市
                val cityName = jsonBean[i].cityList!![c].name
                cityList.add(cityName!!) //添加城市
                val cityArealist: MutableList<String> = ArrayList() //该城市的所有地区列表
                //如果无地区数据，建议添加空字符串，防止数据为null 导致三个选项长度不匹配造成崩溃
                cityArealist.addAll(jsonBean[i].cityList!![c].area!!)
                provinceAreaList.add(cityArealist) //添加该省所有地区数据
            }
            /**
             * 添加城市数据
             */
            options2Items.add(cityList.toMutableList())
            /**
             * 添加地区数据
             */
            options3Items.add(provinceAreaList)
        }
    }


    private fun parseData(result: String?): ArrayList<JsonBean> { //Gson 解析
        val detail: ArrayList<JsonBean> = ArrayList()
        try {
            val data = JSONArray(result)
            val gson = Gson()
            for (i in 0 until data.length()) {
                val entity = gson.fromJson(data.optJSONObject(i).toString(), JsonBean::class.java)
                detail.add(entity)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return detail
    }
}
