package cn.cystal.app.base

import cn.cystal.app.BuildConfig

/**
 *  author : zhangbao
 *  date : 2020-02-08 20:04
 *  description :基础信息配置类
 */
fun createBaseUrl() {
    if (BuildConfig.DEBUG) {
        BASE_URL = BASE_DEBUG_URL
        BASE_WEB_URL = BASE_WEB_DEBUG_URL
    } else {
        BASE_URL = BASE_RELEASE_URL
        BASE_WEB_URL = BASE_WEB_RELEASE_URL
    }
}

fun getBaseUrl():String {
    return BASE_URL
}

const val BASE_DEBUG_URL = "http://47.99.80.137:8087/"
const val BASE_RELEASE_URL = "https://xishi.seniornet.cn/"
private var BASE_URL = BASE_RELEASE_URL

//测试web地址
const val BASE_WEB_DEBUG_URL = "http://www.seniornet.cn/js/sjh5test/"
//正式web地址
const val BASE_WEB_RELEASE_URL = "https://www.seniornet.cn/js/sjh5/"
private var BASE_WEB_URL = BASE_WEB_RELEASE_URL

const val PHONE_NUMBER = "phone_number"
const val ADD_OR_MODIFY = "add_or_modify"
const val DETAIL_TYPE = "detail_type"

//申请入会
val APPLY_FOR_MEMBERSHIP = "${BASE_WEB_URL}pages/applayVIP/applayVIP"
//特卖
val SPECIAL_SALE = "${BASE_WEB_URL}pages/temai/temai"
//会费缴纳
val PAY_MEMBERSHIP = "${BASE_WEB_URL}pages/equityrecharge/equityrecharge2"
//禧世通协议
val VIP_AGREEMENT = "${BASE_WEB_URL}xstagreement/xstagreement"
//隐私政策
val PRIVACY_POLICY = "${BASE_WEB_URL}pages/hidexieyi/hidexieyi"
//用户协议
val USER_AGREEMENT = "${BASE_WEB_URL}pages/userxieyi/userxieyi"
//我的-规则说明
val RULE = "${BASE_WEB_URL}pages/guize/guize"
//限时秒杀
val LIMITED_SECKILL = "${BASE_WEB_URL}pages/temaidetail/temaidetail?goodId="
//列表每页数量
const val LIMIT = 10
//h5交互调用方法的名字
const val JS_NAME = "app"

const val CHOOSE = "choose"
const val MINE = "mine"

const val REQUEST_CODE_APPLYINVOICE = 100
const val RESULT_CODE_MYADDRESS = 200
const val REQUEST_CODE_EDIT = 300
const val RESULT_CODE_EDIT = 400