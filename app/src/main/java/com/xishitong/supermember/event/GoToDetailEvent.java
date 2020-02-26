package com.xishitong.supermember.event;

/**
 * author : zhangbao
 * date : 2020-02-17 17:10
 * description :
 */
public class GoToDetailEvent {
    private String url;

    public GoToDetailEvent(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "GoToDetailEvent{" +
                "url='" + url + '\'' +
                '}';
    }
}
