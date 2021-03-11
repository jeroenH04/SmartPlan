package com.example.agenda_app.ui.profile;

import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.ViewUtils;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.agenda_app.R;

public class ProfileFragment extends Fragment {

    private AlertDialog dialog;
    private AlertDialog dialogShow;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        Button btnResetPass = (Button) root.findViewById(R.id.buttonResetPass);
        btnResetPass.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                createNewResetPassDialog();
            }
        });
        return root;
    }

    //Create new dialog to reset password
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void createNewResetPassDialog() {

        //Setup popup window
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this.getActivity());
        final View resetPassPopUpView = getLayoutInflater().inflate(R.layout.reset_pw_popup, null);
        Button btnSave = (Button) resetPassPopUpView.findViewById(R.id.button_Save);
        Button btnCancel = (Button) resetPassPopUpView.findViewById(R.id.button_Cancel);
        final TextView editOldPass = (TextView) resetPassPopUpView.findViewById(R.id.edit_passOld);
        final TextView editNewPass = (TextView) resetPassPopUpView.findViewById(R.id.edit_passNew);

        dialogBuilder.setView(resetPassPopUpView);
        dialogShow = dialogBuilder.create();
        dialogShow.show();

        //Set the cancel button to close the dialog
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogShow.dismiss();
            }
        });

        //Set the save button to save the new password
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPass = editOldPass.getText().toString().trim();
                String newPass = editNewPass.getText().toString().trim();

                //If field for old password is left empty
                if (oldPass.isEmpty()) {
                    editOldPass.requestFocus();
                    editOldPass.setError("Old password field cannot be left empty!");
                } else if (newPass.isEmpty()) { //If field for new password is left empty
                    editNewPass.requestFocus();
                    editNewPass.setError("New password field cannot be left empty!");
                } else if (newPass.equals(oldPass)) { //If the new password added is the same with the old password
                    editNewPass.requestFocus();
                    editNewPass.setError("New password cannot be the same with the old password!");
                }
            }
        });

    }
}