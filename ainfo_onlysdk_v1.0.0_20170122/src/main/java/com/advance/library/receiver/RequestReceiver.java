package com.advance.library.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import com.advance.library.service.RequestService;

/**
 * Created by Hongd on 2016/11/18.
 */

public class RequestReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        boolean isConnected = wifi != null && wifi.isConnectedOrConnecting() || mobile != null && mobile.isConnectedOrConnecting();
        if (isConnected) {
            Log.d("NetWork Available", "YES");
        } else {
            Log.d("NetWork Available", "NO");
        }


        if (intent.getStringExtra("startService") != null) {
            if (intent.getStringExtra("startService").equals("start")) {
                Intent startService = new Intent(context, RequestService.class);
                context.startService(startService);
            }
        } else {
            Intent i = new Intent(context, RequestService.class);
            context.startService(i);
        }
    }
}
