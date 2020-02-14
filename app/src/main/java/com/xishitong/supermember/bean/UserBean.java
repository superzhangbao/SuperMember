package com.xishitong.supermember.bean;

import com.xishitong.supermember.base.BaseModel;

/**
 * author : zhangbao
 * date : 2020-02-14 20:16
 * description :入会信息查询接口对应实体类
 */
public class UserBean extends BaseModel {

    /**
     * data : {"bankCard":null,"idCard":null,"userPhone":"18367827072","name":null}
     */

    public DataBean data;

    public static class DataBean {
        /**
         * bankCard : null
         * idCard : null
         * userPhone : 18367827072
         * name : null
         */

        public String bankCard;
        public String idCard;
        public String userPhone;
        public String name;
    }
}
