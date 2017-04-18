package com.advance.library.core;

import android.content.Context;
import android.content.Intent;
import com.advance.library.service.RequestService;

/**
 * Description: 初始化工具类 <br>
 * Creator: Hongd <br>
 * Date: 2017/3/22 16:48 <br>
 * Email: tobu794@163.com <br>
 * Version: 1.0 <br>
 */
public class AdvanceSDK {

  private static Context context;

  private AdvanceSDK() {
    throw new UnsupportedOperationException("u can't instantiate me...");
  }

  /**
   * 初始化工具类
   *
   * @param context 上下文
   */
  public static void init(Context context) {
    AdvanceSDK.context = context.getApplicationContext();
    Intent gary = new Intent(context, RequestService.class);
    context.startService(gary);
  }

  /**
   * 获取ApplicationContext
   *
   * @return ApplicationContext
   */
  public static Context getContext() {
    if (context != null) {
      return context;
    }
    throw new NullPointerException("u should init first");
  }
}
