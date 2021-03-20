package com.example.agenda_app.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.agenda_app.Authentication;
import com.example.agenda_app.MainActivity;
import com.example.agenda_app.R;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends Activity {
    Handler handler;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        final FirebaseAuth fAuth;
        fAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashfile);
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(fAuth.getCurrentUser() == null) {
                    Intent intent = new Intent(
                            SplashActivity.this,
                            Authentication.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(
                            SplashActivity.this,
                            MainActivity.class);
                    startActivity(intent);
                }
                finish();
            }
        }, 3000);
    }
}
