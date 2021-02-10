package com.example.agenda_app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private EditText newcontactpopup_firstName;
    private Button newcontactpopup_cancel, newcontactpopup_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);

        Button popBtn = findViewById(R.id.popUpButton);
        popBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewContactDialog();
            }
        });

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);


    }

    public void createNewContactDialog() {
        dialogBuilder = new AlertDialog.Builder(this);
        final View contactPopupView = getLayoutInflater().inflate(R.layout.popup, null);
        newcontactpopup_firstName = (EditText) contactPopupView.findViewById(R.id.newcontactpopup_firstname);

        newcontactpopup_save = (Button) contactPopupView.findViewById(R.id.saveButton);
        newcontactpopup_cancel = (Button) contactPopupView.findViewById(R.id.cancelButton);

        dialogBuilder.setView(contactPopupView);
        dialog = dialogBuilder.create();
        dialog.show();

        newcontactpopup_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //define save button
                dialog.dismiss();
            }
        });

        newcontactpopup_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //define cancel button
                dialog.dismiss();
            }
        });
    }
}