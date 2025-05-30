package com.group8.busbookingapp.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.group8.busbookingapp.R;
import com.group8.busbookingapp.dto.ApiResponse;
import com.group8.busbookingapp.dto.BookingRequest;
import com.group8.busbookingapp.model.Booking;
import com.group8.busbookingapp.network.ApiClient;
import com.group8.busbookingapp.network.ApiService;
import java.math.BigDecimal;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PassengerInfoActivity extends AppCompatActivity {
    private final String TAG = "PassengerInfoActivity";
    private TextView tvRouteInfo, tvDateInfo, tvPickupInfo, tvDropoffInfo, tvSeat;
    private TextInputEditText etFullName, etPhone, etEmail;
    private CheckBox cbSaveInfo;
    private Button btnConfirm;
    private ImageButton btnBack;
    private Toolbar toolbar;
    private String tripId, pickupPointId, dropoffPointId, arrivalTime;
    private ArrayList<String> selectedSeatIds, selectedSeatNumber;
    private BigDecimal totalPrice;
    private View loadingView;
    private ActivityResultLauncher<Intent> loginLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_info);

        loadingView = getLayoutInflater().inflate(R.layout.layout_loading, null);
        loadingView.setVisibility(View.GONE);
        ((ViewGroup) findViewById(android.R.id.content)).addView(loadingView);

        toolbar = findViewById(R.id.toolbar);
        tvRouteInfo = findViewById(R.id.tvRouteInfo);
        tvDateInfo = findViewById(R.id.tvDateInfo);
        tvSeat = findViewById(R.id.tvSeats);
        tvPickupInfo = findViewById(R.id.tvPickupInfo);
        tvDropoffInfo = findViewById(R.id.tvDropoffInfo);
        etFullName = findViewById(R.id.etFullName);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        cbSaveInfo = findViewById(R.id.cbSaveInfo);
        btnConfirm = findViewById(R.id.btnConfirm);
        btnBack = findViewById(R.id.btn_back);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Initialize Activity Result Launcher
        loginLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        // Login successful, proceed with booking
                        submitBooking();
                    } else {
                        Toast.makeText(this, "Đăng nhập bị hủy", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });

        tripId = getIntent().getStringExtra("TRIP_ID");
        pickupPointId = getIntent().getStringExtra("PICKUP_POINT_ID");
        dropoffPointId = getIntent().getStringExtra("DROPOFF_POINT_ID");
        selectedSeatIds = getIntent().getStringArrayListExtra("SELECTED_SEAT_IDS");
        selectedSeatNumber = getIntent().getStringArrayListExtra("SELECTED_SEAT_NUMBER");
        double totalPriceDouble = getIntent().getDoubleExtra("TOTAL_PRICE", 0.0);
        totalPrice = BigDecimal.valueOf(totalPriceDouble);
        String routeName = getIntent().getStringExtra("ROUTE_NAME");
        String dateTime = getIntent().getStringExtra("DATE_TIME");
        String pickupAddress = getIntent().getStringExtra("PICKUP_ADDRESS");
        String dropoffAddress = getIntent().getStringExtra("DROPOFF_ADDRESS");

        tvRouteInfo.setText(routeName != null ? routeName : "N/A");
        tvDateInfo.setText(dateTime != null ? dateTime : "N/A");
        tvSeat.setText(String.join(", ", selectedSeatNumber));
        tvPickupInfo.setText(pickupAddress != null ? pickupAddress : "N/A");
        tvDropoffInfo.setText(dropoffAddress != null ? dropoffAddress : "N/A");

        // Load saved info if available
        SharedPreferences prefs = getSharedPreferences("PassengerInfo", MODE_PRIVATE);
        etFullName.setText(prefs.getString("fullName", ""));
        etPhone.setText(prefs.getString("phone", ""));
        etEmail.setText(prefs.getString("email", ""));

        btnConfirm.setOnClickListener(v -> submitBooking());
        btnBack.setOnClickListener(v -> finish());
    }

    private void submitBooking() {
        String fullName = etFullName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        boolean saveInfo = cbSaveInfo.isChecked();

        if (fullName.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!android.util.Patterns.PHONE.matcher(phone).matches()) {
            Toast.makeText(this, "Số điện thoại không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (saveInfo) {
            getSharedPreferences("PassengerInfo", MODE_PRIVATE)
                    .edit()
                    .putString("fullName", fullName)
                    .putString("phone", phone)
                    .putString("email", email)
                    .apply();
        }

        BookingRequest request = new BookingRequest();
        request.setTripId(tripId);
        request.setSeatIds(selectedSeatIds);
        request.setTotalPrice(totalPrice);
        request.setPickupStopPointId(pickupPointId);
        request.setDropoffStopPointId(dropoffPointId);
        BookingRequest.PassengerDetail passengerDetail = new BookingRequest.PassengerDetail();
        passengerDetail.setFullName(fullName);
        passengerDetail.setPhoneNumber(phone);
        passengerDetail.setEmail(email);
        request.setPassengerDetail(passengerDetail);

        Log.d(TAG, "submitBooking: " + request.toString());

        showLoading();

        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String jwtToken = sharedPreferences.getString("token", null);

        if (jwtToken == null) {
            new AlertDialog.Builder(this)
                    .setTitle("Yêu cầu đăng nhập")
                    .setMessage(R.string.please_login)
                    .setPositiveButton("OK", (dialog, which) -> {
                        Intent intent = new Intent(this, LoginActivity.class);
                        intent.putExtra("FROM_BOOKING", true);
                        loginLauncher.launch(intent);
                    })
                    .setCancelable(false)
                    .show();
            return;
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ApiResponse<Booking>> call = apiService.bookTrip("Bearer " + jwtToken, request);
        call.enqueue(new Callback<ApiResponse<Booking>>() {
            @Override
            public void onResponse(Call<ApiResponse<Booking>> call, Response<ApiResponse<Booking>> response) {
                hideLoading();
                Log.d(TAG, "onResponse: " + response);
                Log.d(TAG, "onResponse: " + response.body());
                Log.d(TAG, "onResponse: " + response.isSuccessful());
                Log.d(TAG, "onResponse: " + response.body().getStatus());

                if (response.isSuccessful() && response.body() != null && response.body().getStatus().equals("success")) {
                    Toast.makeText(PassengerInfoActivity.this, "Đặt vé thành công", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(PassengerInfoActivity.this, TicketActivity.class);
                    intent.putExtra("BOOKING_ID", response.body().getData().getId());
                    intent.putExtra("CITY_START", response.body().getData().getStartCity());
                    intent.putExtra("CITY_END", response.body().getData().getEndCity());
                    intent.putExtra("TOTAL_PRICE", response.body().getData().getTotalPrice());
                    intent.putExtra("BOOKING_CODE", response.body().getData().getBookingCode());
                    intent.putExtra("SEAT", String.join(", ", selectedSeatNumber));
                    intent.putExtra("TIME", response.body().getData().getDepartureTime());
                    intent.putExtra("ARRIVAL_TIME", response.body().getData().getArrivalTime());
                    intent.putExtra("PICKUP", response.body().getData().getStartCity()); // Adjust if you have specific pickup data
                    intent.putExtra("DROPOFF", response.body().getData().getEndCity()); // Adjust if you have specific dropoff data

                    startActivity(intent);
                    finish();
                } else {
                    String errorMsg = response.body() != null ? response.body().getMessage() : "Unknown error";
                    Toast.makeText(PassengerInfoActivity.this, "Đặt vé thất bại: " + errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Booking>> call, Throwable t) {
                hideLoading();
                Toast.makeText(PassengerInfoActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading() {
        loadingView.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        loadingView.setVisibility(View.GONE);
    }
}