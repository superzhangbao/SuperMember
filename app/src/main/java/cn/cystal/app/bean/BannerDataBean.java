package cn.cystal.app.bean;

import cn.cystal.app.base.BaseModel;
import cn.cystal.app.base.BaseModel;

/**
 * author : zhangbao
 * date : 2020-02-11 12:24
 * description :
 */
public class BannerDataBean extends BaseModel {
    public BannerDataBean(String url) {
        this.url = url;
    }

    public String url;
}
