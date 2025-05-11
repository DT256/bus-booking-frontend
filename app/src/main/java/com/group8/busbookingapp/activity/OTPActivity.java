package com.group8.busbookingapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.group8.busbookingapp.R;
import com.group8.busbookingapp.dto.ApiResponse;
import com.group8.busbookingapp.network.ApiClient;
import com.group8.busbookingapp.network.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OTPActivity extends AppCompatActivity {
    private EditText etOtp1, etOtp2, etOtp3, etOtp4, etOtp5, etOtp6;
    private Button btnVerify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_otp); // XML của bạn

        // Ánh xạ các ô nhập OTP
        etOtp1 = findViewById(R.id.etOtp1);
        etOtp2 = findViewById(R.id.etOtp2);
        etOtp3 = findViewById(R.id.etOtp3);
        etOtp4 = findViewById(R.id.etOtp4);
        etOtp5 = findViewById(R.id.etOtp5);
        etOtp6 = findViewById(R.id.etOtp6);
        btnVerify = findViewById(R.id.btnVerify);

        String email = getIntent().getStringExtra("email");
        String purpose = getIntent().getStringExtra("purpose");

        TextView tvResendOtp = findViewById(R.id.tvResendOtp);
        tvResendOtp.setOnClickListener(v -> {
            if (email == null || email.isEmpty()) {
                Toast.makeText(this, "Không tìm thấy email để gửi lại OTP", Toast.LENGTH_SHORT).show();
                return;
            }
            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            Call<ApiResponse<String>> call = apiService.resendOtp(email);

            call.enqueue(new Callback<ApiResponse<String>>() {
                @Override
                public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(OTPActivity.this, "OTP đã được gửi lại!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(OTPActivity.this, "Không thể gửi lại OTP", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                    Toast.makeText(OTPActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        btnVerify.setOnClickListener(v -> {
            String otp = etOtp1.getText().toString().trim() +
                    etOtp2.getText().toString().trim() +
                    etOtp3.getText().toString().trim() +
                    etOtp4.getText().toString().trim() +
                    etOtp5.getText().toString().trim() +
                    etOtp6.getText().toString().trim();

            if (otp.length() != 6) {
                Toast.makeText(this, "Vui lòng nhập đủ 6 chữ số OTP", Toast.LENGTH_SHORT).show();
                return;
            }

            // Gọi API xác thực OTP
            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            Call<ApiResponse<String>> call = apiService.activeAccount(email, otp);
            call.enqueue(new Callback<ApiResponse<String>>() {
                @Override
                public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(OTPActivity.this, "Xác thực thành công!", Toast.LENGTH_SHORT).show();
                        // Chuyển sang màn hình đăng nhập
                        if ("register".equals(purpose)) {
                            // Chuyển về LoginActivity
                            Intent intent = new Intent(OTPActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } else if ("forgot_password".equals(purpose)) {
                            // Chuyển đến ResetPasswordActivity
                            Intent intent = new Intent(OTPActivity.this, ResetPasswordActivity.class);
                            intent.putExtra("email", email); // truyền email để đặt lại mật khẩu
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        Log.e("OTP_CHECK", "API Call Failed, Response Code: " + response.code());
                        Toast.makeText(OTPActivity.this, "OTP không hợp lệ hoặc đã hết hạn", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                    Toast.makeText(OTPActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
