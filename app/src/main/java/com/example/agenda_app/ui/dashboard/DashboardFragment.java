package com.example.agenda_app.ui.dashboard;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.agenda_app.R;
import com.example.agenda_app.Task;
import com.example.agenda_app.TaskScheduler;

public class DashboardFragment extends Fragment {

    private AlertDialog dialog;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel = ViewModelProviders.of(this).get(DashboardViewModel.class);
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

        Button newTaskSave = (Button) taskPopUpView.findViewById(R.id.saveButton);
        Button newTaskCancel = (Button) taskPopUpView.findViewById(R.id.cancelButton);

        dialogBuilder.setView(taskPopUpView);
        dialog = dialogBuilder.create();
        dialog.show();

        newTaskSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // @TODO: This should be placed somewhere else..
                TaskScheduler scheduler = new TaskScheduler();

                // Get today's date
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                String currentDate = sdf.format(new Date());

                // Get the tasks name
                EditText nameField = (EditText) taskPopUpView.findViewById(R.id.newtask_name);
                final String name = nameField.getText().toString();

                // Get the tasks duration
                EditText durationField = (EditText) taskPopUpView.findViewById(R.id.newtask_duration);
                final String duration = durationField.getText().toString();

                // Get the tasks deadline
                EditText deadlineField = (EditText) taskPopUpView.findViewById(R.id.newtask_deadline);
                final String deadline = deadlineField.getText().toString();

                // Get the tasks intensity
                RadioGroup intensityGroup = (RadioGroup) taskPopUpView.findViewById(R.id.intensityGroup);
                int selectedIdIntensity = intensityGroup.getCheckedRadioButtonId();
                RadioButton radioButtonIntens = (RadioButton) taskPopUpView.findViewById(selectedIdIntensity);
                final String intensity = (String) radioButtonIntens.getText();

                // Get the tasks intensity
                RadioGroup difficultyGroup = (RadioGroup) taskPopUpView.findViewById(R.id.difficultyGroup);
                int selectedIdDifficulty = difficultyGroup.getCheckedRadioButtonId();
                RadioButton radioButtonDiff = (RadioButton) taskPopUpView.findViewById(selectedIdDifficulty);
                final String difficulty = (String) radioButtonDiff.getText();

                scheduler.addTask(name, duration, intensity.toLowerCase(), difficulty.toLowerCase(), deadline, currentDate);
                System.out.println(scheduler.getTaskList());

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

        // Get RadioButtons from intensity and change colors when selected
        final RadioGroup intensityGroup = (RadioGroup) taskPopUpView.findViewById(R.id.intensityGroup);
        final RadioButton relaxed = (RadioButton) taskPopUpView.findViewById(R.id.relaxedBtn);
        final RadioButton normal = (RadioButton) taskPopUpView.findViewById(R.id.normalBtn);
        final RadioButton intense = (RadioButton) taskPopUpView.findViewById(R.id.intenseBtn);

        intensityGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (relaxed.isChecked()) {
                    relaxed.setBackgroundResource(R.color.colorAccent);
                    normal.setBackgroundColor(Color.WHITE);
                    intense.setBackgroundColor(Color.WHITE);
                }
                if (normal.isChecked()) {
                    normal.setBackgroundResource(R.color.colorAccent);
                    relaxed.setBackgroundColor(Color.WHITE);
                    intense.setBackgroundColor(Color.WHITE);
                }
                if (intense.isChecked()) {
                    intense.setBackgroundResource(R.color.colorAccent);
                    normal.setBackgroundColor(Color.WHITE);
                    relaxed.setBackgroundColor(Color.WHITE);
                }

            }
        });

        // Get RadioButtons from difficulty and change colors when selected
        final RadioGroup difficultyGroup = (RadioGroup) taskPopUpView.findViewById(R.id.difficultyGroup);
        final RadioButton easy = (RadioButton) taskPopUpView.findViewById(R.id.easyBtn);
        final RadioButton medium = (RadioButton) taskPopUpView.findViewById(R.id.mediumBtn);
        final RadioButton hard = (RadioButton) taskPopUpView.findViewById(R.id.hardBtn);

        difficultyGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (easy.isChecked()) {
                    easy.setBackgroundResource(R.color.colorAccent);
                    medium.setBackgroundColor(Color.WHITE);
                    hard.setBackgroundColor(Color.WHITE);
                }
                if (medium.isChecked()) {
                    medium.setBackgroundResource(R.color.colorAccent);
                    easy.setBackgroundColor(Color.WHITE);
                    hard.setBackgroundColor(Color.WHITE);
                }
                if (hard.isChecked()) {
                    hard.setBackgroundResource(R.color.colorAccent);
                    medium.setBackgroundColor(Color.WHITE);
                    easy.setBackgroundColor(Color.WHITE);
                }

            }
        });
    }
}