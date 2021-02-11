package com.example.agenda_app.ui.dashboard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.agenda_app.R;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    private AlertDialog dialog;
    private Button myButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        Button popBtn = (Button) root.findViewById(R.id.popUpButton);
        popBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewTaskDialog();
            }
        });
        final TextView textView = root.findViewById(R.id.text_dashboard);
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        return root;
    }

    public void createNewTaskDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this.getActivity());
        final View taskPopUpView = getLayoutInflater().inflate(R.layout.popup, null);
        EditText newcontactpopup_firstName = (EditText) taskPopUpView.findViewById(R.id.newcontactpopup_firstname);

        Button newTaskSave = (Button) taskPopUpView.findViewById(R.id.saveButton);
        Button newTaskCancel = (Button) taskPopUpView.findViewById(R.id.cancelButton);

        dialogBuilder.setView(taskPopUpView);
        dialog = dialogBuilder.create();
        dialog.show();

        newTaskSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //define save button
                dialog.dismiss();
            }
        });

        newTaskCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //define cancel button
                dialog.dismiss();
            }
        });
    }
}