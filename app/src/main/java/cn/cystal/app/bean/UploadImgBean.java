package cn.cystal.app.bean;

import cn.cystal.app.base.BaseModel;
import cn.cystal.app.base.BaseModel;

import java.util.List;

/**
 * author : zhangbao
 * date : 2020-02-15 18:27
 * description :
 */
public class UploadImgBean extends BaseModel {

    public List<DataBean> data;

    public static class DataBean {
        /**
         * path : http://zhaogongbao.oss-cn-hangzhou.aliyuncs.com/market/xstvip/808fbc58-e9bb-4c36-92e3-af89b9ae7918.jpeg
         * webUrl : http://zhaogongbao.oss-cn-hangzhou.aliyuncs.com/market/xstvip/808fbc58-e9bb-4c36-92e3-af89b9ae7918.jpeg
         */

        public String path;
        public String webUrl;
    }
}
