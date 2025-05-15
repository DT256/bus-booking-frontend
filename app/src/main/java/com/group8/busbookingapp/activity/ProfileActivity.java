package com.group8.busbookingapp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.group8.busbookingapp.R;
import com.group8.busbookingapp.dto.ApiResponse;
import com.group8.busbookingapp.model.User;
import com.group8.busbookingapp.network.ApiClient;
import com.group8.busbookingapp.network.ApiService;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private Uri selectedImageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ImageView ivEditPhoto = findViewById(R.id.ivEditPhoto);

        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String token = prefs.getString("token", "");
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ApiResponse<User>> call = apiService.getProfile("Bearer " + token);
        call.enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    User user = response.body().getData();
                    displayUserData(user);
                } else {
                    Toast.makeText(getApplicationContext(), "Lỗi: " + (response.body() != null ? response.body().getMessage() : "Không rõ lỗi"), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                Log.d("Loi ket noi",t.getMessage());
                Toast.makeText(getApplicationContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        ivEditPhoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        CircleImageView imageView = findViewById(R.id.ivProfileImage);
                        imageView.setImageURI(selectedImageUri);
                        Log.d("image",selectedImageUri.toString());
                    }
                }
        );
    }
    private void displayUserData(User user) {
        if (user.getUsername() != null && !user.getUsername().isEmpty()) {
            ((TextInputEditText) findViewById(R.id.etFullName)).setText(user.getUsername());
        }

        if (user.getPhoneNumber() != null && !user.getPhoneNumber().isEmpty()) {
            ((TextInputEditText) findViewById(R.id.etPhone)).setText(user.getPhoneNumber());
        }

        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            ((TextInputEditText) findViewById(R.id.etEmail)).setText(user.getEmail());
        }

//        if (user.getAddress() != null && !user.getAddress().isEmpty()) {
//            ((TextInputEditText) findViewById(R.id.etCity)).setText(user.getAddress());
//        }

//        if (user.getDistrict() != null && !user.getDistrict().isEmpty()) {
//            ((TextInputEditText) findViewById(R.id.etDistrict)).setText(user.getDistrict());
//        }

//        if (user.getAddress() != null && !user.getAddress().isEmpty()) {
//            ((TextInputEditText) findViewById(R.id.etAddress)).setText(user.getAddress());
//        }

//        if (user.getGender() != null && !user.getGender().isEmpty()) {
//            RadioGroup rgGender = findViewById(R.id.rgGender);
//            switch (user.getGender().toLowerCase()) {
//                case "nam":
//                    rgGender.check(R.id.rbMale);
//                    break;
//                case "nữ":
//                    rgGender.check(R.id.rbFemale);
//                    break;
//                default:
//                    rgGender.check(R.id.rbOther);
//                    break;
//            }
//        }
    }

//    private void updateUserProfile() {
//        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
//        String token = prefs.getString("token", "");
//
//        // Lấy dữ liệu từ UI
//        String fullName = ((TextInputEditText) findViewById(R.id.etFullName)).getText().toString();
//        String phone = ((TextInputEditText) findViewById(R.id.etPhone)).getText().toString();
//        String email = ((TextInputEditText) findViewById(R.id.etEmail)).getText().toString();
//        String dob = ((TextInputEditText) findViewById(R.id.etDateOfBirth)).getText().toString();
//        String city = ((TextInputEditText) findViewById(R.id.etCity)).getText().toString();
//        String district = ((TextInputEditText) findViewById(R.id.etDistrict)).getText().toString();
//        String addressDetail = ((TextInputEditText) findViewById(R.id.etAddress)).getText().toString();
//
//        // Giới tính
//        String gender = "Khác";
//        RadioGroup rgGender = findViewById(R.id.rgGender);
//        int checkedId = rgGender.getCheckedRadioButtonId();
//        if (checkedId == R.id.rbMale) gender = "Nam";
//        else if (checkedId == R.id.rbFemale) gender = "Nữ";
//
//        // Tạo JSON từ HashMap
//        Map<String, Object> dataMap = new HashMap<>();
//        dataMap.put("username", fullName);
//        dataMap.put("phoneNumber", phone);
//        dataMap.put("gender", gender);
//        dataMap.put("dateOfBirth", dob); // Đảm bảo đúng định dạng ISO
//
//        Map<String, String> addressMap = new HashMap<>();
//        addressMap.put("city", city);
//        addressMap.put("district", district);
//        addressMap.put("commune", "");
//        addressMap.put("other", addressDetail);
//        dataMap.put("address", addressMap);
//
//        String jsonData = new com.google.gson.Gson().toJson(dataMap);
//        RequestBody dataBody = RequestBody.create(
//                okhttp3.MediaType.parse("application/json"), jsonData);
//
//        // Chuẩn bị avatar nếu có
//        MultipartBody.Part avatarPart = null;
//        if (selectedImageUri != null) {
//            try {
//                File file = new File(FileUtils.getPath(this, selectedImageUri));
//                RequestBody fileBody = RequestBody.create(
//                        MediaType.parse("image/*"), file);
//                avatarPart = MultipartBody.Part.createFormData("avatar", file.getName(), fileBody);
//            } catch (Exception e) {
//                Toast.makeText(this, "Không thể đọc ảnh đại diện", Toast.LENGTH_SHORT).show();
//                return;
//            }
//        }
//
//        ApiService apiService = ApiClient.getClient().create(ApiService.class);
//        Call<ApiResponse<User>> call = apiService.updateProfile("Bearer " + token, avatarPart, dataBody);
//        call.enqueue(new Callback<ApiResponse<User>>() {
//            @Override
//            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    Toast.makeText(ProfileActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(ProfileActivity.this, "Lỗi cập nhật", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
//                Toast.makeText(ProfileActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    public static class FileUtils {
        public static String getPath(Context context, Uri uri) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            if (cursor == null) return null;
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            String path = cursor.getString(idx);
            cursor.close();
            return path;
        }
    }

}
