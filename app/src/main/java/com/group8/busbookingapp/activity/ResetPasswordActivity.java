package com.group8.busbookingapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.group8.busbookingapp.R;
import com.group8.busbookingapp.dto.ApiResponse;
import com.group8.busbookingapp.network.ApiClient;
import com.group8.busbookingapp.network.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {

    private TextInputEditText etNewPassword, etConfirmNewPassword;
    private Button btnUpdatePassword, btnGoToLogin;
    private View cardSuccess;
    private String email="nguyenhoaitan31082004@gmail.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        // Lấy email từ intent
//        email = getIntent().getStringExtra("email");

        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmNewPassword = findViewById(R.id.etConfirmNewPassword);
        btnUpdatePassword = findViewById(R.id.btnUpdatePassword);
        cardSuccess = findViewById(R.id.cardSuccess);
        btnGoToLogin = findViewById(R.id.btnGoToLogin);

        btnUpdatePassword.setOnClickListener(v -> handlePasswordUpdate());
        btnGoToLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void handlePasswordUpdate() {
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmNewPassword.getText().toString().trim();

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showToast("Vui lòng nhập đầy đủ thông tin");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showToast("Mật khẩu xác nhận không khớp");
            return;
        }

        if (newPassword.length() < 6) {
            showToast("Mật khẩu phải có ít nhất 6 ký tự");
            return;
        }
        Log.d("email",email);
        Log.d("new password",newPassword);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ApiResponse<String>> call = apiService.resetPassword(email, newPassword);

        call.enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                Log.d("response:", response.toString());
                if (response.isSuccessful() && response.body() != null && response.body().getStatus().equals("success")) {
                    cardSuccess.setVisibility(View.VISIBLE);
                } else {
                    showToast("Cập nhật thất bại. Vui lòng thử lại.");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                showToast("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
