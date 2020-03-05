package cn.cystal.app.bean;

import cn.cystal.app.base.BaseModel;
import cn.cystal.app.base.BaseModel;

import java.util.List;

/**
 * author : zhangbao
 * date : 2020-02-10 12:52
 * description :
 */
public class MyAddressBean extends BaseModel {

    /**
     * code : 200
     * data : {"list":[{"addTime":"2019-12-17 17:24:56","detailed":"详细地址","gegion":"省市区","id":27,"idCard":null,"invoiceName":null,"name":"收件人","phone":"收件人手机","status":1,"updateTime":null,"userPhone":"13735"}],"totalCount":3}
     * message : SUCCESS
     */

    public DataBean data;

    public static class DataBean {

        /**
         * list : [{"addTime":"2019-12-17 17:24:56","detailed":"详细地址","gegion":"省市区","id":27,"idCard":null,"invoiceName":null,"name":"收件人","phone":"收件人手机","status":1,"updateTime":null,"userPhone":"13735"}]
         * totalCount : 3
         */

        public int totalCount;
        public List<ListBean> list;

        public static class ListBean {

            /**
             * addTime : 2019-12-17 17:24:56
             * detailed : 详细地址
             * gegion : 省市区
             * id : 27
             * idCard : null
             * invoiceName : null
             * name : 收件人
             * phone : 收件人手机
             * status : 1
             * updateTime : null
             * userPhone : 13735
             */

            public String addTime;
            public String detailed;
            public String gegion;
            public int id;
            public String idCard;
            public String invoiceName;
            public String name;
            public String phone;
            public int status;
            public String updateTime;
            public String userPhone;

            @Override
            public String toString() {
                return "ListBean{" +
                        "addTime='" + addTime + '\'' +
                        ", detailed='" + detailed + '\'' +
                        ", gegion='" + gegion + '\'' +
                        ", id=" + id +
                        ", idCard=" + idCard +
                        ", invoiceName=" + invoiceName +
                        ", name='" + name + '\'' +
                        ", phone='" + phone + '\'' +
                        ", status=" + status +
                        ", updateTime=" + updateTime +
                        ", userPhone='" + userPhone + '\'' +
                        '}';
            }
        }
    }
}
