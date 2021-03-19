package com.example.agenda_app.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.agenda_app.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPass extends AppCompatActivity {
    private EditText mEmail;
    private Button btn;
    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);

        btn = findViewById(R.id.forgotBtn);
        mEmail = findViewById(R.id.forgotEmail);
        fAuth = FirebaseAuth.getInstance();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                String Email = mEmail.getText().toString();
                fAuth.sendPasswordResetEmail(Email)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(final Void aVoid) {
                        Toast.makeText(ForgotPass.this,
                                "Reset mail sent",
                                Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull final Exception e) {
                        Toast.makeText(ForgotPass.this,
                                "Reset mail NOT sent" + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }
}