package com.example.agenda_app.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.agenda_app.MainActivity;
import com.example.agenda_app.R;
import com.example.agenda_app.algorithms.Availability;
import com.example.agenda_app.algorithms.Item;
import com.example.agenda_app.algorithms.TaskScheduler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;

public class Register extends AppCompatActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Button btn = findViewById(R.id.regBtn);
        TextView signIn = findViewById(R.id.signIn);
        final FirebaseAuth fAuth;
        final ProgressBar pBar;
        final EditText mEmail;
        final EditText mPassword;
        final EditText mUsername;

        mEmail = findViewById(R.id.editEmail);
        mPassword = findViewById(R.id.editPassword);
        mUsername = findViewById(R.id.editName);
        pBar = findViewById(R.id.pBar);
        fAuth = FirebaseAuth.getInstance();
        final String TAG = "Register";

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                final String username = mUsername.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email is required");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    mPassword.setError("Password is required");
                    return;
                }
                if (TextUtils.isEmpty(username)) {
                    mUsername.setError("Username is required");
                }

                if (password.length() < 6) {
                    mPassword.setError(
                            "Password must be longer than 6 characters");
                }
                pBar.setVisibility(View.VISIBLE);
                fAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(
                                new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull final Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            createUser(username);
                                            // verify email
                                            FirebaseUser uSer =
                                                    fAuth.getCurrentUser();
                                            uSer.sendEmailVerification()
                                                    .addOnSuccessListener(
                                                            new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(final Void aVoid) {
                                                                    Toast.makeText(Register.this,
                                                                            "Verification mail sent",
                                                                            Toast.LENGTH_LONG).show();
                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.d(TAG, "onFailure: " +
                                                            "Email not sent "
                                                            + e.getMessage());
                                                }
                                            });

                                            Toast.makeText(Register.this,
                                                    "User created",
                                                    Toast.LENGTH_LONG).show();
                                            startActivity(new Intent(getApplicationContext(),
                                                    MainActivity.class));
                                            finish();
                                        } else {
                                            Toast.makeText(Register.this,
                                                    "Error! "
                                                            + task.getException().getMessage(),
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
            }
        });
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                startActivity(new Intent(Register.this,
                        Login.class));
            }
        });
    }

    /**
     * Create a new user.
     *
     * @param name, name of the user
     */
    public void createUser(final String name) {
        final FirebaseAuth fAuth = FirebaseAuth.getInstance();
        final FirebaseUser uSer = fAuth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Firestore entry
        final String TAG = "Register";
        final TaskScheduler scheduler = new TaskScheduler(
                new ArrayList<com.example.agenda_app.algorithms.Task>(),
                new ArrayList<Availability>(),
                new ArrayList<Item>(), name, "Off", 120,
                240, 480, 0,
                Calendar.getInstance().getTime().toString());

        // Add a new document with a generated ID
        db.collection("users").document(uSer.getUid())
                .set(scheduler)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(final Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot added with ID: "
                                + uSer.getUid());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull final Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }
}
