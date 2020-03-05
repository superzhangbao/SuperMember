package cn.cystal.app.bean;

import cn.cystal.app.base.BaseModel;
import cn.cystal.app.base.BaseModel;

import java.util.List;

/**
 * author : zhangbao
 * date : 2020-02-12 15:57
 * description :
 */
public class OrderBean extends BaseModel {


    /**
     * data : {"list":[{"account":"15056006309","addTime":"2020-02-15 02:26:15","addressDetailed":null,"addressGegion":null,"addressName":null,"addressPhone":null,"amount":33300,"bankAccount":"杭州禧越网络科技有限公司","bankCard":"372776674215","bankName":"中国银行杭州望湖支行","billId":"970969546","buyNum":1,"completeTime":null,"courierNumber":null,"deductAmount":0,"id":712,"imgUrl":"http://zhaogongbao.oss-cn-hangzhou.aliyuncs.com/market/xstvip/6066d4ca-da4f-453a-bde0-f701171280e4.","name":"张宝","orderType":1,"originalAmount":33300,"payOrderId":null,"payType":"bank","processStatus":"cw","productId":"100","productImg":"https://www.seniornet.cn/images/card_icon.png","productName":"会费缴纳-自定义金额","productValue":33366,"reason":null,"saleRemark":null,"status":3,"updateTime":"2020-02-15 02:27:41","urlList":["http://zhaogongbao.oss-cn-hangzhou.aliyuncs.com/market/xstvip/6066d4ca-da4f-453a-bde0-f701171280e4."],"userPhone":"15056006309"}],"totalCount":1}
     */

    public DataBean data;

    public static class DataBean {
        /**
         * list : [{"account":"15056006309","addTime":"2020-02-15 02:26:15","addressDetailed":null,"addressGegion":null,"addressName":null,"addressPhone":null,"amount":33300,"bankAccount":"杭州禧越网络科技有限公司","bankCard":"372776674215","bankName":"中国银行杭州望湖支行","billId":"970969546","buyNum":1,"completeTime":null,"courierNumber":null,"deductAmount":0,"id":712,"imgUrl":"http://zhaogongbao.oss-cn-hangzhou.aliyuncs.com/market/xstvip/6066d4ca-da4f-453a-bde0-f701171280e4.","name":"张宝","orderType":1,"originalAmount":33300,"payOrderId":null,"payType":"bank","processStatus":"cw","productId":"100","productImg":"https://www.seniornet.cn/images/card_icon.png","productName":"会费缴纳-自定义金额","productValue":33366,"reason":null,"saleRemark":null,"status":3,"updateTime":"2020-02-15 02:27:41","urlList":["http://zhaogongbao.oss-cn-hangzhou.aliyuncs.com/market/xstvip/6066d4ca-da4f-453a-bde0-f701171280e4."],"userPhone":"15056006309"}]
         * totalCount : 1
         */

        public int totalCount;
        public List<ListBean> list;

        public static class ListBean {
            /**
             * account : 15056006309
             * addTime : 2020-02-15 02:26:15
             * addressDetailed : null
             * addressGegion : null
             * addressName : null
             * addressPhone : null
             * amount : 33300
             * bankAccount : 杭州禧越网络科技有限公司
             * bankCard : 372776674215
             * bankName : 中国银行杭州望湖支行
             * billId : 970969546
             * buyNum : 1
             * completeTime : null
             * courierNumber : null
             * deductAmount : 0
             * id : 712
             * imgUrl : http://zhaogongbao.oss-cn-hangzhou.aliyuncs.com/market/xstvip/6066d4ca-da4f-453a-bde0-f701171280e4.
             * name : 张宝
             * orderType : 1
             * originalAmount : 33300
             * payOrderId : null
             * payType : bank
             * processStatus : cw
             * productId : 100
             * productImg : https://www.seniornet.cn/images/card_icon.png
             * productName : 会费缴纳-自定义金额
             * productValue : 33366
             * reason : null
             * saleRemark : null
             * status : 3
             * updateTime : 2020-02-15 02:27:41
             * urlList : ["http://zhaogongbao.oss-cn-hangzhou.aliyuncs.com/market/xstvip/6066d4ca-da4f-453a-bde0-f701171280e4."]
             * userPhone : 15056006309
             */

            public String account;
            public String addTime;
            public String addressDetailed;
            public String addressGegion;
            public String addressName;
            public String addressPhone;
            public int amount;
            public String bankAccount;
            public String bankCard;
            public String bankName;
            public String billId;
            public int buyNum;
            public String completeTime;
            public String courierNumber;
            public int deductAmount;
            public int id;
            public String imgUrl;
            public String name;
            public int orderType;
            public int originalAmount;
            public String payOrderId;
            public String payType;
            public String processStatus;
            public String productId;
            public String productImg;
            public String productName;
            public int productValue;
            public String reason;
            public String saleRemark;
            public int status;
            public String updateTime;
            public String userPhone;
            public List<String> urlList;

            @Override
            public String toString() {
                return "ListBean{" +
                        "account='" + account + '\'' +
                        ", addTime='" + addTime + '\'' +
                        ", addressDetailed='" + addressDetailed + '\'' +
                        ", addressGegion='" + addressGegion + '\'' +
                        ", addressName='" + addressName + '\'' +
                        ", addressPhone='" + addressPhone + '\'' +
                        ", amount=" + amount +
                        ", bankAccount='" + bankAccount + '\'' +
                        ", bankCard='" + bankCard + '\'' +
                        ", bankName='" + bankName + '\'' +
                        ", billId='" + billId + '\'' +
                        ", buyNum=" + buyNum +
                        ", completeTime='" + completeTime + '\'' +
                        ", courierNumber='" + courierNumber + '\'' +
                        ", deductAmount=" + deductAmount +
                        ", id=" + id +
                        ", imgUrl='" + imgUrl + '\'' +
                        ", name='" + name + '\'' +
                        ", orderType=" + orderType +
                        ", originalAmount=" + originalAmount +
                        ", payOrderId='" + payOrderId + '\'' +
                        ", payType='" + payType + '\'' +
                        ", processStatus='" + processStatus + '\'' +
                        ", productId='" + productId + '\'' +
                        ", productImg='" + productImg + '\'' +
                        ", productName='" + productName + '\'' +
                        ", productValue=" + productValue +
                        ", reason='" + reason + '\'' +
                        ", saleRemark='" + saleRemark + '\'' +
                        ", status=" + status +
                        ", updateTime='" + updateTime + '\'' +
                        ", userPhone='" + userPhone + '\'' +
                        ", urlList=" + urlList +
                        '}';
            }
        }
    }
}
