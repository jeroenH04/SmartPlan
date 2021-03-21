package com.example.agenda_app;

import android.content.Intent;
import android.os.Bundle;

import com.example.agenda_app.hardware.Accelerometer;
import com.example.agenda_app.hardware.Gyroscope;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.view.View;

public class MainActivity extends AppCompatActivity {

    private Accelerometer accelerometer;
    private Gyroscope gyroscope;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        accelerometer = new Accelerometer(this);
        gyroscope = new Gyroscope(this);
        accelerometer.setListener(new Accelerometer.Listener() {
            @Override
            public void onTranslation(final float tx, final float ty,
                                      final float tz) {
                if (tx > 1.0f) {
                    // to be implemented
                } else if (tx < -1.0f) {
                    // to be implemented
                }
            }
        });

        gyroscope.setListener(new Gyroscope.Listener() {
            @Override
            public void onRotation(final float rx, final float ry,
                                   final float rz) {
                if (rx > 1.0f) {
                    // to be implemented
                } else if (rx < -1.0f) {
                    // to be implemented
                }
            }
        });

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(R.id.navigation_profile,
                        R.id.navigation_dashboard, R.id.navigation_planning,
                        R.id.navigation_settings).build();
        NavController navController = Navigation.findNavController(this,
                R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController,
                appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    public void logout(final View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), Authentication.class));
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        accelerometer.register();
        gyroscope.register();
    }

    @Override
    public void onPause() {
        super.onPause();
        accelerometer.unregister();
        gyroscope.unregister();
    }
}
