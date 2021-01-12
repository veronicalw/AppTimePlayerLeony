package com.example.myapplication.AlarmPlayer;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class DismissBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("dismiss", "dismiss");
        AlarmControl.getInstance(context).stopMusic();
        // do your code here...
    }

}
