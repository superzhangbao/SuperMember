package com.xishitong.supermember.bean;

import com.xishitong.supermember.base.BaseModel;

/**
 * author : zhangbao
 * date : 2020-02-12 23:26
 * description :
 */
public class UserInfoBean extends BaseModel {
    /**
     * data : {"money":254640900,"isMember":true}
     */

    public DataBean data;

    public static class DataBean {
        /**
         * money : 254640900
         * isMember : true
         */

        public int money;
        public boolean isMember;
    }
}
