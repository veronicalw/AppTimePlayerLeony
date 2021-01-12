package com.example.myapplication.AlarmPlayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.myapplication.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AlarmPlayer extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener{
    private static final String TAG = AlarmPlayer.class.getSimpleName();
    private SqliteDatabase mDatabase;
    private ArrayList<MyNotes> allNotes=new ArrayList<>();
    private MyNotesAdapter mAdapter;
    TextClock timeNow;
    ImageButton btnAdd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_player);

        RecyclerView noteView = findViewById(R.id.recycleView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        noteView.setLayoutManager(linearLayoutManager);
        noteView.setHasFixedSize(true);
        mDatabase = new SqliteDatabase(this);
        allNotes = mDatabase.listMyNotes();

        timeNow = findViewById(R.id.timeNow);
        blink();

        if(allNotes.size() > 0){
            noteView.setVisibility(View.VISIBLE);
            mAdapter = new MyNotesAdapter(this, allNotes);
            noteView.setAdapter(mAdapter);

        } else {
            noteView.setVisibility(View.GONE);
            Toast.makeText(this, "You have note any less, please add one", Toast.LENGTH_LONG).show();
        }
        btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTaskDialog();
            }
        });
    }

    private void addTaskDialog(){
        LayoutInflater inflater = LayoutInflater.from(this);
        View subView = inflater.inflate(R.layout.add_note_layout, null);

        final EditText titles = subView.findViewById(R.id.edtTitle);
        final EditText details = subView.findViewById(R.id.edtDetail);
        final Button alarms = subView.findViewById(R.id.edtTime);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add new note");
        builder.setView(subView);
        builder.create();

        alarms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timepicker = new TimePickerFragment();
                timepicker.show(getSupportFragmentManager(), "time picker");
            }
        });

        builder.setPositiveButton("ADD MY NOTES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String title = titles.getText().toString();
                final String detail = details.getText().toString();

                if(TextUtils.isEmpty(title)){
                    Toast.makeText(AlarmPlayer.this, "Something went wrong. Check your input values", Toast.LENGTH_LONG).show();
                }
                else{
                    MyNotes newNotes = new MyNotes(title, detail);
                    mDatabase.addMyNotes(newNotes);

                    finish();
                    startActivity(getIntent());
                }
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(AlarmPlayer.this, "Task cancelled", Toast.LENGTH_LONG).show();
            }
        });
        builder.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mDatabase != null){
            mDatabase.close();
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        startAlarm(c);
    }

    private void startAlarm(Calendar c) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, MyBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
        if (c.before(Calendar.getInstance())) {
            c.add(Calendar.DATE, 1);
        }
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
    }

    private void blink() {
        final Handler hander = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(800);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                hander.post(new Runnable() {
                    @Override
                    public void run() {
                        if(timeNow.getVisibility() == View.VISIBLE) {
                            timeNow.setVisibility(View.INVISIBLE);
                        } else {
                            timeNow.setVisibility(View.VISIBLE);
                        }
                        blink();
                    }
                });
            }
        }).start();
    }
}