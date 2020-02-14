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
     * data : {"list":[{"deductAmount":100,"detailImgList":["http://zhaogongbao.oss-cn-hangzhou.aliyuncs.com/market/null/94a0ffb0-0408-427c-9254-a184a0e8b9a0.jpg"],"endTime":"2020-02-14 23:59:59","headImgList":["http://zhaogongbao.oss-cn-hangzhou.aliyuncs.com/market/null/cff422a7-522d-4dd4-986b-252c83c90b48."],"id":75,"marketAmount":100,"numbers":1,"originalAmount":100,"productDescription":"时间商品","productName":"时间商品","saleTotal":0,"startTime":"2020-02-14 14:00:00","status":1},{"deductAmount":20000,"detailImgList":["http://zhaogongbao.oss-cn-hangzhou.aliyuncs.com/market/null/6ec7e52b-c674-4943-bb01-021a360f8b5f.jpg"],"endTime":"2020-02-14 23:59:59","headImgList":["http://zhaogongbao.oss-cn-hangzhou.aliyuncs.com/market/null/4e2965e4-11c3-449f-a87f-43b3f9c4ab09.","http://zhaogongbao.oss-cn-hangzhou.aliyuncs.com/market/null/de4ef807-2637-40cb-b47c-6fd5b4079d83.","http://zhaogongbao.oss-cn-hangzhou.aliyuncs.com/market/null/69a6eefb-c1bc-4698-b180-19fe2b5b98ae."],"id":57,"marketAmount":380000,"numbers":2,"originalAmount":369900,"productDescription":"手机操控 低温净味 隐藏门把手","productName":"Haier/海尔 BCD-572WDENU1 智能变频双开门","saleTotal":0,"startTime":"2020-02-14 14:00:00","status":1},{"deductAmount":20000,"detailImgList":["http://zhaogongbao.oss-cn-hangzhou.aliyuncs.com/market/null/6ec7e52b-c674-4943-bb01-021a360f8b5f.jpg"],"endTime":"2020-02-14 23:59:59","headImgList":["http://zhaogongbao.oss-cn-hangzhou.aliyuncs.com/market/null/4e2965e4-11c3-449f-a87f-43b3f9c4ab09.","http://zhaogongbao.oss-cn-hangzhou.aliyuncs.com/market/null/de4ef807-2637-40cb-b47c-6fd5b4079d83."],"id":51,"marketAmount":380000,"numbers":3,"originalAmount":369900,"productDescription":"手机操控 低温净味 隐藏门把手","productName":"Haier/海尔 BCD-572WDENU1 智能变频双开门","saleTotal":0,"startTime":"2020-02-14 14:00:00","status":1},{"deductAmount":20000,"detailImgList":["http://zhaogongbao.oss-cn-hangzhou.aliyuncs.com/market/null/6ec7e52b-c674-4943-bb01-021a360f8b5f.jpg"],"endTime":"2020-02-14 23:59:59","headImgList":["http://zhaogongbao.oss-cn-hangzhou.aliyuncs.com/market/null/4e2965e4-11c3-449f-a87f-43b3f9c4ab09."],"id":63,"marketAmount":380000,"numbers":11,"originalAmount":369900,"productDescription":"手机操控 低温净味 隐藏门把手","productName":"Haier/海尔 BCD-572WDENU1 智能变频双开门","saleTotal":0,"startTime":"2020-02-14 14:00:00","status":1},{"deductAmount":152,"detailImgList":["http://zhaogongbao.oss-cn-hangzhou.aliyuncs.com/market/null/4a5af3f8-2352-4974-a61b-c30adca28c9a.png"],"endTime":"2020-02-14 23:59:59","headImgList":["http://zhaogongbao.oss-cn-hangzhou.aliyuncs.com/market/null/183a434d-5288-42ba-b811-4289c3ee3d50."],"id":17,"marketAmount":2568,"numbers":99,"originalAmount":2311,"productDescription":"2","productName":"2","saleTotal":0,"startTime":"2020-02-14 14:00:00","status":1},{"deductAmount":300,"detailImgList":["http://zhaogongbao.oss-cn-hangzhou.aliyuncs.com/market/null/59dcc105-9a99-45fd-9c73-e52f9e6ea618.png"],"endTime":"2020-02-14 23:59:59","headImgList":["http://zhaogongbao.oss-cn-hangzhou.aliyuncs.com/market/null/51c164b4-9d98-4d37-abc5-6bb713ac7997."],"id":18,"marketAmount":300,"numbers":99,"originalAmount":300,"productDescription":"3","productName":"3","saleTotal":0,"startTime":"2020-02-14 14:00:00","status":1}],"totalCount":8}
     */

    public DataBean data;

    public static class DataBean {
        /**
         * list : [{"deductAmount":100,"detailImgList":["http://zhaogongbao.oss-cn-hangzhou.aliyuncs.com/market/null/94a0ffb0-0408-427c-9254-a184a0e8b9a0.jpg"],"endTime":"2020-02-14 23:59:59","headImgList":["http://zhaogongbao.oss-cn-hangzhou.aliyuncs.com/market/null/cff422a7-522d-4dd4-986b-252c83c90b48."],"id":75,"marketAmount":100,"numbers":1,"originalAmount":100,"productDescription":"时间商品","productName":"时间商品","saleTotal":0,"startTime":"2020-02-14 14:00:00","status":1},{"deductAmount":20000,"detailImgList":["http://zhaogongbao.oss-cn-hangzhou.aliyuncs.com/market/null/6ec7e52b-c674-4943-bb01-021a360f8b5f.jpg"],"endTime":"2020-02-14 23:59:59","headImgList":["http://zhaogongbao.oss-cn-hangzhou.aliyuncs.com/market/null/4e2965e4-11c3-449f-a87f-43b3f9c4ab09.","http://zhaogongbao.oss-cn-hangzhou.aliyuncs.com/market/null/de4ef807-2637-40cb-b47c-6fd5b4079d83.","http://zhaogongbao.oss-cn-hangzhou.aliyuncs.com/market/null/69a6eefb-c1bc-4698-b180-19fe2b5b98ae."],"id":57,"marketAmount":380000,"numbers":2,"originalAmount":369900,"productDescription":"手机操控 低温净味 隐藏门把手","productName":"Haier/海尔 BCD-572WDENU1 智能变频双开门","saleTotal":0,"startTime":"2020-02-14 14:00:00","status":1},{"deductAmount":20000,"detailImgList":["http://zhaogongbao.oss-cn-hangzhou.aliyuncs.com/market/null/6ec7e52b-c674-4943-bb01-021a360f8b5f.jpg"],"endTime":"2020-02-14 23:59:59","headImgList":["http://zhaogongbao.oss-cn-hangzhou.aliyuncs.com/market/null/4e2965e4-11c3-449f-a87f-43b3f9c4ab09.","http://zhaogongbao.oss-cn-hangzhou.aliyuncs.com/market/null/de4ef807-2637-40cb-b47c-6fd5b4079d83."],"id":51,"marketAmount":380000,"numbers":3,"originalAmount":369900,"productDescription":"手机操控 低温净味 隐藏门把手","productName":"Haier/海尔 BCD-572WDENU1 智能变频双开门","saleTotal":0,"startTime":"2020-02-14 14:00:00","status":1},{"deductAmount":20000,"detailImgList":["http://zhaogongbao.oss-cn-hangzhou.aliyuncs.com/market/null/6ec7e52b-c674-4943-bb01-021a360f8b5f.jpg"],"endTime":"2020-02-14 23:59:59","headImgList":["http://zhaogongbao.oss-cn-hangzhou.aliyuncs.com/market/null/4e2965e4-11c3-449f-a87f-43b3f9c4ab09."],"id":63,"marketAmount":380000,"numbers":11,"originalAmount":369900,"productDescription":"手机操控 低温净味 隐藏门把手","productName":"Haier/海尔 BCD-572WDENU1 智能变频双开门","saleTotal":0,"startTime":"2020-02-14 14:00:00","status":1},{"deductAmount":152,"detailImgList":["http://zhaogongbao.oss-cn-hangzhou.aliyuncs.com/market/null/4a5af3f8-2352-4974-a61b-c30adca28c9a.png"],"endTime":"2020-02-14 23:59:59","headImgList":["http://zhaogongbao.oss-cn-hangzhou.aliyuncs.com/market/null/183a434d-5288-42ba-b811-4289c3ee3d50."],"id":17,"marketAmount":2568,"numbers":99,"originalAmount":2311,"productDescription":"2","productName":"2","saleTotal":0,"startTime":"2020-02-14 14:00:00","status":1},{"deductAmount":300,"detailImgList":["http://zhaogongbao.oss-cn-hangzhou.aliyuncs.com/market/null/59dcc105-9a99-45fd-9c73-e52f9e6ea618.png"],"endTime":"2020-02-14 23:59:59","headImgList":["http://zhaogongbao.oss-cn-hangzhou.aliyuncs.com/market/null/51c164b4-9d98-4d37-abc5-6bb713ac7997."],"id":18,"marketAmount":300,"numbers":99,"originalAmount":300,"productDescription":"3","productName":"3","saleTotal":0,"startTime":"2020-02-14 14:00:00","status":1}]
         * totalCount : 8
         */

        public int totalCount;
        public List<ListBean> list;

        public static class ListBean {
            /**
             * deductAmount : 100
             * detailImgList : ["http://zhaogongbao.oss-cn-hangzhou.aliyuncs.com/market/null/94a0ffb0-0408-427c-9254-a184a0e8b9a0.jpg"]
             * endTime : 2020-02-14 23:59:59
             * headImgList : ["http://zhaogongbao.oss-cn-hangzhou.aliyuncs.com/market/null/cff422a7-522d-4dd4-986b-252c83c90b48."]
             * id : 75
             * marketAmount : 100
             * numbers : 1
             * originalAmount : 100
             * productDescription : 时间商品
             * productName : 时间商品
             * saleTotal : 0
             * startTime : 2020-02-14 14:00:00
             * status : 1
             */

            public int deductAmount;
            public String endTime;
            public int id;
            public int marketAmount;
            public int numbers;
            public int originalAmount;
            public String productDescription;
            public String productName;
            public int saleTotal;
            public String startTime;
            public int status;
            public List<String> detailImgList;
            public List<String> headImgList;
        }
    }
}
