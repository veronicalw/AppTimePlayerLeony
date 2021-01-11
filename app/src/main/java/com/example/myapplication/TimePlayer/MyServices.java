package com.example.myapplication.TimePlayer;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Notification;
import android.app.Notification.Builder;
import android.app.PendingIntent;
import android.content.Context;
import android.content.IntentFilter;
import android.widget.RemoteViews;

import com.example.myapplication.R;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

public class MyServices extends Service implements PropertyChangeListener {
    private static final String ACTION_PLAY	= "com.example.myapplication.play";
    private static final String ACTION_RESET = "com.example.myapplication.reset";
    private static final String ACTION_STOP = "com.example.myapplication.stop";

//    private static final int NOTIFICATION_ID = 590123562;
    private static final int NOTIFICATION_ID = 100;
    String notifyID ="channelid";

    private NotificationManager mNotificationManager;
    private boolean isNotificationShowing;
    private BroadcastReceiver receiverStateChanged;
    private BroadcastReceiver receiverResets;
    private BroadcastReceiver receiverExit;
    private Timer t;
    private NotificationCompat.Builder mBuilder;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isNotificationShowing = false;
        int importance = NotificationManager.IMPORTANCE_HIGH;
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel mChannel = new NotificationChannel(notifyID, "name", importance);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotificationManager.createNotificationChannel(mChannel);
        }
        mNotificationManager.getActiveNotifications();
        IntentFilter filterNext = new IntentFilter(ACTION_PLAY);
        receiverStateChanged = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(TimeContainer.getInstance().getCurrentState() == TimeContainer.STATE_RUNNING) {
                    TimeContainer.getInstance().pause();
                } else {
                    TimeContainer.getInstance().start();
                }
                updateNotification();
            }
        };
        registerReceiver(receiverStateChanged, filterNext);
        receiverResets = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                TimeContainer.getInstance().reset();
                updateNotification();
            }
        };
        IntentFilter filterPause = new IntentFilter(ACTION_RESET);
        registerReceiver(receiverResets, filterPause);
        IntentFilter filterPrev = new IntentFilter(ACTION_STOP);
        receiverExit = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                TimeContainer.getInstance().reset();
                stopForeground(true);
                isNotificationShowing = false;
                stopSelf();
            }
        };
        registerReceiver(receiverExit, filterPrev);
        startUpdateTimer();
        TimeContainer.getInstance().isServiceRunning.set(true);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.timer)
                    .setOngoing(true)
                    .setOnlyAlertOnce(true);

        }
        super.onCreate();
    }


    public void startUpdateTimer() {
        if(t != null) {
            t.cancel();
            t = null;
        }
        t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                updateNotification();
            }
        }, 0, 1000);
    }

    private synchronized void updateNotification() {
        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.custom_navigation);
        if(TimeContainer.getInstance().getCurrentState() == TimeContainer.STATE_RUNNING) {
            contentView.setImageViewResource(R.id.btnPlay, R.drawable.ic_launcher_blackpause_foreground);
        } else {
            contentView.setImageViewResource(R.id.btnPlay, R.drawable.ic_launcher_blackplay_foreground);
        }

        contentView.setTextViewText(R.id.textNotifTime, msToHourMinSec(TimeContainer.getInstance().getElapsedTime()) );

        Intent playIntent = new Intent(ACTION_PLAY, null);
        PendingIntent playPendingIntent = PendingIntent.getBroadcast(this, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent resetIntent = new Intent(ACTION_RESET, null);
        PendingIntent  resetPendingIntent = PendingIntent.getBroadcast(this, 0, resetIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent stopIntent = new Intent(ACTION_STOP, null);
        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(this, 0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        contentView.setOnClickPendingIntent(R.id.btnPlay, playPendingIntent);
        contentView.setOnClickPendingIntent(R.id.btnReset, resetPendingIntent);
        contentView.setOnClickPendingIntent(R.id.btnStop, stopPendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mBuilder.setContent(contentView);
            Intent notificationIntent = new Intent(this, TimePlayer.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent intent = PendingIntent.getActivity(this, PendingIntent.FLAG_UPDATE_CURRENT, notificationIntent, 0);
            mBuilder.setSmallIcon(R.drawable.timer);
            mBuilder.setContentIntent(intent);
            mBuilder.setChannelId(notifyID);
            mBuilder.build();
            if(isNotificationShowing) {
                mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
            } else {
                isNotificationShowing = true;
                startForeground(NOTIFICATION_ID,  mBuilder.build());
            }
        }
    }

    @Override
    public void onDestroy() {
        if(t != null) {
            t.cancel();
            t = null;
        }
        unregisterReceiver(receiverExit);
        unregisterReceiver(receiverResets);
        unregisterReceiver(receiverStateChanged);
        TimeContainer.getInstance().isServiceRunning.set(false);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static class TimeContainer {

        public static final int STATE_STOPPED = 0;
        public static final int STATE_PAUSED  = 1;
        public static final int STATE_RUNNING = 2;

        private static TimeContainer instance;
        public AtomicBoolean isServiceRunning;
        private PropertyChangeSupport observers;

        public static final String STATE_CHANGED = "state_changed";

        private int currentState;
        private long startTime;
        private long elapsedTime;

        private final Object mSynchronizedObject = new Object();

        private TimeContainer() {
            isServiceRunning = new AtomicBoolean(false);
            observers = new PropertyChangeSupport(this);
        }

        public void addObserver(PropertyChangeListener listener) {
            observers.addPropertyChangeListener(listener);
        }

        public void removeObserver(PropertyChangeListener listener) {
            observers.removePropertyChangeListener(listener);
        }

        public static TimeContainer getInstance() {
            if(instance == null) {
                instance = new TimeContainer();
            }
            return instance;
        }

        public void notifyStateChanged() {
            observers.firePropertyChange(STATE_CHANGED, null, currentState);
        }

        public int getCurrentState() {
            return currentState;
        }

        public long getStartTime() {
            return startTime;
        }

        public long getElapsedTime() {
            if(startTime == 0) {
                return elapsedTime;
            } else {
                return elapsedTime + (System.currentTimeMillis() - startTime);
            }
        }

        public void start() {
            synchronized (mSynchronizedObject) {
                startTime = System.currentTimeMillis();
                currentState = STATE_RUNNING;
                notifyStateChanged();
            }
        }

        public void reset() {
            synchronized (mSynchronizedObject) {
                if(currentState == STATE_RUNNING) {
                    startTime = System.currentTimeMillis();
                    elapsedTime = 0;
                    currentState = STATE_RUNNING;
                    notifyStateChanged();
                } else {
                    startTime = 0;
                    elapsedTime = 0;
                    currentState = STATE_STOPPED;
                    notifyStateChanged();
                }
            }
        }

        public void stopAndReset() {
            synchronized (mSynchronizedObject) {
                startTime = 0;
                elapsedTime = 0;
                currentState = STATE_STOPPED;
                notifyStateChanged();
            }
        }

        public void pause() {
            synchronized (mSynchronizedObject) {
                elapsedTime = elapsedTime + (System.currentTimeMillis() - startTime);
                startTime = 0;
                currentState = STATE_PAUSED;
                notifyStateChanged();
            }
        }

    }

    private String msToHourMinSec(long ms) {
        if(ms == 0) {
            return "00:00";
        } else {
            long seconds = (ms / 1000) % 60;
            long minutes = (ms / 1000) / 60;
            long hours = minutes / 60;

            StringBuilder sb = new StringBuilder();
            if(hours > 0) {
                sb.append(hours);
                sb.append(':');
            }
            if(minutes > 0) {
                minutes = minutes % 60;
                if(minutes >= 10) {
                    sb.append(minutes);
                } else {
                    sb.append(0);
                    sb.append(minutes);
                }
            } else {
                sb.append('0');
                sb.append('0');
            }
            sb.append(':');
            if(seconds > 0) {
                if(seconds >= 10) {
                    sb.append(seconds);
                } else {
                    sb.append(0);
                    sb.append(seconds);
                }
            } else {
                sb.append('0');
                sb.append('0');
            }
            return sb.toString();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if(event.getPropertyName() == TimeContainer.STATE_CHANGED) {
            startUpdateTimer();
        }
    }

}
