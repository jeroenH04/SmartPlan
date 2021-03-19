package com.example.agenda_app;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.agenda_app.ui.Login;
import com.example.agenda_app.ui.Register;

public class Authentication extends AppCompatActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        Button btn = (Button) findViewById(R.id.button);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                startActivity(new Intent(Authentication.this,
                        Login.class));
                finish();
            }
        });

        TextView txt = (TextView) findViewById(R.id.textView2);

        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                startActivity(new Intent(Authentication.this,
                        Register.class));
                finish();
            }
        });
    }
}
