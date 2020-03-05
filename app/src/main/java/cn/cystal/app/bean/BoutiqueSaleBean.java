package cn.cystal.app.bean;

import cn.cystal.app.base.BaseModel;
import cn.cystal.app.base.BaseModel;

import java.util.List;

/**
 * author : zhangbao
 * date : 2020-02-21 14:42
 * description :
 */
public class BoutiqueSaleBean extends BaseModel {

    public List<DataBean> data;

    public static class DataBean {
        /**
         * addTime : 2020-02-17 15:52:49
         * enName : outUrl
         * icon : www.baidu.com
         * id : 2
         * name : 车主邦加油
         * parentName : outUrl
         * status : 1
         * updateTime : 2020-02-17 15:59:19
         * url : www.baidu.com
         */

        public String addTime;
        public String enName;
        public String icon;
        public int id;
        public String name;
        public String parentName;
        public int status;
        public String updateTime;
        public String url;
    }
}
