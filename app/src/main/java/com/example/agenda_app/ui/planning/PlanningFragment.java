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

import com.example.agenda_app.R;
import com.example.agenda_app.Task;
import com.example.agenda_app.TaskScheduler;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PlanningFragment extends Fragment {

    private AlertDialog dialog;
    private Map<String, Map<Task,String>> schedule;
    private TaskScheduler taskScheduler = new TaskScheduler();

    @RequiresApi(api = Build.VERSION_CODES.N)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_planning, container, false);

        LinearLayout agenda_dash = (LinearLayout) root.findViewById(R.id.agenda_dashboard);

        //todo remove after reading from database
        //creating test data map
      /*  final Map<Task, String> date1 = new HashMap<>();
        date1.put(new Task("test", "", "", "Hard", "", "", 8),"10:30-12:00");
        date1.put(new Task("test2", "", "", "Medium", "", "", 8),"13:00-16:00");
        final Map<Task, String> date2 = new HashMap<>();
        date2.put(new Task("1test1", "", "", "Medium", "", "", 8),"08:30-10:00");
        date2.put(new Task("2test2", "", "", "Medium", "", "", 8),"10:00-12:00");
        date2.put(new Task("3test3", "", "", "Hard", "", "", 8),"12:30-14:00");
        date2.put(new Task("4test4", "", "", "Easy", "", "", 8),"15:00-17:00");
        schedule.put("13-03-2021", date1);
        schedule.put("15-04-2022", date2);*/
        try {
            taskScheduler.addAvailability("15-03-2021", "10:00-18:00");
            taskScheduler.addAvailability("15-04-2021", "11:00-20:00");
            taskScheduler.addAvailability("16-04-2021", "12:00-20:00");
            taskScheduler.addAvailability("17-04-2021", "13:00-20:00");
            taskScheduler.addAvailability("16-05-2021", "12:00-17:00");
            taskScheduler.addTask("clean_room", "03:30", "normal", "Hard", "17-05-2021", "14-03-2021");
            taskScheduler.addTask("get_coffee", "02:00", "intense", "Easy", "18-05-2021", "14-03-2021");
            taskScheduler.addTask("sleep", "16:00", "intense", "Easy", "18-05-2021", "14-03-2021");
            taskScheduler.createSchedule();
        } catch (Exception e) {
            alertView(e.getMessage());
        }

        showPlanning(agenda_dash);

        return root;
    }

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

    private void showPlanning(LinearLayout agenda_dash) {
        //get a set of all dates
        schedule = taskScheduler.getSchedule();
        Set<String> dates = schedule.keySet();

        //for all dates j where a task is planned
        for (final String j : dates) {
            //get a map containing all tasks with there time
            Map<Task, String> taskList = schedule.get(j);
            //get a set of all tasks
            Set<Task> tasks = taskList.keySet();

            //create TextView for the date of the task
            final TextView date = new TextView(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0,50,0,0);
            date.setLayoutParams(params);

            date.setText(j);
            date.setTextSize(20);
            agenda_dash.addView(date);

            //for all tasks i planned on date j
            for (final Task i : tasks) {
                //get name from task object
                final String taskName = i.getName();
                //get date from the task map
                String taskTime = taskList.get(i);
                //get difficulty from task object
                String taskDifficulty = i.getDifficulty();

                // Constraint Layout
                final ConstraintLayout cLayOut = new ConstraintLayout(getContext());
                cLayOut.setBackgroundResource(R.drawable.customborder);
                ConstraintLayout.LayoutParams conParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
                conParams.setMargins(0,20,0,0);
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
                if (taskDifficulty.equals("Easy")) {
                    difficulty.setBackgroundResource(R.color.colorEasy);
                } else if (taskDifficulty.equals("Medium")) {
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
                        modifyTask(v, j , i, date);
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
        String taskName = (String) task.getName();

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
                schedule.get(date).remove(task);
                //if las task on a date is removed also remove the date in the schedule and the date textview from the UI
                if (schedule.get(date).isEmpty()) {
                    schedule.remove(date);
                    dateText.setVisibility(View.GONE);
                }
                dialog.dismiss();
            }
        });
    }
}
