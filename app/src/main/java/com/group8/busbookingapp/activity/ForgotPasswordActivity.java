package com.group8.busbookingapp.activity;

import android.content.Intent;
import android.os.Bundle;
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

public class ForgotPasswordActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);

        Button btnSendRequest = findViewById(R.id.btnSendRequest);
        TextInputEditText etEmail = findViewById(R.id.etEmail);

        btnSendRequest.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();

            if (email.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
                return;
            }

            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            Call<ApiResponse<String>> call = apiService.resendOtp(email);

            call.enqueue(new Callback<ApiResponse<String>>() {
                @Override
                public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(ForgotPasswordActivity.this, "Đã gửi OTP tới email", Toast.LENGTH_SHORT).show();

                        // 👉 Chuyển sang màn hình nhập OTP
                        Intent intent = new Intent(ForgotPasswordActivity.this, OTPActivity.class);
                        intent.putExtra("email", email);
                        intent.putExtra("purpose", "forgot_password");
                        startActivity(intent);
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this, "Không thể gửi OTP. Vui lòng thử lại", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                    Toast.makeText(ForgotPasswordActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });



    }
}
