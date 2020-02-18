package com.xishitong.supermember.network;

import android.net.ParseException;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.xishitong.supermember.base.BaseModel;
import com.xishitong.supermember.event.LogoutEvent;
import com.xishitong.supermember.util.LogUtil;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import retrofit2.HttpException;

import java.io.EOFException;
import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.UnknownHostException;

public abstract class BaseObserver<T extends BaseModel> implements Observer<T> {
    /**
     * 于服务器约定  返回code为几是正常请求
     */
    private static final int CODE = 0;

    /**
     * 网络连接失败  无网
     */
    private static final int NETWORK_ERROR = 100000;
    /**
     * 解析数据失败
     */
    private static final int PARSE_ERROR = 1008;
    /**
     * 网络问题
     */
    private static final int BAD_NETWORK = 1007;
    /**
     * 连接错误
     */
    private static final int CONNECT_ERROR = 1006;
    /**
     * 连接超时
     */
    private static final int CONNECT_TIMEOUT = 1005;

    @Override
    public void onSubscribe(Disposable d) {
    }

    @Override
    public void onNext(T t) {
        if (t.code == 200) {
            onSuccess(t);
        }else if (t.code == 401) {
            EventBus.getDefault().post(new LogoutEvent());
            onError(t.message);
        }else {
            onError(t.message);
        }
    }

    @Override
    public void onError(Throwable e) {
        if (e instanceof HttpException) {
            ResponseBody responseBody = ((HttpException) e).response().errorBody();
            if (responseBody == null) {
                onError("连接出错");
                return;
            }
            Gson gson = new Gson();
            try {
                String string = responseBody.string();
                LogUtil.INSTANCE.e("error",string);
                BaseModel baseModel = gson.fromJson(string, BaseModel.class);
                onError(baseModel.message);
            } catch (Exception e1) {
                e1.printStackTrace();
                //HTTP错误
                onExceptions(BAD_NETWORK, "");
            }
        } else if (e instanceof ConnectException
                || e instanceof UnknownHostException) {
            //连接错误
            onExceptions(CONNECT_ERROR, "");
        } else if (e instanceof InterruptedIOException) {
            //连接超时
            onExceptions(CONNECT_TIMEOUT, "");
        } else if (e instanceof JsonParseException
                || e instanceof JSONException
                || e instanceof ParseException) {
            //解析错误
            onExceptions(PARSE_ERROR, "");
            e.printStackTrace();
        }else if (e instanceof EOFException) {
            onSuccess(null);
        }
        else {
            onError(e.toString());
        }
    }

    @Override
    public void onComplete() {

    }

    public abstract void onSuccess(T t);

    public abstract void onError(String msg);

    private void onExceptions(int unknownError, String message) {
        switch (unknownError) {
            case CONNECT_ERROR:
                onError("网络不可用，请检查网络连接！");
                break;
            case CONNECT_TIMEOUT:
                onError("连接超时");
                break;
            case BAD_NETWORK:
                onError("网络超时");
                break;
            case PARSE_ERROR:
                onError("数据解析失败");
                break;
            //网络不可用
            case NETWORK_ERROR:
                onError("网络不可用，请检查网络连接！");
                break;
            default:
                onError("未知错误");
                break;
        }
    }

}
