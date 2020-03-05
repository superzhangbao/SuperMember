package cn.cystal.app.network;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * OKHttp3添加证书
 * Created by Administrator on 2016/11/1.
 */
public class HttpSLL {

    /*信任指定证书*/
//    public static SSLContext getSLLContext(){
//        SSLContext sslContext = null;
//        try {
//            CertificateFactory cf = CertificateFactory.getInstance("X.509");
//            /*把证书放到assets下面*/
//            InputStream cerInputStream = LdlApplication.getInstance().getAssets().open(HttpsConstant.LDL_CRT);
//            Certificate ca = cf.generateCertificate(cerInputStream);
//
//            String keyStoreType = KeyStore.getDefaultType();
//            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
//            keyStore.load(null, null);
//            keyStore.setCertificateEntry("ca", ca);
//
//            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
//            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
//            tmf.init(keyStore);
//            TrustManager[] trustManagers = tmf.getTrustManagers();
//
//            sslContext = SSLContext.getInstance("TLS");
//            sslContext.init(null, trustManagers, null);
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (KeyStoreException e) {
//            e.printStackTrace();
//        } catch (KeyManagementException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (CertificateException e) {
//            e.printStackTrace();
//        }
//        return  sslContext;
//    }

    /*信任全部证书*/
    public static SSLContext getAllSLLContext() {
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }}, new SecureRandom());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return  sslContext;
    }
}
