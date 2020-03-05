package cn.cystal.app.bean;

import java.util.List;

/**
 * author : zhangbao
 * date : 2020-02-11 19:50
 * description :
 */
public class CommonBeanEvent {
    public CommonBeanEvent(List<CommonBean.DataBean.ListBean> list) {
        this.list = list;
    }

    public List<CommonBean.DataBean.ListBean> list;

}
