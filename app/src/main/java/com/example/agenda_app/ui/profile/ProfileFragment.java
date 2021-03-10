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

   // private ProfileViewModel profileViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        /*profileViewModel =
                ViewModelProviders.of(this).get(ProfileViewModel.class);
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        final TextView textView = root.findViewById(R.id.text_profile);

        profileViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root; */
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

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this.getActivity());
        final View resetPassPopUpView = getLayoutInflater().inflate(R.layout.reset_pw_popup, null);

        dialogBuilder.setView(resetPassPopUpView);
        dialogShow = dialogBuilder.create();
        dialogShow.show();


    }
}