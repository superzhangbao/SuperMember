package com.xishitong.supermember.bean

import com.xishitong.supermember.base.BaseModel

/**
 * author : zhangbao
 * date : 2020-02-13 00:00
 * description :
 */
class BalanceBean : BaseModel() {
    /**
     * data : {"list":[{"addTime":"2019-11-21 16:29:47","billId":"20191121154033748816872657","id":1,"money":5000100,"productName":"权益充值","type":1,"userPhone":"123"}],"totalCount":1}
     */
    var data: DataBean? = null

    class DataBean {
        /**
         * list : [{"addTime":"2019-11-21 16:29:47","billId":"20191121154033748816872657","id":1,"money":5000100,"productName":"权益充值","type":1,"userPhone":"123"}]
         * totalCount : 1
         */
        var totalCount = 0
        var list: List<ListBean>? = null

        class ListBean {
            /**
             * addTime : 2019-11-21 16:29:47
             * billId : 20191121154033748816872657
             * id : 1
             * money : 5000100
             * productName : 权益充值
             * type : 1
             * userPhone : 123
             */
            var addTime: String? = null
            var billId: String? = null
            var id = 0
            var money = 0
            var productName: String? = null
            var type = 0
            var userPhone: String? = null
        }
    }
}