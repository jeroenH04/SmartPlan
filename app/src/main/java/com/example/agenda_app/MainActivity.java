package com.example.agenda_app;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.example.agenda_app.hardware.Accelerometer;
import com.example.agenda_app.hardware.Gyroscope;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.view.View;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user;
    DocumentReference docRef;
    boolean b;
    String uid;
    String o;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);

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

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("My not","My not", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);

        }

        Thread t = new Thread(){
            public void run() {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                user = FirebaseAuth.getInstance().getCurrentUser();
                uid = user.getUid();
                final DocumentReference docRef = db.collection("users").document(uid);
                while(true){
                    docRef.get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    b = documentSnapshot.getBoolean("moved");
                                    o = documentSnapshot.getString("studyMode");
                                    if(b && o.equals("On")){
                                        sendNotify();
                                       

                                    }

                                    System.out.println(b);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    System.out.println("oops");
                                }
                            });
                    if(b){
                        try{Thread.sleep(5000);}
                        catch(Exception e){}
                        docRef.update("moved",false);
                    } else{
                        try{Thread.sleep(2000);}
                        catch(Exception e){}
                    }
                }
                }

            }

            public void sendNotify() {
                NotificationCompat.Builder builder =
                        new NotificationCompat.Builder(MainActivity.this,
                                "My not");
                builder.setContentTitle("Stop using your phone");
                builder.setContentText("Please go back to studying!");
                builder.setSmallIcon(R.drawable.ic_baseline_error_outline_24);
                builder.setAutoCancel(true);
                builder.setPriority(NotificationCompat.PRIORITY_MAX);


                NotificationManagerCompat managerCompat =
                        NotificationManagerCompat.from(MainActivity.this);
                managerCompat.notify(1, builder.build());

            }



        };

        t.start();


    }

    public void logout(final View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),
                Authentication.class));
        finish();
    }
}
