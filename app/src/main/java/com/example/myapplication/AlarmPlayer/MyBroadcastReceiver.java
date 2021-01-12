package com.example.myapplication.AlarmPlayer;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyBroadcastReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmControl.getInstance(context).playMusic();
        com.example.myapplication.AlarmPlayer.NotificationHelper notificationHelper = new com.example.myapplication.AlarmPlayer.NotificationHelper(context);
        Notification nb = notificationHelper.getChannelNotification();
        notificationHelper.getManager().notify(1, nb);
    }

}
