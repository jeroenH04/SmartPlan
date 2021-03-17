package com.example.agenda_app.ui.profile;

import android.app.AlertDialog;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.ViewUtils;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.agenda_app.MainActivity;
import com.example.agenda_app.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ProfileFragment extends Fragment {

    private AlertDialog dialog;
    private AlertDialog dialogShow;
    final String TAG = "ProfileFragment";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

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

        final TextView emailField = (TextView) root.findViewById(R.id.setEmail);
        final TextView usernameField = (TextView) root.findViewById(R.id.setUserName);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        emailField.setText( user.getEmail() ); // get the email of the current user

        db.collection("users")
                // Find the data of the user where the ID is equal to the current user
                .whereEqualTo(FieldPath.documentId(),user.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                usernameField.setText((CharSequence) document.get("name"));
                            }
                        }
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

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


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
                } else {
                    user.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Password updated successfully!");
                                Toast.makeText(getActivity(),"Password replaced",Toast.LENGTH_SHORT).show();
                            } else {
                                Log.d(TAG, "Error password not updated!");
                                Toast.makeText(getActivity(),"Password not replaced",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    dialogShow.dismiss();
                }
            }
        });

    }
}