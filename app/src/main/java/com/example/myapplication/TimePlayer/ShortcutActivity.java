package com.example.myapplication.TimePlayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.myapplication.R;

import androidx.appcompat.app.AppCompatActivity;

public class ShortcutActivity extends AppCompatActivity {
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Intent.ShortcutIconResource shortcutIconResource = Intent.ShortcutIconResource.fromContext(this, R.drawable.timer);
        Intent intent = new Intent();
        Intent runIntent = new Intent(this, ServiceActivity.class);
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, runIntent);
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.timelable_shortcut));
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, shortcutIconResource);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        setResult(RESULT_OK, intent);
        finish();
    }
}
