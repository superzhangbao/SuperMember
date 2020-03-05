package cn.cystal.app.bean;

import cn.cystal.app.base.BaseModel;

import java.util.List;

/**
 * author : zhangbao
 * date : 2020-02-12 23:16
 * description :
 */
public class BannerBean extends BaseModel {

    public List<DataBean> data;

    public static class DataBean {
        /**
         * addTime : null
         * enName : phone
         * endTime : null
         * id : null
         * pages : null
         * parentName : phone
         * photo : http://zhaogongbao.oss-cn-hangzhou.aliyuncs.com/market/null/6db409cb-ed03-487b-b8b6-a3ee4d1362b5.jpg
         * remark : null
         * startTime : null
         * status : null
         * title : null
         * updateTime : null
         */

        public Object addTime;
        public String enName;
        public Object endTime;
        public Object id;
        public Object pages;
        public String parentName;
        public String photo;
        public Object remark;
        public Object startTime;
        public Object status;
        public Object title;
        public Object updateTime;
    }
}
