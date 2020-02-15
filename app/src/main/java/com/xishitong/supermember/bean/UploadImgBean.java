package com.xishitong.supermember.bean;

import com.xishitong.supermember.base.BaseModel;

/**
 * author : zhangbao
 * date : 2020-02-15 18:27
 * description :
 */
public class UploadImgBean extends BaseModel {

    /**
     * data : {"path":"http://zhaogongbao.oss-cn-hangzhou.aliyuncs.com/test/6d00b667-ef0d-45ae-87be-af525e4f29c0.jpg","webUrl":"http://zhaogongbao.oss-cn-hangzhou.aliyuncs.com/test/6d00b667-ef0d-45ae-87be-af525e4f29c0.jpg"}
     */

    public DataBean data;

    public static class DataBean {
        /**
         * path : http://zhaogongbao.oss-cn-hangzhou.aliyuncs.com/test/6d00b667-ef0d-45ae-87be-af525e4f29c0.jpg
         * webUrl : http://zhaogongbao.oss-cn-hangzhou.aliyuncs.com/test/6d00b667-ef0d-45ae-87be-af525e4f29c0.jpg
         */

        public String path;
        public String webUrl;
    }
}
