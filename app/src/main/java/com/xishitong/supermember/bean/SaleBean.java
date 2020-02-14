package com.xishitong.supermember.bean;

import com.xishitong.supermember.base.BaseModel;

import java.util.List;

/**
 * author : zhangbao
 * date : 2020-02-12 23:21
 * description :
 */
public class SaleBean extends BaseModel {
    /**
     * data : {"list":[{"deductAmount":50,"detailImgList":["详1","详2"],"endTime":"2019-12-17 00:50:35","headImgList":["头1","头2"],"marketAmount":90,"originalAmount":100,"productDescription":"描述","productName":"名称","startTime":"2019-12-16 00:50:35","status":2}],"totalCount":5}
     */

    public DataBean data;

    public static class DataBean {
        /**
         * list : [{"deductAmount":50,"detailImgList":["详1","详2"],"endTime":"2019-12-17 00:50:35","headImgList":["头1","头2"],"marketAmount":90,"originalAmount":100,"productDescription":"描述","productName":"名称","startTime":"2019-12-16 00:50:35","status":2}]
         * totalCount : 5
         */

        public int totalCount;
        public List<ListBean> list;

        public static class ListBean {
            public ListBean(int deductAmount, String endTime, int marketAmount, int originalAmount, String productDescription, String productName, String startTime, int status, List<String> detailImgList, List<String> headImgList) {
                this.deductAmount = deductAmount;
                this.endTime = endTime;
                this.marketAmount = marketAmount;
                this.originalAmount = originalAmount;
                this.productDescription = productDescription;
                this.productName = productName;
                this.startTime = startTime;
                this.status = status;
                this.detailImgList = detailImgList;
                this.headImgList = headImgList;
            }

            /**
             * deductAmount : 50
             * detailImgList : ["详1","详2"]
             * endTime : 2019-12-17 00:50:35
             * headImgList : ["头1","头2"]
             * marketAmount : 90
             * originalAmount : 100
             * productDescription : 描述
             * productName : 名称
             * startTime : 2019-12-16 00:50:35
             * status : 2
             */

            public int deductAmount;
            public String endTime;
            public int marketAmount;
            public int originalAmount;
            public String productDescription;
            public String productName;
            public String startTime;
            public int status;
            public List<String> detailImgList;
            public List<String> headImgList;
        }
    }
}
