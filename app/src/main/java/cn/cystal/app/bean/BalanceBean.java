package cn.cystal.app.bean;

import cn.cystal.app.base.BaseModel;

import java.util.List;

/**
 * author : zhangbao
 * date : 2020-02-14 21:28
 * description :
 */
public class BalanceBean extends BaseModel {

    /**
     * data : {"list":[{"addTime":"2020-02-14 19:58:49","amount":0.01,"billId":"076113486","courierNumber":null,"id":573,"money":1,"productId":"100","productImg":"https://www.seniornet.cn/images/card_icon.png","productName":"会费缴纳(转账)","status":1,"type":1,"userPhone":"15056006309"},{"addTime":"2020-02-14 19:58:47","amount":100,"billId":"329580613","courierNumber":null,"id":572,"money":10020,"productId":"100","productImg":"https://www.seniornet.cn/images/card_icon.png","productName":"会费缴纳(转账)","status":0,"type":1,"userPhone":"15056006309"}],"totalCount":2}
     */

    public DataBean data;

    public static class DataBean {
        /**
         * list : [{"addTime":"2020-02-14 19:58:49","amount":0.01,"billId":"076113486","courierNumber":null,"id":573,"money":1,"productId":"100","productImg":"https://www.seniornet.cn/images/card_icon.png","productName":"会费缴纳(转账)","status":1,"type":1,"userPhone":"15056006309"},{"addTime":"2020-02-14 19:58:47","amount":100,"billId":"329580613","courierNumber":null,"id":572,"money":10020,"productId":"100","productImg":"https://www.seniornet.cn/images/card_icon.png","productName":"会费缴纳(转账)","status":0,"type":1,"userPhone":"15056006309"}]
         * totalCount : 2
         */

        public int totalCount;
        public List<ListBean> list;

        public static class ListBean {
            /**
             * addTime : 2020-02-14 19:58:49
             * amount : 0.01
             * billId : 076113486
             * courierNumber : null
             * id : 573
             * money : 1
             * productId : 100
             * productImg : https://www.seniornet.cn/images/card_icon.png
             * productName : 会费缴纳(转账)
             * status : 1
             * type : 1
             * userPhone : 15056006309
             */

            public String addTime;
            public Double amount;
            public String billId;
            public String courierNumber;
            public int id;
            public int money;
            public String productId;
            public String productImg;
            public String productName;
            public int status;
            public int type;
            public String userPhone;
        }
    }
}
