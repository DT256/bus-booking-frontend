package com.group8.busbookingapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

import com.group8.busbookingapp.R;

public class ProfileFragment extends Fragment {

    private LinearLayout personalInfoItem;
    private LinearLayout changePersonalInfoForm;
    private Button savePersonalInfoButton;
    private Button cancelPersonalInfoButton;
    private EditText editFullName;
    private EditText editEmail;
    private EditText editPhone;

    private LinearLayout changePasswordItem;
    private LinearLayout changePasswordForm;
    private Button savePasswordButton;
    private Button cancelPasswordButton;
    private EditText oldPassword;
    private EditText newPassword;
    private EditText confirmNewPassword;
    private LinearLayout logoutItem;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize views for Personal Info
        personalInfoItem = view.findViewById(R.id.personal_info_item);
        changePersonalInfoForm = view.findViewById(R.id.change_personal_info_form);
        savePersonalInfoButton = view.findViewById(R.id.save_personal_info_button);
        cancelPersonalInfoButton = view.findViewById(R.id.cancel_personal_info_button);
        editFullName = view.findViewById(R.id.edit_full_name);
        editEmail = view.findViewById(R.id.edit_email);
        editPhone = view.findViewById(R.id.edit_phone);

        // Initialize views for Change Password
        changePasswordItem = view.findViewById(R.id.change_password_item);
        changePasswordForm = view.findViewById(R.id.change_password_form);
        savePasswordButton = view.findViewById(R.id.save_password_button);
        cancelPasswordButton = view.findViewById(R.id.cancel_password_button);
        oldPassword = view.findViewById(R.id.old_password);
        newPassword = view.findViewById(R.id.new_password);
        confirmNewPassword = view.findViewById(R.id.confirm_new_password);
        logoutItem = view.findViewById(R.id.logout_item);

        // Set click listener for "Personal Info" item
        personalInfoItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (changePersonalInfoForm.getVisibility() == View.VISIBLE) {
                    changePersonalInfoForm.setVisibility(View.GONE);
                } else {
                    changePersonalInfoForm.setVisibility(View.VISIBLE);
                    changePasswordForm.setVisibility(View.GONE); // Close the other form
                }
            }
        });

        // Set click listener for "Save Personal Info" button
        savePersonalInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullName = editFullName.getText().toString();
                String email = editEmail.getText().toString();
                String phone = editPhone.getText().toString();

                // Logic to update personal info (e.g., API call)
                Toast.makeText(getContext(), "Cập nhật thông tin thành công!", Toast.LENGTH_SHORT).show();
                changePersonalInfoForm.setVisibility(View.GONE);
                editFullName.setText("");
                editEmail.setText("");
                editPhone.setText("");
            }
        });

        // Set click listener for "Cancel Personal Info" button
        cancelPersonalInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePersonalInfoForm.setVisibility(View.GONE);
                editFullName.setText("");
                editEmail.setText("");
                editPhone.setText("");
            }
        });

        // Set click listener for "Change Password" item
        changePasswordItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (changePasswordForm.getVisibility() == View.VISIBLE) {
                    changePasswordForm.setVisibility(View.GONE);
                } else {
                    changePasswordForm.setVisibility(View.VISIBLE);
                    changePersonalInfoForm.setVisibility(View.GONE); // Close the other form
                }
            }
        });

        // Set click listener for "Save Password" button
        savePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPass = oldPassword.getText().toString();
                String newPass = newPassword.getText().toString();
                String confirmPass = confirmNewPassword.getText().toString();

                if (newPass.equals(confirmPass)) {
                    // Logic to update password (e.g., API call)
                    Toast.makeText(getContext(), "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
                    changePasswordForm.setVisibility(View.GONE);
                    oldPassword.setText("");
                    newPassword.setText("");
                    confirmNewPassword.setText("");
                } else {
                    Toast.makeText(getContext(), "Mật khẩu xác nhận không khớp!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set click listener for "Cancel Password" button
        cancelPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePasswordForm.setVisibility(View.GONE);
                oldPassword.setText("");
                newPassword.setText("");
                confirmNewPassword.setText("");
            }
        });

        // Set click listener for "Logout" item
        logoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Logic for logout (e.g., clear session, redirect to login screen)
                Toast.makeText(getContext(), "Đăng xuất thành công!", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}