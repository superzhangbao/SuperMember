package com.xishitong.supermember.bean;

import java.util.List;

/**
 * author : zhangbao
 * date : 2020-02-11 17:40
 * description :
 */
public class CommonBean {
    public DataBean data;

    public static class DataBean {
        public DataBean(int totalCount, List<ListBean> list) {
            this.totalCount = totalCount;
            this.list = list;
        }

        /**
         * list : [{"addTime":"2019-12-17 17:24:56","detailed":"详细地址","gegion":"省市区","id":27,"idCard":null,"invoiceName":null,"name":"收件人","phone":"收件人手机","status":1,"updateTime":null,"userPhone":"13735"}]
         * totalCount : 3
         */

        public int totalCount;
        public List<ListBean> list;

        public static class ListBean {
            public ListBean(String imgUrl, String jumpUrl, String title) {
                this.imgUrl = imgUrl;
                this.jumpUrl = jumpUrl;
                this.title = title;
            }

            public String imgUrl;
            public String jumpUrl;
            public String title;

            @Override
            public String toString() {
                return "ListBean{" +
                        "imgUrl='" + imgUrl + '\'' +
                        ", jumpUrl='" + jumpUrl + '\'' +
                        ", title='" + title + '\'' +
                        '}';
            }
        }
    }
}
