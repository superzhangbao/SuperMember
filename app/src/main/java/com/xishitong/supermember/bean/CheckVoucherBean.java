package com.xishitong.supermember.bean;

import com.xishitong.supermember.base.BaseModel;

/**
 * author : zhangbao
 * date : 2020-02-12 23:38
 * description :
 */
public class CheckVoucherBean extends BaseModel {
    /**
     * data : {"img":"www.baidu.comwww,www,www,aaa"}
     */

    public DataBean data;

    public static class DataBean {
        /**
         * img : www.baidu.comwww,www,www,aaa
         */

        public String img;
    }
}
