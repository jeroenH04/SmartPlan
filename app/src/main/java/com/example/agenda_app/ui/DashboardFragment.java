package com.example.agenda_app.ui;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import com.example.agenda_app.R;
import com.example.agenda_app.algorithms.Task;
import com.example.agenda_app.algorithms.TaskScheduler;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

public class DashboardFragment extends Fragment {
    private AlertDialog dialog;
    private int buttonCount;
    private final ArrayList<Button> buttonArrayList = new ArrayList<>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseUser user = FirebaseAuth.getInstance()
            .getCurrentUser();
    private Handler myHandler;
    private TaskScheduler scheduler;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public View onCreateView(@NonNull final LayoutInflater inflater,
                             final ViewGroup container,
                             final Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_dashboard,
                container, false);
        Button popBtn = (Button) root.findViewById(R.id.popUpButton);
        Button deleteBtn = (Button) root.findViewById(R.id.deleteButton);
        popBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                createNewTaskDialog();
            }
        });
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                alertDeleteView();
            }
        });

        // Get the scheduler object from the database
        DocumentReference docRef = db.collection("users")
                .document(user.getUid());
        docRef.get().addOnSuccessListener(
                new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(final DocumentSnapshot documentSnapshot) {
                scheduler = documentSnapshot.toObject(TaskScheduler.class);
                drawTasks(root); // draw the tasks on the root view
            }
        });
        myHandler = new Handler();
        return root;
    }

    private void reloadFragment() {
        DocumentReference docRef = db.collection("users")
                .document(user.getUid());
        docRef.get().addOnSuccessListener(
                new OnSuccessListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSuccess(final DocumentSnapshot documentSnapshot) {
                scheduler = documentSnapshot.toObject(TaskScheduler.class);
                drawTasks(getView());
            }
        });
    }

    /**
     * Create info view when clicking onto a task.
     *
     * @param name, name of the task to display
     * @param duration, duration of the task to display
     * @param deadline, deadline of the task to display
     * @param intensity, intensity of the task to display
     * @param difficulty, difficulty of the task to display
     * @param button, button task is put onto
     */
    private void infoView(final String name, final String duration,
                          final String deadline, final String intensity,
                          final String difficulty, final Button button) {
        AlertDialog.Builder infoDialog = new AlertDialog.Builder(
                this.getActivity());
        infoDialog.setTitle(name.toUpperCase())
                .setIcon(R.drawable.ic_baseline_info_24)
                .setMessage("The duration of this task is: " + duration + '\n'
                        + "The deadline of this task is: " + deadline + '\n'
                        + "Intensity is set to: " + intensity + '\n'
                        + "difficulty is set to: " + difficulty + '\n'
                )
                .setPositiveButton("Ok", new
                        DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialoginterface,
                                        final int i) {
                    }
                });
        infoDialog.setNegativeButton("Delete",
                new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    public void onClick(final DialogInterface dialoginterface,
                                        final int i) {
                        scheduler.removeTask(name);
                        updateDatabase();
                        drawTasks(getView());
                        buttonArrayList.remove(button);
                    }
                });
        infoDialog.show();
    }

    /**
     * Create alert view to display error message.
     *
     * @param message, message to display
     */
    private void alertView(final String message) {
        AlertDialog.Builder alertDialog =
                new AlertDialog.Builder(this.getActivity());
        alertDialog.setTitle("Error!")
                .setIcon(R.drawable.ic_baseline_error_outline_24)
                .setMessage(message)
                .setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialoginterface,
                                        final int i) {
                    }
                }).show();
    }

    /**
     * Create alert view to confirm deletion of all tasks.
     */
    private void alertDeleteView() {
        AlertDialog.Builder alertDialog = new
                AlertDialog.Builder(this.getActivity());
        alertDialog.setTitle("Confirm Deletion")
                .setIcon(R.drawable.ic_baseline_error_outline_24)
                .setMessage("Are you sure you want to permanently delete all "
                        + "these tasks?")
                .setPositiveButton("Cancel", new
                        DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialoginterface,
                                        final int i) {
                    }
                });
        alertDialog.setNegativeButton("Ok",
                new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            public void onClick(final DialogInterface dialoginterface,
                                final int i) {
                scheduler.clearTasklist();
                updateDatabase();
                drawTasks(getView()); // clear all the buttons from the screen
                buttonArrayList.clear(); // clear the button ArrayList
            }
        });
        alertDialog.show();
    }

    /**
     * Create new dialog to add a new task.
     */
    public void createNewTaskDialog() {
        // Setup pop-up window
        AlertDialog.Builder dialogBuilder = new
                AlertDialog.Builder(this.getActivity());
        final View taskPopUpView = getLayoutInflater()
                .inflate(R.layout.popup_add_task, null);
        Button newTaskSave = (Button) taskPopUpView
                .findViewById(R.id.saveButton);
        Button newTaskCancel = (Button) taskPopUpView
                .findViewById(R.id.cancelButton);
        final EditText eText = (EditText) taskPopUpView
                .findViewById(R.id.newtask_deadline);

        dialogBuilder.setView(taskPopUpView);
        dialog = dialogBuilder.create();

        // Create date-picker for deadline
        eText.setInputType(InputType.TYPE_NULL);
        eText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                final NumberFormat f = new DecimalFormat("00");
                // date picker dialog
                DatePickerDialog picker = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(final DatePicker view,
                                                  final int year,
                                                  final int monthOfYear,
                                                  final int dayOfMonth) {
                                eText.setText(f.format(dayOfMonth) + "-"
                                        + f.format(monthOfYear + 1)
                                        + "-" + year);
                            }
                        }, year, month, day);
                picker.show();
            }
        });

        dialog.show();

        newTaskSave.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(final View v) {
                // Get today's date
                SimpleDateFormat sdf = new SimpleDateFormat(
                        "dd-MM-yyyy", Locale.getDefault());
                String currentDate = sdf.format(new Date());

                try {
                    // Get the tasks name
                    EditText nameField = (EditText) taskPopUpView
                            .findViewById(R.id.newtask_name);
                    final String name = nameField.getText().toString()
                            .toLowerCase();

                    // Get the tasks duration
                    EditText durationField = (EditText) taskPopUpView
                            .findViewById(R.id.newtask_duration);
                    final String duration = durationField.getText().toString();

                    // Get the tasks deadline
                    EditText deadlineField = (EditText) taskPopUpView
                            .findViewById(R.id.newtask_deadline);
                    final String deadline = deadlineField.getText().toString();

                    // Get the tasks intensity
                    RadioGroup intensityGroup = (RadioGroup) taskPopUpView
                            .findViewById(R.id.intensityGroup);
                    int selectedIdIntensity = intensityGroup
                            .getCheckedRadioButtonId();
                    RadioButton radioButtonIntens = (RadioButton) taskPopUpView
                            .findViewById(selectedIdIntensity);
                    final String intensity = (String) radioButtonIntens
                            .getText();

                    // Get the tasks intensity
                    RadioGroup difficultyGroup = (RadioGroup) taskPopUpView
                            .findViewById(R.id.difficultyGroup);
                    int selectedIdDifficulty = difficultyGroup
                            .getCheckedRadioButtonId();
                    RadioButton radioButtonDiff = (RadioButton) taskPopUpView
                            .findViewById(selectedIdDifficulty);
                    final String difficulty = (String) radioButtonDiff
                            .getText();

                    // Add the task to the taskList
                    scheduler.addTask(name, duration, intensity.toLowerCase(),
                            difficulty.toLowerCase(), deadline, currentDate);
                    updateDatabase();

                    drawTasks(getView()); // draw the tasks
                    dialog.dismiss(); // close pop-up window
                } catch (Exception e) {
                    if (e.getMessage().equals("name is not unique")) {
                        alertView("The task name should be unique.");
                    } else if (e.getMessage().equals("minutes >= 60")
                            || e.getMessage().equals("duration input is "
                            + "invalid")) {
                        alertView("Your time input is incorrect.");
                    } else if (e.getMessage().equals("deadline <= today")) {
                        alertView("The deadline cannot be in the past.");
                    } else {
                        alertView("All details should be filled in.");
                    }
                }
            }
        });

        newTaskCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                // Cancel pop-up
                dialog.dismiss();
            }
        });

        // Get RadioButtons from intensity and change colors when selected
        final RadioGroup intensityGroup = (RadioGroup) taskPopUpView
                .findViewById(R.id.intensityGroup);
        final RadioButton relaxed = (RadioButton) taskPopUpView
                .findViewById(R.id.relaxedBtn);
        final RadioButton normal = (RadioButton) taskPopUpView
                .findViewById(R.id.normalBtn);
        final RadioButton intense = (RadioButton) taskPopUpView
                .findViewById(R.id.intenseBtn);

        intensityGroup.setOnCheckedChangeListener(
                new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onCheckedChanged(final RadioGroup group,
                                         final int checkedId) {
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
        final RadioGroup difficultyGroup = (RadioGroup) taskPopUpView
                .findViewById(R.id.difficultyGroup);
        final RadioButton easy = (RadioButton) taskPopUpView
                .findViewById(R.id.easyBtn);
        final RadioButton medium = (RadioButton) taskPopUpView
                .findViewById(R.id.mediumBtn);
        final RadioButton hard = (RadioButton) taskPopUpView
                .findViewById(R.id.hardBtn);

        difficultyGroup.setOnCheckedChangeListener(
                new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onCheckedChanged(final RadioGroup group,
                                         final int checkedId) {
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

    /**
     * Draw tasks on the fragment.
     *
     * @param root, view to draw the tasks on
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void drawTasks(final View root) {
        // Find the dashboard layout
        final ConstraintLayout layout = (ConstraintLayout) root
                .findViewById(R.id.availabilityBoxDashboard);
        final ConstraintSet set = new ConstraintSet();
        set.clone(layout);

        // First remove all drawn buttons, if any
        for (Button b : buttonArrayList) {
            layout.removeView(b);
        }

        for (Task task : scheduler.getTaskList()) {
            // Create new button for the task
            final Button button = new Button(getActivity());
            final String name = task.getName();
            final String duration = task.getDuration();
            final String deadline = task.getDeadline();
            final String intensity = task.getIntensity();
            final String difficulty = task.getDifficulty();
            button.setText(name);
            @SuppressLint("UseCompatLoadingForDrawables") Drawable img = button
                    .getContext().getDrawable(R.drawable.ic_baseline_info_24);
            button.setCompoundDrawablesWithIntrinsicBounds(null, null,
                    img, null); // set icon on the right of button
            button.setId(name.hashCode()); // get unique ID from name
            button.setBackgroundResource(R.drawable.customborder);
            buttonArrayList.add(button);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    infoView(name, duration, deadline, intensity, difficulty,
                            button);
                }
            });
            layout.addView(button);

            // place the button at the correct spot according to the button
            // counter
            set.connect(button.getId(), ConstraintSet.TOP, ConstraintSet
                    .PARENT_ID, ConstraintSet.TOP, buttonCount * 130);
            set.connect(button.getId(), ConstraintSet.RIGHT, ConstraintSet
                    .PARENT_ID, ConstraintSet.RIGHT, 0);
            set.connect(button.getId(), ConstraintSet.LEFT, ConstraintSet
                    .PARENT_ID, ConstraintSet.LEFT, 0);
            set.constrainHeight(button.getId(), 100);
            set.applyTo(layout);
            ++buttonCount; // increase the button counter
        }
        buttonCount = 0; // reset button counter
    }

    /**
     * Update the Firestore database with the newly created tasks
     */
    public void updateDatabase() {
        //get database reference to load and write to database
        DocumentReference docRef = db.collection("users")
                .document(user.getUid());
        docRef.get().addOnSuccessListener
                (new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(final DocumentSnapshot documentSnapshot) {
                //set the old schedule to oldScheduler
                TaskScheduler oldScheduler = documentSnapshot
                        .toObject(TaskScheduler.class);
                // if the date of last update from the oldScheduler is the same
                // as the date of last update on the current scheduler
                // you have the most updated database
                if (scheduler.getDateOfLastUpdate().equals(oldScheduler
                        .getDateOfLastUpdate())) {
                    // if no device is changing the database or if the change is
                    // the same
                    if (oldScheduler.getSchedulerHashcode() == 0 || oldScheduler
                            .getSchedulerHashcode() == scheduler.hashCode()) {
                        // set hashCode of the current change to the oldScheduler
                        oldScheduler.setSchedulerHashcode(scheduler.hashCode());
                        // upload oldScheduler with updated hashCode to the
                        // database
                        db.collection("users").document(
                                user.getUid()).set(oldScheduler,
                                SetOptions.merge());
                    } else {
                        // oldScheduler hashcode != 0 or is different from
                        // scheduler.hashCode() thus an other device is changing
                        // database already
                        alertView("You are already trying to edit the data on "
                                + "another account. Please try again later");
                    }
                } else {
                    // oldScheduler.lastchangedate != scheduler.lastchangedate
                    // thus user needs to first load most recent version of
                    // scheduler
                    alertView("This device was still on an old version of the "
                            + "Planning, The planning has been reloaded. "
                            + "Please try again.");
                    reloadFragment();
                }
            }
        });

        // function to be delayed for 200 ms
        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //get database reference to load and write to database
                DocumentReference docRef = db.collection("users")
                        .document(user.getUid());
                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        //set the old schedule to oldScheduler
                        TaskScheduler oldScheduler = documentSnapshot
                                .toObject(TaskScheduler.class);
                        // if the date of last update from the oldScheduler is
                        // the same as the date of last update on the current
                        // scheduler you have the most updated database
                        if (scheduler.getDateOfLastUpdate().equals(oldScheduler
                                .getDateOfLastUpdate())) {
                            // if database was to busy and thus not yet updated
                            // oldScheduler.hashCode run updateDatabase again
                            if (oldScheduler.getSchedulerHashcode() == 0) {
                                //alertView("reupdating database");
                                updateDatabase();
                                return;
                            }
                            // if this device updated the hashCode of the
                            // oldScheduler then they can update the database
                            if (oldScheduler.getSchedulerHashcode() == scheduler
                                    .hashCode()) {
                                scheduler.setDateOfLastUpdate(Calendar
                                        .getInstance().getTime().toString());
                                db.collection("users")
                                        .document(user.getUid()).set(scheduler,
                                        SetOptions.merge());
                            } else { // another device is trying to update
                                // database since oldScheduler.getHashCode() !=
                                // scheduler.hashCode()
                                alertView("You are already trying to edit the "
                                        + "data on another account. Please try "
                                        + "again later.");
                            }
                        } else {
                            // oldScheduler.lastchangedate != scheduler
                            // .lastchangedate thus user needs to first load
                            // most recent version of scheduler
                            alertView("This device was still on an old version "
                                    + "of the Planning, The planning has been "
                                    + "reloaded. Please try again.");
                            reloadFragment();
                        }
                    }
                });
            }
        }, 200); // myHandler is run after 200 ms
    }
}
