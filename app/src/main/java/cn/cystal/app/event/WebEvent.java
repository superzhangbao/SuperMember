package cn.cystal.app.event;

/**
 * author : zhangbao
 * date : 2020-02-13 19:23
 * description :
 */
public class WebEvent {
    private String url;

    public WebEvent(String url) {
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
        return "WebEvent{" +
                "url='" + url + '\'' +
                '}';
    }
}
