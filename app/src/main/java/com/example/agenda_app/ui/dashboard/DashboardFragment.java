package com.example.agenda_app.ui.dashboard;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.agenda_app.R;
import com.example.agenda_app.Task;
import com.example.agenda_app.TaskScheduler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class DashboardFragment extends Fragment {

    private AlertDialog dialog;
    private int prevBtnID;
    private int buttonCount;
    private ArrayList<Integer> buttonArrayList = new ArrayList<>();


    TaskScheduler scheduler = new TaskScheduler(); // @TODO: This should be placed somewhere else..

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

    // Method to create alert pop-up
    private void alertView( String message ) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this.getActivity());
        alertDialog.setTitle( "Error!" )
                .setIcon(R.drawable.ic_baseline_error_outline_24)
                .setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                    }
                }).show();
    }

    // Method to create info pop-up for task
    private void infoView(final String name, String duration, String deadline, String intensity,
                          String difficulty, final ConstraintLayout layout, final Button button, final ConstraintSet set ) {
        AlertDialog.Builder infoDialog = new AlertDialog.Builder(this.getActivity());
        infoDialog.setTitle( name.toUpperCase() )
                .setIcon(R.drawable.ic_baseline_info_24)
                .setMessage("The duration of this task is: " + duration + '\n' +
                            "The deadline of this task is: " + deadline + '\n' +
                            "Intensity is set to: " + intensity + '\n' +
                            "difficulty is set to: " + difficulty + '\n'
                )
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                    }
                });
        infoDialog.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        scheduler.removeTask(name); // remove the task from the task list
                        layout.removeView(button); // remove the button
                        int index = buttonArrayList.indexOf(button.getId());
                        buttonArrayList.remove(Integer.valueOf(button.getId()));
                        --buttonCount; // decrease the button counter
                        for (i = index; i < buttonArrayList.size(); ++i) {
                            set.connect(buttonArrayList.get(i), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 150 + i * 78);
                            set.connect(buttonArrayList.get(i), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0);
                            set.connect(buttonArrayList.get(i), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0);
                            set.constrainHeight(buttonArrayList.get(i), 100);
                            set.applyTo(layout);
                        }
                    }
                });
        infoDialog.show();
    }

    public void createNewTaskDialog() {
        // Setup pop-up window
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this.getActivity());
        final View taskPopUpView = getLayoutInflater().inflate(R.layout.popup, null);
        Button newTaskSave = (Button) taskPopUpView.findViewById(R.id.saveButton);
        Button newTaskCancel = (Button) taskPopUpView.findViewById(R.id.cancelButton);
        final EditText eText=(EditText) taskPopUpView.findViewById(R.id.newtask_deadline);

        dialogBuilder.setView(taskPopUpView);
        dialog = dialogBuilder.create();

        // Create date-picker for deadline
        eText.setInputType(InputType.TYPE_NULL);
        eText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                final NumberFormat f = new DecimalFormat("00");
                // date picker dialog
                DatePickerDialog picker = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                eText.setText(f.format(dayOfMonth) + "-" + f.format(monthOfYear + 1) + "-" + year);
                            }
                        }, year, month, day);
                picker.show();
            }
        });

        NumberPicker picker1;

        dialog.show();
        newTaskSave.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                // Get today's date
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                String currentDate = sdf.format(new Date());

                // Get the tasks name
                try {
                    EditText nameField = (EditText) taskPopUpView.findViewById(R.id.newtask_name);
                    final String name = nameField.getText().toString().toLowerCase();

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

                    // Add the task to the taskList
                    scheduler.addTask(name, duration, intensity.toLowerCase(), difficulty.toLowerCase(), deadline, currentDate);
                    System.out.println(scheduler.getTaskList());

                    // Find the dashboard layout
                    final ConstraintLayout layout = (ConstraintLayout) getView().findViewById(R.id.dashboard_layout);
                    final ConstraintSet set = new ConstraintSet();
                    set.clone(layout);

                    // Create new button for the task
                    final Button button = new Button(getActivity());
                    button.setText(name);
                    @SuppressLint("UseCompatLoadingForDrawables") Drawable img = button.getContext().getDrawable( R.drawable.ic_baseline_info_24 );
                    button.setCompoundDrawablesWithIntrinsicBounds(null, null, img,null); // set icon on the right of button
                    button.setId(name.hashCode()); // get unique ID from name
                    button.setBackgroundResource(R.color.colorPrimaryDark);
                    buttonArrayList.add(button.getId());
                    button.setOnClickListener( new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            infoView(name, duration, deadline, intensity, difficulty, layout, button, set);
                        }
                    });

                    layout.addView(button);
                    // place the button below the other buttons
                    set.connect(button.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 150 + buttonCount * 78);
                    set.connect(button.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0);
                    set.connect(button.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0);
                    set.constrainHeight(button.getId(), 100);
                    prevBtnID = button.getId();
                    set.applyTo(layout);
                    ++buttonCount; // increase the button counter

                    // Close pop-up window
                    dialog.dismiss();

                } catch (Exception e) {
                    if (e.getMessage().equals("name is not unique")) {
                        alertView("The task name should be unique.");
                    }
                    else if (e.getMessage().equals("minutes >= 60")) {
                        alertView("Your time input is incorrect.");
                    }
                    else if (e.getMessage().equals("deadline <= today")) {
                        alertView("The deadline cannot be in the past.");
                    } else {
                        alertView("All details should be filled in.");
                    }
                    System.out.println(e);
                }
            }
        });

        newTaskCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cancel pop-up
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