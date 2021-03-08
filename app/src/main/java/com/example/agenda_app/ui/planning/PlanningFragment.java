package com.example.agenda_app.ui.planning;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import com.example.agenda_app.R;

import org.w3c.dom.Text;

public class PlanningFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_planning, container, false);

        LinearLayout agenda_dash = (LinearLayout) root.findViewById(R.id.agenda_dashboard);
        showPlanning(agenda_dash);

        return root;
    }

    private void showPlanning(LinearLayout agenda_dash) {

        int numDates = 5; //number of dates on which tasks are planned todo

        for (int j = 0; j < numDates; j++) {

            int numTasks = j + 2; //number of tasks that need to be put into dashboard todo
            String taskDate = "Date " + j; //date of the tasks todo

            TextView date = new TextView(getContext()); //create TextView for the date of the task
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0,50,0,0);
            date.setLayoutParams(params);

            date.setText(taskDate);
            date.setTextSize(20);
            agenda_dash.addView(date);

            for (int i = 0; i < numTasks; i++) {

                String taskName = "Task " + i; //name of the task todo
                String taskTime = "12:00-24:00 "; //start and end time of the task todo
                String taskDifficulty = "Hard"; //difficulty of the task todo

                //show purpose only
                if ((i+j)%3 ==0) {
                    taskDifficulty = "Easy";
                } else if ((i + j) %3 == 1){
                    taskDifficulty = "Medium";
                } else {
                    taskDifficulty = "Hard";
                }

                // Constraint Layout
                ConstraintLayout cLayOut = new ConstraintLayout(getContext());
                cLayOut.setBackgroundResource(R.drawable.customborder);
                ConstraintLayout.LayoutParams conParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
                conParams.setMargins(0,20,0,0);
                cLayOut.setLayoutParams(conParams);
                agenda_dash.addView(cLayOut);

                // Name Textview
                TextView task = new TextView(getContext());
                task.setLayoutParams(new ViewGroup.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT));
                task.setTextSize(22);
                task.setText(taskName);
                task.setId(taskName.hashCode());
                cLayOut.addView(task);

                // Time textView
                TextView time = new TextView(getContext());
                time.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                time.setText(taskTime);
                time.setTextSize(18);
                time.setId(taskTime.hashCode());
                cLayOut.addView(time);

                // Difficulty textView
                TextView difficulty = new TextView(getContext());
                difficulty.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 0));

                //set color based on difficulty
                if (taskDifficulty == "Easy") {
                    difficulty.setBackgroundResource(R.color.colorEasy);
                } else if (taskDifficulty == "Medium") {
                    difficulty.setBackgroundResource(R.color.colorMedium);
                } else {
                    difficulty.setBackgroundResource(R.color.colorHard);
                }
                difficulty.setText("  ");
                //difficulty.setTextSize(18);
                difficulty.setId(taskTime.hashCode() + taskName.hashCode());
                cLayOut.addView(difficulty);

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
