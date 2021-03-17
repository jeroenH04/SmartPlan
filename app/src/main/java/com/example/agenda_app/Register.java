package com.example.agenda_app;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button btn = (Button)findViewById(R.id.regBtn);
        
        TextView signIn = (TextView)findViewById(R.id.signIn);
        final FirebaseAuth fAuth;
        final ProgressBar pBar;
        final EditText mEmail,mPassword,mUsername;

        mEmail = findViewById(R.id.editEmail);
        mPassword = findViewById(R.id.editPassword);
        mUsername = findViewById(R.id.editName);
        pBar = findViewById(R.id.pBar);
        fAuth = FirebaseAuth.getInstance();
        final String TAG = "Register";



        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                final String username = mUsername.getText().toString().trim();


                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email is required");
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    mPassword.setError("Password is required");
                    return;
                }
                if (TextUtils.isEmpty(username)){
                    mUsername.setError("Username is required");
                }

                if(password.length() < 6){
                    mPassword.setError("Password must be longer than 6 characters");
                }

                pBar.setVisibility(View.VISIBLE);

                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            createUser(username);

                            // verify email
                            FirebaseUser uSer = fAuth.getCurrentUser();
                            uSer.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(Register.this,"Verification mail sent",Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG,"onFailure: Email not sent " + e.getMessage());
                                }
                            });
                            //

                            Toast.makeText(Register.this,"User created",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            finish();

                        }else {
                            Toast.makeText(Register.this,"Error! " + task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent (Register.this,Login.class));

            }
        });

       


    }

    public void createUser(String name){
        final FirebaseAuth fAuth = FirebaseAuth.getInstance();
        final FirebaseUser uSer = fAuth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //Firestore entry
        final String TAG = "Register";

        final TaskScheduler scheduler = new TaskScheduler(new ArrayList<com.example.agenda_app.Task>(), new ArrayList<Availability>(),
                new ArrayList<Item>(), name,
                "Off", 120, 240, 480, 0, Calendar.getInstance().getTime().toString());

        // Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put("Username", name);
        user.put("Schedule", scheduler);
        user.put("Studymode", "Off");
        user.put("Tasks", "No");


        // Add a new document with a generated ID
        db.collection("users").document(uSer.getUid()).set(scheduler)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + uSer.getUid());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });


//        db.collection("users")
//                .add(user)
//                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                    @Override
//                    public void onSuccess(DocumentReference documentReference) {
//                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w(TAG, "Error adding document", e);
//                    }
//                });

    }

}
