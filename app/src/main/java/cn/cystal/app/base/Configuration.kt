package cn.cystal.app.base

import cn.cystal.app.BuildConfig

/**
 *  author : zhangbao
 *  date : 2020-02-08 20:04
 *  description :基础信息配置类
 */
//const val BASE_URL = "http://47.99.80.137:8087/"
const val BASE_URL = "https://xishi.seniornet.cn/"
const val PHONE_NUMBER = "phone_number"
const val ADD_OR_MODIFY = "add_or_modify"
const val DETAIL_TYPE = "detail_type"
//测试web地址
//const val WEB_BASE_URL = "http://www.seniornet.cn/js/sjh5test/"
//正式web地址
const val WEB_BASE_URL = "https://www.seniornet.cn/js/sjh5/"
//申请入会
const val APPLY_FOR_MEMBERSHIP = "${WEB_BASE_URL}pages/applayVIP/applayVIP"
//特卖
const val SPECIAL_SALE = "${WEB_BASE_URL}pages/temai/temai"
//会费缴纳
const val PAY_MEMBERSHIP = "${WEB_BASE_URL}pages/equityrecharge/equityrecharge2"
//禧世通协议
const val VIP_AGREEMENT = "${WEB_BASE_URL}xstagreement/xstagreement"
//隐私政策
const val PRIVACY_POLICY = "${WEB_BASE_URL}pages/hidexieyi/hidexieyi"
//用户协议
const val USER_AGREEMENT = "${WEB_BASE_URL}pages/userxieyi/userxieyi"
//我的-规则说明
const val RULE = "${WEB_BASE_URL}pages/guize/guize"
//限时秒杀
const val LIMITED_SECKILL = "${WEB_BASE_URL}pages/temaidetail/temaidetail?goodId="
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