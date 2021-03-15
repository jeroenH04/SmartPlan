package com.example.agenda_app.ui.planning;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import com.example.agenda_app.Item;
import com.example.agenda_app.R;
import com.example.agenda_app.Task;
import com.example.agenda_app.TaskScheduler;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PlanningFragment extends Fragment {

    private AlertDialog dialog;
    private ArrayList<Item> schedule;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    TaskScheduler scheduler;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_planning, container, false);

        final LinearLayout agenda_dash = (LinearLayout) root.findViewById(R.id.agenda_dashboard);
        Button createPlanningBtn = (Button) root.findViewById(R.id.createPlanning);
        createPlanningBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scheduler.createSchedule();
                updateDatabase();
                agenda_dash.removeAllViews();
                showPlanning(agenda_dash);
            }
        });

        // Get the scheduler object from the database
        DocumentReference docRef = db.collection("users").document(user.getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                scheduler = documentSnapshot.toObject(TaskScheduler.class);
                showPlanning(agenda_dash);
            }
        });

        return root;
    }

    private void showPlanning(LinearLayout agenda_dash) {
        //get a set of all dates
        schedule = scheduler.getSchedule();
     //   Set<String> dates = schedule.keySet();
        //create a list keeping track of which dates are already displayed
        ArrayList<String> datesDone = new ArrayList<String>();

        //for all dates j where a task is planned
        for (final Item i : schedule) {
            //get a map containing all tasks with there time
      //      Map<Task, String> taskList = schedule.get(j);
            //get a set of all tasks
       //     Set<Task> tasks = taskList.keySet();
            //check if date is already displayed if it is skip
            if (datesDone.contains(i.getDate())) {
                break;
            }
            //add date to datesDone if date is not yet displayed
            datesDone.add(i.getDate());
            
            //create TextView for the date of the task
            final TextView date = new TextView(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0,50,0,0);
            date.setLayoutParams(params);

            date.setText(i.getDate());
            date.setTextSize(20);
            agenda_dash.addView(date);

            //for all tasks i planned on date j
            for (final Item j : schedule) {
                if (j.getDate().equals(i.getDate())) {
                    //get name from task object
                    final String taskName = j.getTask().getName();
                    //get date from the task map
                    String taskTime = j.getTime();
                    //get difficulty from task object
                    String taskDifficulty = i.getTask().getDifficulty();

                    // Constraint Layout
                    final ConstraintLayout cLayOut = new ConstraintLayout(getContext());
                    cLayOut.setBackgroundResource(R.drawable.customborder);
                    ConstraintLayout.LayoutParams conParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
                    conParams.setMargins(0, 20, 0, 0);
                    cLayOut.setLayoutParams(conParams);
                    agenda_dash.addView(cLayOut);

                    // Name Textview
                    final TextView task = new TextView(getContext());
                    task.setLayoutParams(new ViewGroup.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT));
                    task.setTextSize(22);
                    task.setText(taskName);
                    task.setId("taskName".hashCode());
                    cLayOut.addView(task);

                    // Time textView
                    final TextView time = new TextView(getContext());
                    time.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    time.setText(taskTime);
                    time.setTextSize(18);
                    time.setId(taskTime.hashCode());
                    cLayOut.addView(time);

                    // Difficulty textView
                    final TextView difficulty = new TextView(getContext());
                    difficulty.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 0));

                    //set color based on difficulty
                    if (taskDifficulty.equals("easy")) {
                        difficulty.setBackgroundResource(R.color.colorEasy);
                    } else if (taskDifficulty.equals("medium")) {
                        difficulty.setBackgroundResource(R.color.colorMedium);
                    } else {
                        difficulty.setBackgroundResource(R.color.colorHard);
                    }
                    difficulty.setText("  ");
                    //difficulty.setTextSize(18);
                    difficulty.setId(taskTime.hashCode() + taskName.hashCode());
                    cLayOut.addView(difficulty);

                    //set onclick listener to all tasks to modify them
                    cLayOut.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            //parameters View v on which was clicked
                            //j is the date on which this task was planned
                            //i the the task object that was planned
                            //date is the TextView containing the date
                            modifyTask(v, i.getDate(), j.getTask(), date);
                        }
                    });

                    // Constrains of textViews
                    ConstraintSet constraintSet = new ConstraintSet();
                    constraintSet.clone(cLayOut);
                    // Constrain time to be to the left of difficulty
                    constraintSet.connect(time.getId(), ConstraintSet.RIGHT, difficulty.getId(), ConstraintSet.LEFT, 100);
                    // Constrain difficulty to be on the right side of the screen
                    constraintSet.connect(difficulty.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT);
                    // Constrain name to be to the left of time
                    constraintSet.connect(task.getId(), ConstraintSet.RIGHT, time.getId(), ConstraintSet.LEFT, 10);
                    //Constrain name to be on the left side of the screen
                    constraintSet.connect(task.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0);
                    //Constrain difficulty to be as high as parent
                    constraintSet.connect(difficulty.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
                    constraintSet.connect(difficulty.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
                    constraintSet.applyTo(cLayOut);
                }
            }
        }
    }

    private void modifyTask(final View view, final String date, final Task task, final TextView dateText) {

        //show modify task layout as popup
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this.getActivity());
        final View taskPopUpView = getLayoutInflater().inflate(R.layout.popup_modify_task, null);
        dialogBuilder.setView(taskPopUpView);
        dialog = dialogBuilder.create();
        dialog.show();

        //find the buttons and textView
        Button taskDelete = (Button) taskPopUpView.findViewById(R.id.deleteButton);
        Button taskCancel = (Button) taskPopUpView.findViewById(R.id.cancelButton);
        TextView taskDeleteText = (TextView) taskPopUpView.findViewById(R.id.deleteTextView);
        //get task name from task object
        final String taskName = (String) task.getName();

        //set textView to contain name of the clicked task
        taskDeleteText.setText("Delete " + taskName + " from the planning");

        //add onclick listener to the cancel button which closes the popup
        taskCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        //add onclick listener to the delete button
        taskDelete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //remove the clicked view from the UI
                view.setVisibility(View.GONE);
                //remove the task in the schedule
                scheduler.completeTask(taskName);
                updateDatabase();
                //if las task on a date is removed also remove the date in the schedule and the date textview from the UI
                if (!schedule.contains(date)) {
                    dateText.setVisibility(View.GONE);
                }
                dialog.dismiss();
            }
        });
    }

    public void updateDatabase() {
        db.collection("users").document(user.getUid()).set(scheduler, SetOptions.merge());
    }
}
