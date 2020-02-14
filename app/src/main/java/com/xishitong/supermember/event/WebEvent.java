package com.xishitong.supermember.event;

/**
 * author : zhangbao
 * date : 2020-02-13 19:23
 * description :
 */
public class WebEvent {
    private String url;
    private String token;

    public WebEvent(String url, String token) {
        this.url = url;
        this.token = token;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "WebEvent{" +
                "url='" + url + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
