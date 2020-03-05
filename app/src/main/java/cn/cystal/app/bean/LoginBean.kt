package cn.cystal.app.bean

import cn.cystal.app.base.BaseModel

/**
 * author : zhangbao
 * date : 2020-02-12 22:35
 * description :
 */
class LoginBean : BaseModel() {
    /**
     * data : {"token":"LsE6kZEsJXelCIEHxxwEqJgNcH0RzSdV"}
     */
    var data: DataBean? = null

    class DataBean {
        /**
         * token : LsE6kZEsJXelCIEHxxwEqJgNcH0RzSdV
         */
        var token: String? = null
        var isMember:Boolean = false
    }
}