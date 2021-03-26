package com.example.agenda_app.ui;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import com.example.agenda_app.MainActivity;
import com.example.agenda_app.algorithms.Availability;
import com.example.agenda_app.R;
import com.example.agenda_app.algorithms.TaskScheduler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SettingsFragment extends Fragment {
    private AlertDialog dialog;
    private AlertDialog dialogShow;
    private int buttonCount;
    private final ArrayList<Button> buttonArrayList = new ArrayList<>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseUser user = FirebaseAuth.getInstance()
            .getCurrentUser();
    private Handler myHandler;
    private TaskScheduler scheduler;

    public View onCreateView(@NonNull final LayoutInflater inflater,
                             final ViewGroup container,
                             final Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_settings, container,
                false);

        final EditText editRelaxNumber =
                root.findViewById(R.id.editRelaxNumber);
        final EditText editNormalNumber =
                root.findViewById(R.id.editNormalNumber);
        final EditText editIntenseNumber =
                root.findViewById(R.id.editIntenseNumber);

        // Get the scheduler object from the database
        DocumentReference docRef = db.collection("users")
                .document(user.getUid());
        docRef.get().addOnSuccessListener(
                new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(final DocumentSnapshot documentSnapshot) {
                        scheduler =
                                documentSnapshot.toObject(TaskScheduler.class);
                        editRelaxNumber.setText(String.valueOf(
                                scheduler.getRelaxedIntensity() / 60));
                        editNormalNumber.setText(String.valueOf(
                                scheduler.getNormalIntensity() / 60));
                        editIntenseNumber.setText(String.valueOf(
                                scheduler.getIntenseIntensity() / 60));
                    }
                });

        Button resetPlanningButton = root.findViewById(R.id.resetPlanBtn);
        resetPlanningButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(final View v) {
                alertDeleteSchedule();
                updateDatabase();
            }
        });

        Button setAvailabilityButton = root.findViewById(R.id.setAvailability);
        setAvailabilityButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(final View v) {
                createNewAvailabilityDialog();
            }
        });

        Button btn_save = root.findViewById(R.id.btn_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(final View v) {
                try {
                    final int relaxNumber = Integer.parseInt(
                            editRelaxNumber.getText().toString());
                    final int normalNumber = Integer.parseInt(
                            editNormalNumber.getText().toString());
                    final int intenseNumber = Integer.parseInt(
                            editIntenseNumber.getText().toString());
                    scheduler.setIntensity(relaxNumber, normalNumber,
                            intenseNumber);
                    updateDatabase();
                    Toast.makeText(getActivity(),
                            "Intensity preferences saved.",
                            Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    if (e.getMessage().equals("intensity <= 0")) {
                        alertView("Intensity Preferences needs to be at least "
                                + "1h or higher");
                    } else {
                        alertView("Please fill in all details.");
                    }
                }
            }
        });

        // Delete the account and the corresponding data
        Button deleteAccountBtn = root.findViewById(R.id.delAcntBtn);
        deleteAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                alertDeleteAccount();
            }
        });

        myHandler = new Handler();
        return root;
    }

    private void reloadFragment(final boolean drawAvailability,
                                final View availabilityPopUpView) {
        DocumentReference docRef = db.collection("users")
                .document(user.getUid());
        docRef.get().addOnSuccessListener(
                new OnSuccessListener<DocumentSnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onSuccess(final DocumentSnapshot documentSnapshot) {
                        scheduler =
                                documentSnapshot.toObject(TaskScheduler.class);
                        if (drawAvailability) {
                            drawAvailability(availabilityPopUpView);
                        }
                        final EditText editRelaxNumber =
                                getView().findViewById(R.id.editRelaxNumber);
                        final EditText editNormalNumber =
                                getView().findViewById(R.id.editNormalNumber);
                        final EditText editIntenseNumber =
                                getView().findViewById(R.id.editIntenseNumber);

                        editRelaxNumber.setText(String.valueOf(
                                scheduler.getRelaxedIntensity() / 60));
                        editNormalNumber.setText(String.valueOf(
                                scheduler.getNormalIntensity() / 60));
                        editIntenseNumber.setText(String.valueOf(
                                scheduler.getIntenseIntensity() / 60));
                    }
                });
    }

    /**
     * Create info pop-up for the availability.
     *
     * @param date,                  date of the availability
     * @param time,                  time of the availability
     * @param button,                button of the availability
     * @param availabilityPopUpView, the pop-up the availabiltiy is shown on
     */
    private void infoView(final String date, final String time,
                          final Button button, final View
                                  availabilityPopUpView) {
        AlertDialog.Builder infoDialog =
                new AlertDialog.Builder(this.getActivity());
        infoDialog.setTitle("Do you want to delete this availability?")
                .setIcon(R.drawable.ic_baseline_info_24)
                .setMessage("Date: " + date + '\n' + "Time: " + time)
                .setPositiveButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialoginterface,
                                                final int i) {
                            }
                        });
        infoDialog.setNegativeButton("Delete",
                new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    public void onClick(final DialogInterface dialoginterface,
                                        final int i) {
                        scheduler.removeAvailability(date, time);
                        updateDatabase(true, availabilityPopUpView);
                        // redraw the availability buttons
                        drawAvailability(availabilityPopUpView);
                        buttonArrayList.remove(button);
                    }
                });
        infoDialog.show();
    }

    /**
     * Method to create alert pop-up for add availability dialog
     *
     * @param message, message to be displayed in pop-up
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
     * Create pop-up dialog for deletion of availability.
     *
     * @param availabilityPopUpView, view to show the dialog on
     */
    private void alertDeleteView(final View availabilityPopUpView) {
        AlertDialog.Builder alertDialog =
                new AlertDialog.Builder(this.getActivity());
        alertDialog.setTitle("Confirm Deletion")
                .setIcon(R.drawable.ic_baseline_error_outline_24)
                .setMessage("Are you sure you want to permanently delete all "
                        + "your availability?")
                .setPositiveButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialoginterface,
                                                final int i) {
                            }
                        });
        alertDialog.setNegativeButton("Ok",
                new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    public void onClick(final DialogInterface dialoginterface,
                                        final int i) {
                        if (scheduler.getSchedule().size() != 0) {
                            alertDeleteSchedule();
                        }
                        scheduler.clearAvailability(); // clear the tasklist
                        updateDatabase(true, availabilityPopUpView);
                        // clear all the buttons from the screen
                        drawAvailability(availabilityPopUpView);
                        buttonArrayList.clear(); // clear the button ArrayList
                    }
                });
        alertDialog.show();
    }

    /**
     * Create pop-up dialog for deletion of the schedule.
     */
    private void alertDeleteSchedule() {
        AlertDialog.Builder alertDialog =
                new AlertDialog.Builder(this.getActivity());
        alertDialog.setTitle("Alert")
                .setIcon(R.drawable.ic_baseline_error_outline_24)
                .setMessage("Do you want to clear your created schedule?")
                .setPositiveButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialoginterface,
                                                final int i) {
                            }
                        });
        alertDialog.setNegativeButton("Ok",
                new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    public void onClick(final DialogInterface dialoginterface,
                                        final int i) {
                        alertRestoreTasks();
                    }
                });
        alertDialog.show();
    }

    /**
     * Create pop-up dialog for account deletion.
     */
    private void alertDeleteAccount() {
        AlertDialog.Builder alertDialog =
                new AlertDialog.Builder(this.getActivity());
        alertDialog.setTitle("Confirm Deletion")
                .setIcon(R.drawable.ic_baseline_error_outline_24)
                .setMessage("Are you sure you want to permanently delete your"
                        + " account?")
                .setPositiveButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface
                                                        dialoginterface,
                                                final int i) {
                            }
                        });
        alertDialog.setNegativeButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialoginterface,
                                        final int i) {
                        // Delete the user's authentication
                        user.delete()
                                .addOnCompleteListener(
                                        new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(
                                                    @NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    startActivity(new Intent(getActivity()
                                                            .getApplicationContext(),
                                                            Login.class));
                                                    Toast.makeText(getActivity(),
                                                            "Your account has" +
                                                                    " been "
                                                                    +
                                                                    "deleted.",
                                                            Toast.LENGTH_SHORT).show();
                                                    // Delete the users data
                                                    db.collection("users")
                                                            .document(user.getUid())
                                                            .delete();
                                                }
                                            }
                                        });
                    }
                });
        alertDialog.show();
    }

    /**
     * Create pop-up dialog to restore tasks into the task list.
     */
    private void alertRestoreTasks() {
        AlertDialog.Builder alertDialog =
                new AlertDialog.Builder(this.getActivity());
        alertDialog.setTitle("Alert")
                .setIcon(R.drawable.ic_baseline_error_outline_24)
                .setMessage("Do you want to restore your planned tasks to your "
                        + "task list?")
                .setPositiveButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialoginterface,
                                                final int i) {
                                scheduler.resetSchedule(); // reset the schedule
                                scheduler.clearTasklist();
                                updateDatabase();
                            }
                        });
        alertDialog.setNegativeButton("Yes",
                new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    public void onClick(final DialogInterface dialoginterface,
                                        final int i) {
                        scheduler.resetSchedule(); // reset the schedule
                        updateDatabase();
                    }
                });
        alertDialog.show();
    }

    /**
     * Create new dialog to show/update the availability.
     */
    @RequiresApi(api = Build.VERSION_CODES.N) // needed to use .sort()
    public void createNewAvailabilityDialog() {
        // Setup pop-up window
        AlertDialog.Builder dialogBuilder =
                new AlertDialog.Builder(this.getActivity());
        final View availabilityPopUpView = getLayoutInflater()
                .inflate(R.layout.popup_availability, null);
        Button addAvailabilityButton = availabilityPopUpView
                .findViewById(R.id.addAvailabilityButton);
        Button cancelAvailabilityButton = availabilityPopUpView
                .findViewById(R.id.cancelAvailabilityButton);
        Button clearAvailabilityButton = availabilityPopUpView
                .findViewById(R.id.clearAvailabilityButton);
        dialogBuilder.setView(availabilityPopUpView);
        dialogShow = dialogBuilder.create();
        dialogShow.show();

        // Draw the availability
        drawAvailability(availabilityPopUpView);

        // Set the add-button to direct to availabilityPopUpView
        addAvailabilityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                addNewAvailabilityDialog(availabilityPopUpView);
            }
        });

        // Set the cancel-button to close the dialog
        cancelAvailabilityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                dialogShow.dismiss();
            }
        });

        // Set the clear-button to clear the availability
        clearAvailabilityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                alertDeleteView(availabilityPopUpView);
            }
        });
    }

    /**
     * Create new dialog to add a new availability.
     *
     * @param availabilityPopUpView, view to draw the availability on
     */
    public void addNewAvailabilityDialog(final View availabilityPopUpView) {
        // Setup pop-up window
        AlertDialog.Builder dialogBuilder =
                new AlertDialog.Builder(this.getActivity());
        final View addAvailabilityPopUpView = getLayoutInflater()
                .inflate(R.layout.popup_add_availability, null);
        Button saveButtonAvailability = addAvailabilityPopUpView
                .findViewById(R.id.saveButtonAvailability);
        final Button cancelButtonAvailability = addAvailabilityPopUpView.findViewById(
                R.id.cancelButtonAvailability);
        dialogBuilder.setView(addAvailabilityPopUpView);
        dialog = dialogBuilder.create();

        // Get the input fields
        final EditText timeField1 = addAvailabilityPopUpView
                .findViewById(R.id.inputTimeAvailable1);
        final EditText timeField2 = addAvailabilityPopUpView
                .findViewById(R.id.inputTimeAvailable2);
        final EditText dateField = addAvailabilityPopUpView
                .findViewById(R.id.inputDateAvailable);

        // Create date-picker for deadline
        dateField.setInputType(InputType.TYPE_NULL);
        dateField.setOnClickListener(new View.OnClickListener() {
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
                                dateField.setText(f.format(dayOfMonth) + "-"
                                        + f.format(monthOfYear + 1)
                                        + "-" + year);
                            }
                        }, year, month, day);
                picker.show();
            }
        });
        dialog.show();

        // Set the save button for the add availability dialog
        saveButtonAvailability.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(final View v) {
                try {
                    // Extract the data input
                    final String date = dateField.getText().toString();
                    final String time1 = timeField1.getText().toString();
                    final String time2 = timeField2.getText().toString();

                    // check if the time input is correct
                    scheduler.getDurationMinutes(time1);
                    scheduler.getDurationMinutes(time2);

                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat dateFormat = new
                            SimpleDateFormat("dd/MM/yyyy");
                    String dateToday = dateFormat.format(calendar.getTime());
                    if (date.compareTo(dateToday) < 0) {
                        Toast.makeText(getActivity(),
                                "Error! You cannot set an availability for " +
                                        "today"
                                        + " or the past.",
                                Toast.LENGTH_LONG).show();
                    } else {
                        // Add availability to the list
                        scheduler.addAvailability(date, time1 + "-" + time2);
                        updateDatabase(true, availabilityPopUpView);
                        drawAvailability(availabilityPopUpView);
                    }
                    dialog.dismiss(); // Close pop-up window
                } catch (Exception e) {
                    if (e.getMessage().equals("negative time available")) {
                        alertView("The end time should be later than the "
                                + "begin time.");
                    } else if (e.getMessage().equals("minutes >= 60")
                            || e.getMessage().equals("invalid time")
                            || e.getMessage().equals("Date input is "
                            + "incorrect")) {
                        alertView("Your time input is incorrect.");
                    } else {
                        alertView("Please fill in all details.");
                    }
                }
            }
        });

        cancelButtonAvailability.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                // Close pop-up window
                dialog.dismiss();
            }
        });
    }

    /**
     * Draw availability on dialog.
     *
     * @param availabilityPopUpView, view to draw the availability onto
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void drawAvailability(final View availabilityPopUpView) {
        // Find the dashboard layout
        final ConstraintLayout layout = availabilityPopUpView.findViewById(R.id.availabilityBox);
        final ConstraintSet set = new ConstraintSet();
        set.clone(layout);

        // First remove all drawn buttons, if any
        for (Button b : buttonArrayList) {
            layout.removeView(b);
        }

        // For each availability in the ArrayList, draw a button
        for (Availability avail : scheduler.getAvailabilityList()) {
            final Button button = new Button(getActivity());
            final String btnName = avail.getDate() + ", " + avail.getDuration();
            final String date = avail.getDate();
            final String time = avail.getDuration();
            boolean dateDiff = false;
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy",
                    Locale.getDefault());
            String currentDate = sdf.format(new Date());
            try {
                // check if the availability falls before today's date
                dateDiff = sdf.parse(date).before(sdf.parse(currentDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (!dateDiff) { // check if the availability is not in the past
                button.setText(btnName);
                @SuppressLint("UseCompatLoadingForDrawables") Drawable img
                        = button.getContext()
                        .getDrawable(R.drawable.ic_baseline_info_24);
                button.setCompoundDrawablesWithIntrinsicBounds(null,
                        null, img, null);
                button.setId(btnName.hashCode()); // get unique ID from name
                button.setBackgroundResource(R.drawable.customborder);
                buttonArrayList.add(button); // add the button to the ArrayList
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        infoView(date, time, button, availabilityPopUpView);
                    }
                });
                layout.addView(button);

                // place the button at the correct spot according to the button
                // counter
                set.connect(button.getId(), ConstraintSet.TOP,
                        ConstraintSet.PARENT_ID, ConstraintSet.TOP,
                        buttonCount * 130);
                set.connect(button.getId(), ConstraintSet.RIGHT,
                        ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0);
                set.connect(button.getId(), ConstraintSet.LEFT,
                        ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0);
                set.constrainHeight(button.getId(), 100);
                set.applyTo(layout);
                ++buttonCount; // increase the button counter
            }
        }
        buttonCount = 0; // reset button counter
    }

    public void updateDatabase() {
        updateDatabase(false, null);
    }

    //call updateDatabase(true, availabilityPopUpView) if after updating
    // database drawAvailability is called
    //else call updateDatabase or call updateDatabase(false, null)
    //necessary because drawAvailability will finish before updateDatabase
    // and thus draw incorrect information
    public void updateDatabase(final boolean drawAvailability,
                               final View availabilityPopUpView) {
        //get database reference to load and write to database
        DocumentReference docRef = db.collection("users")
                .document(user.getUid());
        docRef.get().addOnSuccessListener(
                new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(final DocumentSnapshot documentSnapshot) {
                        //set the old schedule to oldScheduler
                        TaskScheduler oldScheduler =
                                documentSnapshot.toObject(TaskScheduler.class);
                        // if the date of last update from the oldScheduler
                        // is the same
                        // as the date of last update on the current
                        // scheduler you have
                        // the most updated database
                        if (scheduler.getDateOfLastUpdate().equals(oldScheduler
                                .getDateOfLastUpdate())) {
                            //if no device is changing the database or if the
                            // change
                            // is the same
                            if (oldScheduler.getSchedulerHashcode() == 0 || oldScheduler
                                    .getSchedulerHashcode() == scheduler.hashCode()) {
                                // set hashCode of the current change to the
                                // oldScheduler
                                oldScheduler.setSchedulerHashcode(scheduler.hashCode());
                                // upload oldScheduler with updated hashCode
                                // to the
                                // database
                                db.collection("users").document(
                                        user.getUid()).set(oldScheduler,
                                        SetOptions.merge());
                            } else { // oldScheduler hashcode != 0 or is
                                // different from
                                // scheduler.hashCode() thus an other device
                                // is changing
                                // database already
                                alertView("You are already trying to edit the" +
                                        " data on "
                                        + "another account. Please try again " +
                                        "later");

                            }
                        } else {
                            // oldScheduler.lastchangedate != scheduler
                            // .lastchangedate
                            // thus user needs to first load most recent
                            // version of
                            // scheduler
                            alertView("This device was still on an old " +
                                    "version of the "
                                    + "Planning, The planning has been " +
                                    "reloaded."
                                    + " Please try again.");
                            reloadFragment(drawAvailability,
                                    availabilityPopUpView);
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
                docRef.get().addOnSuccessListener(
                        new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(final DocumentSnapshot
                                                          documentSnapshot) {
                                //set the old schedule to oldScheduler
                                TaskScheduler oldScheduler =
                                        documentSnapshot.toObject(TaskScheduler.class);
                                // if the date of last update from the
                                // oldScheduler is
                                // the same as the date of last update on the
                                // current
                                // scheduler you have the most updated database
                                if (scheduler.getDateOfLastUpdate()
                                        .equals(oldScheduler.getDateOfLastUpdate())) {
                                    // if database was to busy and thus not
                                    // yet updated
                                    // oldScheduler.hashCode run
                                    // updateDatabase again
                                    if (oldScheduler.getSchedulerHashcode() == 0) {
                                        //alertView("reupdating database");
                                        updateDatabase();
                                        return;
                                    }
                                    // if this device updated the hashCode of
                                    // the
                                    // oldScheduler then they can update the
                                    // database
                                    if (oldScheduler.getSchedulerHashcode()
                                            == scheduler.hashCode()) {
                                        scheduler.setDateOfLastUpdate(Calendar
                                                .getInstance().getTime().toString());
                                        db.collection("users").document(
                                                user.getUid()).set(scheduler,
                                                SetOptions.merge());
                                    } else {
                                        // another device is trying to update database
                                        // since oldScheduler.getHashCode() !=
                                        // scheduler.hashCode()
                                        alertView("You are already trying to edit the "
                                                + "data on another account. Please try "
                                                + "again later.");
                                    }
                                } else {
                                    // oldScheduler.lastchangedate !=
                                    // scheduler.lastchangedate thus user needs
                                    // to first load most recent version of scheduler
                                    alertView("This device was still on an old version"
                                            + " of the Planning, The planning has been "
                                            + "reloaded. Please try again.");
                                    reloadFragment(drawAvailability,
                                            availabilityPopUpView);
                                }
                            }
                        });
            }
        }, 200); // myHandler is run after 200 ms
    }
}
