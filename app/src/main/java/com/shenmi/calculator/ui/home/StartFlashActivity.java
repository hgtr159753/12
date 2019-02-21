package com.shenmi.calculator.ui.home;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.shenmi.calculator.R;

public class StartFlashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_flash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                intoHomePage();
            }
        }, 1500);
    }

    private void intoHomePage() {
        Intent intent = new Intent(this,MainCalculateActivity.class);
        startActivity(intent);
        finish();
    }
}
