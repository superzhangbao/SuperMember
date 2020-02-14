package com.xishitong.supermember.event;

/**
 * author : zhangbao
 * date : 2020-02-13 19:23
 * description :
 */
public class WebEvent {
    private String url;
    private String title;
    private String token;

    public WebEvent(String url,String title, String token) {
        this.url = url;
        this.title = title;
        this.token = token;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
                ", title='" + title + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
