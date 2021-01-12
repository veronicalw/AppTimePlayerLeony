package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.myapplication.AlarmPlayer.AlarmPlayer;
import com.example.myapplication.TimePlayer.TimePlayer;

public class OptionActivity extends AppCompatActivity {
    private CardView cv1, cv2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);
        cv1 = findViewById(R.id.cv1);
        cv2 = findViewById(R.id.cv2);

        cv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OptionActivity.this, TimePlayer.class);
                startActivity(intent);
                finish();
            }
        });

        cv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OptionActivity.this, AlarmPlayer.class);
                startActivity(intent);
                finish();
            }
        });
    }
}