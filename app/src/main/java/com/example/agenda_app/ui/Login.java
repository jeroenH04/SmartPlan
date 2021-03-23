package com.example.agenda_app.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.agenda_app.MainActivity;
import com.example.agenda_app.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {
    private EditText mEmail;
    private EditText mPassword;
    private ProgressBar lgBar;
    private FirebaseAuth fAuth;
    private TextView forgotPass;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextView signUp = (TextView) findViewById(R.id.signUp);
        Button btn = (Button) findViewById(R.id.linBtn);
        fAuth = FirebaseAuth.getInstance();
        mEmail = findViewById(R.id.lgEmail);
        mPassword = findViewById(R.id.lgPassword);
        lgBar = findViewById(R.id.lgBar);
        forgotPass = findViewById(R.id.forgotTxt);

        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                startActivity(new Intent(Login.this,
                        ForgotPass.class));
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email is required");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    mPassword.setError("Password is required");
                    return;
                }
                if (password.length() < 6) {
                    mPassword.setError(
                            "Password must be longer than 6 characters");
                }

                lgBar.setVisibility(View.VISIBLE);
                fAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(
                                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull
                                           final Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(Login.this,
                                    "Logged in successfully",
                                    Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),
                                    MainActivity.class));
                            lgBar.setVisibility(View.INVISIBLE);
                            finish();
                        } else {
                            for (int i = 0; i < 2; i++) {
                                Toast.makeText(Login.this, "Error! "
                                                + task.getException().getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                            lgBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                startActivity(new Intent(Login.this,
                        Register.class));
                finish();
            }
        });
    }
}