package com.muhammaddaniyal.bloodbank;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class MyService extends Service {


    private static final String TAG = "MyService";
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        CountDownTimer cdt=new CountDownTimer(60000,1000) {
            @Override
            public void onTick(long l) {
                Log.e(TAG, "onTick: "+l/1000 );
            }

            @Override
            public void onFinish() {
                Log.e(TAG, "onFinish: " );

            }
        }.start();
        return START_STICKY;
    }
}
