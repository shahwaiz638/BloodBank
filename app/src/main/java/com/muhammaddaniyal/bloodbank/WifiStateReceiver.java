package com.muhammaddaniyal.bloodbank;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.widget.Toast;

public class WifiStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int wifiStateExtra = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
                WifiManager.WIFI_STATE_UNKNOWN);


        if (wifiStateExtra == WifiManager.WIFI_STATE_ENABLED) {
            Toast.makeText(context, "Wifi is on!", Toast.LENGTH_SHORT).show();
        } else if (wifiStateExtra == WifiManager.WIFI_STATE_DISABLED) {
            Toast.makeText(context, "Wifi is off, Please connect to Wifi", Toast.LENGTH_SHORT).show();
        }
    }


}
