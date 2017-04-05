package com.advance.library.core;

import android.app.Application;
import android.content.Context;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.https.HttpsUtils;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import okhttp3.OkHttpClient;

/**
 * Created by Hongd on 2017/3/27.
 */

public class ApplicationInit extends Application {

  //上下文
  private static Context context;

  @Override
  public void onCreate() {
    super.onCreate();
    context = getApplicationContext();
    HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);
    OkHttpClient okHttpClient = new OkHttpClient.Builder()
        .connectTimeout(20000L, TimeUnit.MILLISECONDS)
        .readTimeout(20000L, TimeUnit.MILLISECONDS)
        .hostnameVerifier(new HostnameVerifier() {
          @Override
          public boolean verify(String hostname, SSLSession session) {
            return false;
          }
        })
        .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
        .build();
    OkHttpUtils.initClient(okHttpClient);
  }
}
