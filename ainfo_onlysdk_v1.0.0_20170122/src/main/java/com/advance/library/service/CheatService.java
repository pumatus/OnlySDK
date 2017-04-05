package com.advance.library.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by Hongd on 2016/11/18.
 */

public class CheatService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(1,new Notification());
        stopForeground(true);
        stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
