package com.xishitong.supermember.event;

/**
 * author : zhangbao
 * date : 2020-02-17 17:10
 * description :
 */
public class GoToDetailEvent {
    private String url;
    private Boolean needToken;

    public GoToDetailEvent(String url, Boolean needToken) {
        this.url = url;
        this.needToken = needToken;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getNeedToken() {
        return needToken;
    }

    public void setNeedToken(Boolean needToken) {
        this.needToken = needToken;
    }

    @Override
    public String toString() {
        return "GoToDetailEvent{" +
                "url='" + url + '\'' +
                ", needToken=" + needToken +
                '}';
    }
}
