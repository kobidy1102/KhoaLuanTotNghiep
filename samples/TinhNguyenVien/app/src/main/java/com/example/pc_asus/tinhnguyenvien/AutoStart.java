package com.example.pc_asus.tinhnguyenvien;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AutoStart extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent myIntent = new Intent(context, CheckConnectionService.class);
        context.startService(myIntent);
    }
}