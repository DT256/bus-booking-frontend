package com.group8.busbookingapp.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.textfield.TextInputEditText;
import com.group8.busbookingapp.R;
import com.group8.busbookingapp.activity.LoginActivity;
import com.group8.busbookingapp.dto.ApiResponse;
import com.group8.busbookingapp.dto.ChangePasswordRequest;
import com.group8.busbookingapp.dto.UpdateUserRequest;
import com.group8.busbookingapp.dto.UserResponse;
import com.group8.busbookingapp.network.ApiClient;
import com.group8.busbookingapp.network.ApiService;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {
    private final String TAG = "ProfileFragment";

    private LinearLayout personalInfoItem;
    private LinearLayout changePersonalInfoForm;
    private Button savePersonalInfoButton;
    private Button cancelPersonalInfoButton;
    private TextInputEditText editFullName;
    private TextInputEditText editPhone;
    private TextInputEditText editDob;
    private CheckBox cbMale, cbFemale, cbOther;
    private LinearLayout changePasswordItem;
    private LinearLayout changePasswordForm;
    private Button savePasswordButton;
    private Button cancelPasswordButton;
    private TextInputEditText oldPassword;
    private TextInputEditText newPassword;
    private TextInputEditText confirmNewPassword;
    private LinearLayout logoutItem;
    private CircleImageView ivProfileImage;
    private ImageView ivEditPhoto;
    private SharedPreferences sharedPreferences;
    private Calendar selectedCalendar;
    private ApiService apiService;
    private Uri selectedImageUri;
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 100;


    // Launcher để xử lý kết quả từ Intent chọn ảnh
//    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            result -> {
//                if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
//                    selectedImageUri = result.getData().getData();
//                    if (selectedImageUri != null) {
//                        updateProfileImage(selectedImageUri);
//                    }
//                }
//            });





    // Launcher for requesting permission
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    openGallery();
                } else {
                    Toast.makeText(getContext(), "Cần cấp quyền để chọn ảnh", Toast.LENGTH_SHORT).show();
                }
            });

    // Launcher for picking image
    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        updateProfileImage(selectedImageUri);
                        uploadAvatar(selectedImageUri);
                    } else {
                        Toast.makeText(getContext(), "Không thể chọn ảnh", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        sharedPreferences = getActivity().getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        selectedCalendar = Calendar.getInstance();
        apiService = ApiClient.getClient().create(ApiService.class);

        personalInfoItem = view.findViewById(R.id.personal_info_item);
        changePersonalInfoForm = view.findViewById(R.id.change_personal_info_form);
        savePersonalInfoButton = view.findViewById(R.id.save_personal_info_button);
        cancelPersonalInfoButton = view.findViewById(R.id.cancel_personal_info_button);
        editFullName = view.findViewById(R.id.edit_full_name);
        editPhone = view.findViewById(R.id.edit_phone);
        editDob = view.findViewById(R.id.edit_dob);
        cbMale = view.findViewById(R.id.cb_male);
        cbFemale = view.findViewById(R.id.cb_female);
        cbOther = view.findViewById(R.id.cb_other);
        changePasswordItem = view.findViewById(R.id.change_password_item);
        changePasswordForm = view.findViewById(R.id.change_password_form);
        savePasswordButton = view.findViewById(R.id.save_password_button);
        cancelPasswordButton = view.findViewById(R.id.cancel_password_button);
        oldPassword = view.findViewById(R.id.old_password);
        newPassword = view.findViewById(R.id.new_password);
        confirmNewPassword = view.findViewById(R.id.confirm_new_password);
        logoutItem = view.findViewById(R.id.logout_item);
        ivProfileImage = view.findViewById(R.id.ivProfileImage);
        ivEditPhoto = view.findViewById(R.id.ivEditPhoto);

        loadProfileData();
        setupDatePicker();

        ivProfileImage.setOnClickListener(v -> showImageDialog());
        ivEditPhoto.setOnClickListener(v -> checkStoragePermission());

        personalInfoItem.setOnClickListener(v -> {
            if (changePersonalInfoForm.getVisibility() == View.VISIBLE) {
                changePersonalInfoForm.setVisibility(View.GONE);
            } else {
                changePersonalInfoForm.setVisibility(View.VISIBLE);
                changePasswordForm.setVisibility(View.GONE);
                loadFormData();
            }
        });

        savePersonalInfoButton.setOnClickListener(v -> {
            String fullName = editFullName.getText().toString();
            String phone = editPhone.getText().toString();
            String dob = editDob.getText().toString();
            int gender = getSelectedGender();

            if (fullName.isEmpty() || phone.isEmpty() || dob.isEmpty() || gender == 0) {
                Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            UpdateUserRequest request = new UpdateUserRequest();
            request.setFullName(fullName);
            request.setPhoneNumber(phone);
            request.setDateOfBirth(convertDateFormat(dob));
            request.setGender(gender);

            updatePersonalInfo(request);
        });

        cancelPersonalInfoButton.setOnClickListener(v -> {
            changePersonalInfoForm.setVisibility(View.GONE);
            clearForm();
        });

        changePasswordItem.setOnClickListener(v -> {
            if (changePasswordForm.getVisibility() == View.VISIBLE) {
                changePasswordForm.setVisibility(View.GONE);
            } else {
                changePasswordForm.setVisibility(View.VISIBLE);
                changePersonalInfoForm.setVisibility(View.GONE);
            }
        });

        savePasswordButton.setOnClickListener(v -> {
            String oldPass = oldPassword.getText().toString();
            String newPass = newPassword.getText().toString();
            String confirmPass = confirmNewPassword.getText().toString();

            if (newPass.equals(confirmPass)) {
                ChangePasswordRequest request = new ChangePasswordRequest();
                request.setOldPassword(oldPass);
                request.setNewPassword(newPass);
                request.setConfirmNewPassword(confirmPass);
                changePassword(request);
            } else {
                Toast.makeText(getContext(), "Mật khẩu xác nhận không khớp!", Toast.LENGTH_SHORT).show();
            }
        });

        cancelPasswordButton.setOnClickListener(v -> {
            changePasswordForm.setVisibility(View.GONE);
            clearPasswordForm();
        });

        logoutItem.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear().apply();
            Toast.makeText(getContext(), "Đăng xuất thành công!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
        });

        return view;
    }

    private void setupDatePicker() {
        editDob.setOnClickListener(v -> {
            int year = selectedCalendar.get(Calendar.YEAR);
            int month = selectedCalendar.get(Calendar.MONTH);
            int day = selectedCalendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getContext(),
                    (view, y, m, d) -> {
                        selectedCalendar.set(y, m, d);
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        editDob.setText(sdf.format(selectedCalendar.getTime()));
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });
    }

//    private void loadProfileData() {
//        String avatarUrl = sharedPreferences.getString("avatarUrl", "");
//        if (getContext() != null) {
//            Glide.with(getContext())
//                    .load(avatarUrl.isEmpty() ? R.drawable.default_avatar : Uri.parse(avatarUrl))
//                    .apply(new RequestOptions()
//                            .placeholder(R.drawable.default_avatar)
//                            .error(R.drawable.default_avatar)
//                            .centerCrop())
//                    .into(ivProfileImage);
//        }
//    }

    private void loadFormData() {
        editFullName.setText(sharedPreferences.getString("username", ""));
        editPhone.setText(sharedPreferences.getString("phoneNumber", ""));
        String dobStr = sharedPreferences.getString("dateOfBirth", "");
        if (!dobStr.isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date dob = sdf.parse(dobStr);
                selectedCalendar.setTime(dob);
                SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy");
                editDob.setText(displayFormat.format(dob));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String genderStr = sharedPreferences.getString("gender", "0");
        Log.d(TAG, "loadFormData: " + genderStr);
        int gender = Integer.parseInt(genderStr);
        cbMale.setChecked(gender == 1);
        cbFemale.setChecked(gender == 2);
        cbOther.setChecked(gender == 3);
    }

    private void clearForm() {
        editFullName.setText("");
        editPhone.setText("");
        editDob.setText("");
        cbMale.setChecked(false);
        cbFemale.setChecked(false);
        cbOther.setChecked(false);
        selectedImageUri = null;
    }

    private void clearPasswordForm() {
        oldPassword.setText("");
        newPassword.setText("");
        confirmNewPassword.setText("");
    }

    private int getSelectedGender() {
        if (cbMale.isChecked()) return 1;
        if (cbFemale.isChecked()) return 2;
        if (cbOther.isChecked()) return 3;
        return 0;
    }

    private String convertDateFormat(String input) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date date = inputFormat.parse(input);
            // Set time to midnight (00:00:00)
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            return outputFormat.format(calendar.getTime());
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private void showImageDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_image, null);
        ImageView dialogImageView = dialogView.findViewById(R.id.dialog_image_view);

        Glide.with(getContext())
                .load(selectedImageUri != null ? selectedImageUri : Uri.parse(sharedPreferences.getString("avatarUrl", "")))
                .apply(new RequestOptions()
                        .placeholder(R.drawable.default_avatar)
                        .error(R.drawable.default_avatar)
                        .centerCrop())
                .into(dialogImageView);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogView);
        builder.setPositiveButton("Đóng", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_CODE_STORAGE_PERMISSION);
        } else {
            openGallery();
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    private void updateProfileImage(Uri imageUri) {
        if (getContext() != null) {
            Glide.with(getContext())
                    .load(imageUri)
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.default_avatar)
                            .error(R.drawable.default_avatar)
                            .centerCrop())
                    .into(ivProfileImage);
            selectedImageUri = imageUri;
        }
    }

    private void loadProfileData() {
        String avatarUrl = sharedPreferences.getString("avatarUrl", "");
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.default_avatar)
                .error(R.drawable.default_avatar)
                .centerCrop();

        if (getContext() != null) {
            Glide.with(getContext())
                    .load(avatarUrl.isEmpty() ? R.drawable.default_avatar : Uri.parse(avatarUrl))
                    .apply(options)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            if (e != null) {
                                e.logRootCauses("Glide load failed");
                            }
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(ivProfileImage);
        }
    }

    private String getJwtToken() {
        return sharedPreferences.getString("token", "");
    }

    private String getRealPathFromURI(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContext().getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        }
        return uri.getPath();
    }

    private void uploadAvatar(Uri imageUri) {
        try {
            String filePath = getRealPathFromURI(imageUri);
            File file = new File(filePath);
            if (!file.exists()) {
                Toast.makeText(getContext(), "Không thể tìm thấy file ảnh", Toast.LENGTH_SHORT).show();
                return;
            }

            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("avatar", file.getName(), requestFile);

            String token = getJwtToken();
            if (token.isEmpty()) {
                Toast.makeText(getContext(), "Không tìm thấy token đăng nhập", Toast.LENGTH_SHORT).show();
                return;
            }

            Call<ApiResponse<UserResponse>> call = apiService.updateAvatar("Bearer " + token, body);
            call.enqueue(new Callback<ApiResponse<UserResponse>>() {
                @Override
                public void onResponse(Call<ApiResponse<UserResponse>> call, Response<ApiResponse<UserResponse>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponse<UserResponse> apiResponse = response.body();
                        if (apiResponse.getData() != null) {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("avatarUrl", apiResponse.getData().getAvatarUrl());
                            editor.apply();
                            Toast.makeText(getContext(), "Cập nhật avatar thành công", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Cập nhật avatar thất bại", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        String errorMessage = "Lỗi server";
                        try {
                            if (response.errorBody() != null) {
                                errorMessage = response.errorBody().string();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(getContext(), "Cần có tệp hình đại diện", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<UserResponse>> call, Throwable t) {
                    Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Lỗi xử lý ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void updatePersonalInfo(UpdateUserRequest request) {
        String token = getJwtToken();
        if (token.isEmpty()) {
            Toast.makeText(getContext(), "Không tìm thấy token đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<ApiResponse<UserResponse>> call = apiService.updatePersonalInfo("Bearer " + token, request);
        call.enqueue(new Callback<ApiResponse<UserResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserResponse>> call, Response<ApiResponse<UserResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<UserResponse> apiResponse = response.body();
                    if (apiResponse.getData() != null) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("username", apiResponse.getData().getFullName());
                        editor.putString("phoneNumber", apiResponse.getData().getPhoneNumber());
                        editor.putString("dateOfBirth", apiResponse.getData().getDateOfBirth());
                        editor.putString("gender", apiResponse.getData().getGender());
                        editor.apply();
                        changePersonalInfoForm.setVisibility(View.GONE);
                        clearForm();
                        Toast.makeText(getContext(), "Thông tin cá nhân đã được cập nhật thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Cập nhật thông tin thất bại", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorMessage = "Lỗi server";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                    Log.d(TAG, "onResponse: " + errorMessage);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserResponse>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    private void changePassword(ChangePasswordRequest request) {
        String token = getJwtToken();
        if (token.isEmpty()) {
            Toast.makeText(getContext(), "Không tìm thấy token đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<ApiResponse<String>> call = apiService.changePassword("Bearer " + token, request);
        call.enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<String> apiResponse = response.body();
                    changePasswordForm.setVisibility(View.GONE);
                    clearPasswordForm();
                    Toast.makeText(getContext(), "Đã thay đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                } else {
                    String errorMessage = "Lỗi server";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getContext(), "Mật khẩu cũ không đúng", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}