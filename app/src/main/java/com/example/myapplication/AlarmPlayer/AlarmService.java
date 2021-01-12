package com.example.myapplication.AlarmPlayer;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class AlarmService extends Service {

    private MediaPlayer mp;

    public static final String URI_BASE = com.example.myapplication.AlarmPlayer.AlarmService.class.getName() + ".";
    public static final String ACTION_DISMISS = URI_BASE + "ACTION_DISMISS";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        String action = intent.getAction();
        if (ACTION_DISMISS.equals(action)) {
            Log.d("Dismisss", "Here");
            dismiss();

        }

        return START_NOT_STICKY;

    }

    private void dismiss() {
        Intent intent = new Intent(this, com.example.myapplication.AlarmPlayer.AlarmService.class);
        stopService(intent);

        intent = new Intent(this, MyBroadcastReceiver.class);
        PendingIntent pendingIntent= PendingIntent.getBroadcast(this,0,intent,PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmM= (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmM.cancel(pendingIntent);

        NotificationManager notiMana= (NotificationManager) getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
        notiMana.cancel(321);
//        mp = NotificationHelper.m;
        mp.stop();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mp.stop();
    }

    private void snooze() {
    }
}
