package com.group8.busbookingapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.group8.busbookingapp.R;
import com.group8.busbookingapp.dto.ApiResponse;
import com.group8.busbookingapp.model.Login;
import com.group8.busbookingapp.model.LoginResponse;
import com.group8.busbookingapp.network.ApiClient;
import com.group8.busbookingapp.network.ApiService;

import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    TextView tvRegister;
    TextView tvForgotPassword;
    private Button btnLogin;
    private EditText etEmail, etPassword;
    private View loadingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tvRegister = findViewById(R.id.tvRegister);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        loadingLayout = findViewById(R.id.loadingLayout);

        showLoading(false);


        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ email và mật khẩu", Toast.LENGTH_SHORT).show();
            } else if (password.length() < 6) {
                Toast.makeText(this, "Mật khẩu yêu cầu ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
            } else {
                loginUser(email, password);
            }
        });
    }


    private void loginUser(String email, String password) {
        showLoading(true);
        ApiService ApiService = ApiClient.getClient().create(ApiService.class);
        Login request = new Login(email, password);

        ApiService.login(request).enqueue(new Callback<ApiResponse<LoginResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<LoginResponse>> call, Response<ApiResponse<LoginResponse>> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<LoginResponse> apiResponse = response.body();
                    LoginResponse loginData = apiResponse.getData();
                    if (loginData != null) {
                        String jwt = loginData.getJwt();
                        String username = loginData.getUsername();
                        String message = loginData.getMessage();

                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();

                        // Lưu token
                        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                        sharedPreferences.edit().putString("token", jwt).apply();
                        sharedPreferences.edit().putString("username", username).apply();

                        // Chuyển qua SearchActivity
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Dữ liệu trả về từ server rỗng", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    try {
                        // Parse lỗi từ response.errorBody()
                        String errorJson = response.errorBody().string();
                        Gson gson = new Gson();
                        Type type = new TypeToken<ApiResponse<Object>>() {
                        }.getType();
                        ApiResponse<?> errorResponse = gson.fromJson(errorJson, type);

                        String errorMessage = errorResponse.getMessage();
                        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(LoginActivity.this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<LoginResponse>> call, Throwable t) {
                showLoading(false);
                Toast.makeText(LoginActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void showLoading(boolean show) {
        if (loadingLayout != null) {
            loadingLayout.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }
}
