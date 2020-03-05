package com.xishitong.supermember.network;

import com.xishitong.supermember.BuildConfig;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

import static com.xishitong.supermember.base.ConfigurationKt.RELEASE_BASE_URL;

public class NetClient {

    private static int CONNECT_TIMEOUT = 5;
    private static int WRITE_TIMEOUT = 5;
    private static int READ_TIMEOUT = 5;
    private static boolean RETRY_ONCONNECTION_FAILURE = false;
    private static String BASE_URL = RELEASE_BASE_URL;
    private Retrofit mRetrofit;


    private NetClient() {
    }

    public static NetClient getInstance() {
        return NetClientHolder.NET_CLIENT;
    }

    private static class NetClientHolder {
        private static final NetClient NET_CLIENT = new NetClient();
    }

    public void init() {
        OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            okHttpBuilder.addInterceptor(loggingInterceptor);
        }

        OkHttpClient okHttpClient = okHttpBuilder
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)//连接 超时时间
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)//写操作 超时时间
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)//读操作 超时时间
                .retryOnConnectionFailure(RETRY_ONCONNECTION_FAILURE)
//                .sslSocketFactory(HttpSLL.getAllSLLContext().getSocketFactory())          //忽略https证书
                .build();
        mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    public NetClient baseUrl(String url) {
        BASE_URL = url;
        return this;
    }

    public NetClient connectTimeOut(int connectTtime) {
        CONNECT_TIMEOUT = connectTtime;
        return this;
    }

    public NetClient writeTimeOut(int writeTimeOut) {
        WRITE_TIMEOUT = writeTimeOut;
        return this;
    }

    public NetClient readTimeOut(int readTimeOut) {
        READ_TIMEOUT = readTimeOut;
        return this;
    }

    public NetClient retryOnConnectionFailure(boolean b) {
        RETRY_ONCONNECTION_FAILURE = b;
        return this;
    }

    /**
     * 获取对应的Service
     */
    public <T> T create(Class<T> service) {
        return mRetrofit.create(service);
    }
}
