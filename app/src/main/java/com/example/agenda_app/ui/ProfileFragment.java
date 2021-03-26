package com.example.agenda_app.ui;

import android.app.AlertDialog;
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
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.agenda_app.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class ProfileFragment extends Fragment {

    private AlertDialog dialog;
    private AlertDialog dialogShow;
    private final String TAG = "ProfileFragment";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public View onCreateView(@NonNull final LayoutInflater inflater,
                             final ViewGroup container,
                             final Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container,
                false);

        Button btnResetPass = root.findViewById(R.id.buttonResetPass);
        btnResetPass.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(final View v) {
                createNewResetPassDialog();
            }
        });

        final TextView emailField = root.findViewById(R.id.setEmail);
        final TextView usernameField =
                root.findViewById(R.id.setUserName);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // get the email of the current user
        emailField.setText(user.getEmail());

        db.collection("users")
                // Find the data of the user where the ID is equal to the
                // current user
                .whereEqualTo(FieldPath.documentId(), user.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull final
                                           Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                usernameField.setText(
                                        (CharSequence) document.get("name"));
                            }
                        }
                    }
                });
        return root;
    }

    /**
     * Create new pop-up to reset password.
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void createNewResetPassDialog() {
        //Setup popup window
        AlertDialog.Builder dialogBuilder = new
                AlertDialog.Builder(this.getActivity());
        final View resetPassPopUpView = getLayoutInflater().inflate(
                R.layout.reset_pw_popup, null);
        Button btnSave = resetPassPopUpView.findViewById(R.id.button_Save);
        Button btnCancel = resetPassPopUpView.findViewById(R.id.button_Cancel);
        final TextView editOldPass = resetPassPopUpView.findViewById(R.id.edit_passOld);
        final TextView editNewPass = resetPassPopUpView.findViewById(R.id.edit_passNew);

        dialogBuilder.setView(resetPassPopUpView);
        dialogShow = dialogBuilder.create();
        dialogShow.show();

        //Set the cancel button to close the dialog
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                dialogShow.dismiss();
            }
        });

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        //Set the save button to save the new password
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                String oldPass = editOldPass.getText().toString().trim();
                final String newPass = editNewPass.getText().toString().trim();

                // If field for old password is left empty
                if (oldPass.isEmpty()) {
                    editOldPass.requestFocus();
                    editOldPass.setError("Old password field cannot be left "
                            + "empty!");
                } else if (newPass.isEmpty()) {
                    editNewPass.requestFocus();
                    editNewPass.setError("New password field cannot be left"
                            + " empty!");
                } else if (newPass.equals(oldPass)) {
                    editNewPass.requestFocus();
                    editNewPass.setError("New password cannot be the same as"
                            + " the old password!");
                } else if (newPass.length() < 6) {
                        editNewPass.requestFocus();
                        editNewPass.setError("The new password should contain"
                                + " at least 6 characters");
                } else {
                    AuthCredential credential = EmailAuthProvider
                            .getCredential(user.getEmail(), oldPass);

                    user.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        user.updatePassword(newPass)
                                                .addOnCompleteListener(
                                                        new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(getActivity(),
                                                            "Password updated",
                                                            Toast.LENGTH_SHORT).show();
                                                            dialogShow.dismiss();
                                                } else {
                                                    Toast.makeText(getActivity(),
                                                            "Error! Password not updated",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    } else {
                                        Toast.makeText(getActivity(),
                                                "Error! Old password does not match!",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }

//

             //   }
            }
        });
    }
}
