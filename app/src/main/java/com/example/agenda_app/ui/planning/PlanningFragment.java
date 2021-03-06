package com.example.agenda_app.ui.planning;

import android.os.Bundle;
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

public class PlanningFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_planning, container, false);

        LinearLayout agenda_dash = (LinearLayout) root.findViewById(R.id.agenda_dashboard);
        showPlanning(agenda_dash);

        return root;
    }

    private void showPlanning(LinearLayout agenda_dash) {

        int numTasks = 25; //number of tasks that need to be put into dashboard
        for(int i=0;i<numTasks;i++)
        {
            ConstraintLayout cLayOut = new ConstraintLayout(getContext()); //create constraint layout
            agenda_dash.addView(cLayOut);

            TextView task = new TextView(getContext()); //create TextView for task name and set some Layout
            task.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            // text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            task.setTextSize(26);
            task.setText("task "+i);
            task.setId(R.id.task);
            cLayOut.addView(task); //add task to the layout

            TextView time = new TextView(getContext()); //create TextView for the time of the task
            time.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            time.setText("time " + i);
            time.setTextSize(20);
            time.setId(R.id.time);
            cLayOut.addView(time);

            ConstraintSet constraintSet = new ConstraintSet(); //create constraint set
            constraintSet.clone(cLayOut);
            constraintSet.connect(time.getId(), ConstraintSet.LEFT, task.getId(), ConstraintSet.RIGHT,10); //bind right of task to left of time
            constraintSet.applyTo(cLayOut);
        }
    }
}
