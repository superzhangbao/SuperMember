package com.xishitong.supermember.bean;

import com.xishitong.supermember.base.BaseModel;

import java.util.List;

/**
 * author : zhangbao
 * date : 2020-02-12 15:57
 * description :
 */
public class OrderBean extends BaseModel {

    /**
     * data : {"list":[{"account":"18367827072","addTime":"2019-12-04 10:58:22","addressDetailed":null,"addressGegion":null,"addressName":null,"addressPhone":null,"amount":9980,"bankAccount":"杭州禧越网络科技有限公司","bankCard":"76780188000156712","bankName":"中国光大银行杭州朝晖支行","billId":"169756033","completeTime":null,"courierNumber":null,"deductAmount":0,"id":260,"imgUrl":"http://zhaogongbao.oss-cn-hangzhou.aliyuncs.com/market/sj/f19e275c-0917-4d69-9e72-802e8f879e76.,","orderType":1,"originalAmount":9980,"payType":"bank","processStatus":"yy","productId":"101","productImg":"https://www.seniornet.cn/images/lofter/quanyika.png","productName":"会费缴纳","productValue":10000,"reason":null,"saleRemark":null,"status":3,"updateTime":"2019-12-17 15:27:53","urlList":["http://zhaogongbao.oss-cn-hangzhou.aliyuncs.com/market/sj/f19e275c-0917-4d69-9e72-802e8f879e76."],"userPhone":"18367827072"}],"totalCount":179}
     */

    public DataBean data;

    public static class DataBean {
        public DataBean(int totalCount, List<ListBean> list) {
            this.totalCount = totalCount;
            this.list = list;
        }

        /**
         * list : [{"account":"18367827072","addTime":"2019-12-04 10:58:22","addressDetailed":null,"addressGegion":null,"addressName":null,"addressPhone":null,"amount":9980,"bankAccount":"杭州禧越网络科技有限公司","bankCard":"76780188000156712","bankName":"中国光大银行杭州朝晖支行","billId":"169756033","completeTime":null,"courierNumber":null,"deductAmount":0,"id":260,"imgUrl":"http://zhaogongbao.oss-cn-hangzhou.aliyuncs.com/market/sj/f19e275c-0917-4d69-9e72-802e8f879e76.,","orderType":1,"originalAmount":9980,"payType":"bank","processStatus":"yy","productId":"101","productImg":"https://www.seniornet.cn/images/lofter/quanyika.png","productName":"会费缴纳","productValue":10000,"reason":null,"saleRemark":null,"status":3,"updateTime":"2019-12-17 15:27:53","urlList":["http://zhaogongbao.oss-cn-hangzhou.aliyuncs.com/market/sj/f19e275c-0917-4d69-9e72-802e8f879e76."],"userPhone":"18367827072"}]
         * totalCount : 179
         */

        public int totalCount;
        public List<ListBean> list;

        public static class ListBean {
            public ListBean(String account, String addTime, int amount, String billId, int deductAmount, int orderType, int originalAmount, String productImg, String productName, int productValue, Object saleRemark, int status, String userPhone) {
                this.account = account;
                this.addTime = addTime;
                this.amount = amount;
                this.billId = billId;
                this.deductAmount = deductAmount;
                this.orderType = orderType;
                this.originalAmount = originalAmount;
                this.productImg = productImg;
                this.productName = productName;
                this.productValue = productValue;
                this.saleRemark = saleRemark;
                this.status = status;
                this.userPhone = userPhone;
            }

            /**
             * account : 18367827072
             * addTime : 2019-12-04 10:58:22
             * addressDetailed : null
             * addressGegion : null
             * addressName : null
             * addressPhone : null
             * amount : 9980
             * bankAccount : 杭州禧越网络科技有限公司
             * bankCard : 76780188000156712
             * bankName : 中国光大银行杭州朝晖支行
             * billId : 169756033
             * completeTime : null
             * courierNumber : null
             * deductAmount : 0
             * id : 260
             * imgUrl : http://zhaogongbao.oss-cn-hangzhou.aliyuncs.com/market/sj/f19e275c-0917-4d69-9e72-802e8f879e76.,
             * orderType : 1
             * originalAmount : 9980
             * payType : bank
             * processStatus : yy
             * productId : 101
             * productImg : https://www.seniornet.cn/images/lofter/quanyika.png
             * productName : 会费缴纳
             * productValue : 10000
             * reason : null
             * saleRemark : null
             * status : 3
             * updateTime : 2019-12-17 15:27:53
             * urlList : ["http://zhaogongbao.oss-cn-hangzhou.aliyuncs.com/market/sj/f19e275c-0917-4d69-9e72-802e8f879e76."]
             * userPhone : 18367827072
             */

            public String account;
            public String addTime;
            public Object addressDetailed;
            public Object addressGegion;
            public Object addressName;
            public Object addressPhone;
            public int amount;
            public String bankAccount;
            public String bankCard;
            public String bankName;
            public String billId;
            public Object completeTime;
            public Object courierNumber;
            public int deductAmount;
            public int id;
            public String imgUrl;
            public int orderType;
            public int originalAmount;
            public String payType;
            public String processStatus;
            public String productId;
            public String productImg;
            public String productName;
            public int productValue;
            public Object reason;
            public Object saleRemark;
            public int status;
            public String updateTime;
            public String userPhone;
            public List<String> urlList;
        }
    }
}
