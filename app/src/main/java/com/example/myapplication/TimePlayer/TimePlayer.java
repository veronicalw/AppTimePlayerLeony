package com.example.myapplication.TimePlayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.myapplication.R;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Timer;
import java.util.TimerTask;
import com.example.myapplication.TimePlayer.MyServices.TimeContainer;
public class TimePlayer extends AppCompatActivity implements PropertyChangeListener {
    TextView chronometer;
    ImageButton btnStart, btnStop;
    Timer timer;

    public static Handler handler;

    public final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            updateTimerText();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_player);

        chronometer = findViewById(R.id.timerChronometer);
        btnStart = findViewById(R.id.btnTimerChronoStart);
        btnStop = findViewById(R.id.btnTimerChronoStop);
        handler = new Handler();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    protected void onResume() {
        checkServiewRunning();
        TimeContainer timeContainer = TimeContainer.getInstance();
        if (timeContainer.getCurrentState() == TimeContainer.STATE_RUNNING) {
            btnStart.setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher_pause_foreground));
            startUpdateTimer();
        } else {
            btnStart.setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher_play_foreground));
            updateTimerText();
        }
        TimeContainer.getInstance().addObserver(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        TimeContainer.getInstance().removeObserver(this);
        super.onPause();
    }

    private void checkServiewRunning() {
        if (!TimeContainer.getInstance().isServiceRunning.get()) {
            startService(new Intent(this, MyServices.class));
        }
    }

    public void changeState(View view) {
        TimeContainer timeContainer = TimeContainer.getInstance();
        if (timeContainer.getCurrentState() == TimeContainer.STATE_RUNNING) {
            TimeContainer.getInstance().pause();
            btnStart.setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher_play_foreground));
        } else {
            TimeContainer.getInstance().start();
            startUpdateTimer();
            btnStart.setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher_resume_foreground));
        }
    }

    public void reset(View view) {
        TimeContainer.getInstance().reset();
        updateTimerText();
    }

    private void updateTimerText(){
        chronometer.setText(getTimeString(TimeContainer.getInstance().getElapsedTime()));
    }

    public void startUpdateTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(runnable);
            }
        }, 0, 16);
    }

    private String getTimeString(long miliSendods) {
        if (miliSendods == 0) {
            return "00:00:00";
        } else {
            StringBuilder builder = new StringBuilder();
            long milliSecond = (miliSendods % 1000) / 10;
            long seconds = (miliSendods / 1000) % 60;
            long minutes = (miliSendods / 1000) / 60;
            long hours = minutes / 60;

            if (hours > 0) {
                builder.append(hours);
                builder.append(':');
            }
            if (minutes > 0) {
                minutes = minutes % 60;
                if (minutes >= 10) {
                    builder.append(minutes);
                } else {
                    builder.append(0);
                    builder.append(minutes);
                }
            } else {
                builder.append('0');
                builder.append('0');
            }
            builder.append(':');
            if (seconds > 0) {
                if (seconds >= 10) {
                    builder.append(seconds);
                } else {
                    builder.append(0);
                    builder.append(seconds);
                }
            } else {
                builder.append('0');
                builder.append('0');
            }
            builder.append(':');
            if (milliSecond > 0) {
                if (milliSecond >= 10) {
                    builder.append(milliSecond);
                } else {
                    builder.append(0);
                    builder.append(milliSecond);
                }
            } else {
                builder.append('0');
                builder.append('0');
            }
            return builder.toString();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        if(propertyChangeEvent.getPropertyName() == TimeContainer.STATE_CHANGED) {
            TimeContainer t = TimeContainer.getInstance();
            if(t.getCurrentState() == TimeContainer.STATE_RUNNING) {
                btnStart.setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher_pause_foreground));
                startUpdateTimer();
            } else {
                btnStart.setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher_play_foreground));
                updateTimerText();
            }
            checkServiewRunning();
        }
    }

}