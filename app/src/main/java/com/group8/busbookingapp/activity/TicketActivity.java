package com.group8.busbookingapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.group8.busbookingapp.R;
import com.group8.busbookingapp.adapter.BankAdapter;
import com.group8.busbookingapp.dto.ApiResponse;
import com.group8.busbookingapp.dto.Bank;
import com.group8.busbookingapp.dto.PaymentDTO;
import com.group8.busbookingapp.network.ApiClient;
import com.group8.busbookingapp.network.ApiService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TicketActivity extends AppCompatActivity {

    AutoCompleteTextView autoCompleteBanks;
    ArrayList<Bank> bankList = new ArrayList<>();
    BankAdapter bankAdapter;
    Button btnPayTicket;
    ImageButton btnBack, btnHome;
    TextView tvStatus; // For payment status

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticketinfo);
        autoCompleteBanks = findViewById(R.id.autoCompleteBanks);
        btnPayTicket = findViewById(R.id.btnPayTicket);
        btnBack = findViewById(R.id.btnBack);
        btnHome = findViewById(R.id.btnHome);
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView tvBookingId = findViewById(R.id.tvBookingId);
        TextView tvDeparturePlace = findViewById(R.id.tvDeparturePlace);
        TextView tvArrivalPlace = findViewById(R.id.tvArrivalPlace);
        TextView tvPrice = findViewById(R.id.tvPrice);
        TextView tvSeatNumber = findViewById(R.id.tvSeatNumber);
        TextView tvDepartureTime = findViewById(R.id.tvDepartureTime);
        TextView tvPassengerName = findViewById(R.id.tvPassengerName);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        Intent intent = getIntent();
        String bookingId = intent.getStringExtra("BOOKING_ID");
        String cityStart = intent.getStringExtra("CITY_START");
        String cityEnd = intent.getStringExtra("CITY_END");
        int totalPrice = intent.getIntExtra("TOTAL_PRICE", 0);
        String bookingCode = intent.getStringExtra("BOOKING_CODE");
        String seat = intent.getStringExtra("SEAT");
        String departureTime = intent.getStringExtra("TIME");
        String pickup = intent.getStringExtra("PICKUP");
        String dropoff = intent.getStringExtra("DROPOFF");

        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String username = prefs.getString("username", "");

        Log.d("TicketActivity", "Booking ID: " + bookingId);
        Log.d("TicketActivity", "City Start: " + cityStart);
        Log.d("TicketActivity", "City End: " + cityEnd);
        Log.d("TicketActivity", "Total Price: " + totalPrice);
        Log.d("TicketActivity", "Booking Code: " + bookingCode);
        Log.d("TicketActivity", "Seat: " + seat);
        Log.d("TicketActivity", "Departure Time: " + departureTime);
        Log.d("TicketActivity", "Username: " + username);

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String formattedPrice = currencyFormat.format(totalPrice);

        String formattedDepartureTime = "N/A";
        if (departureTime != null) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());
                Date date = inputFormat.parse(departureTime);
                if (date != null) {
                    formattedDepartureTime = outputFormat.format(date);
                }
            } catch (ParseException e) {
                Log.e("TicketActivity", "Error parsing departure time: " + e.getMessage());
                formattedDepartureTime = departureTime;
            }
        }

        // Set data to TextViews
        tvBookingId.setText(bookingCode != null ? bookingCode : "N/A");
        tvDeparturePlace.setText(pickup != null ? pickup : "N/A");
        tvArrivalPlace.setText(dropoff != null ? dropoff : "N/A");
        tvPrice.setText(formattedPrice);
        tvSeatNumber.setText(seat != null ? seat : "N/A");
        tvDepartureTime.setText(formattedDepartureTime);
        tvPassengerName.setText(username);
        if (tvStatus != null) {
            tvStatus.setText("Trạng thái: Chưa thanh toán");
        }

        loadBanksFromApi();

        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        btnBack.setOnClickListener(v -> finish());

        btnHome.setOnClickListener(v -> {
            Intent intentHome = new Intent(this, MainActivity.class);
            startActivity(intentHome);
            finish();
        });

        btnPayTicket.setOnClickListener(v -> {
            Bank selectedBank = null;
            for (Bank bank : bankList) {
                if (bank.getCode().equals(autoCompleteBanks.getText().toString())) {
                    selectedBank = bank;
                    break;
                }
            }
            if (selectedBank == null) {
                Toast.makeText(this, "Vui lòng chọn ngân hàng hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            String baseUrl = ApiClient.getClient().baseUrl().toString();
            Call<ApiResponse<PaymentDTO>> call = apiService.createVnPayPayment(totalPrice, selectedBank.getCode(), bookingId, baseUrl);
            call.enqueue(new Callback<ApiResponse<PaymentDTO>>() {
                @Override
                public void onResponse(Call<ApiResponse<PaymentDTO>> call, Response<ApiResponse<PaymentDTO>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String url = response.body().getData().getPaymentUrl();
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(browserIntent);
                    } else {
                        Toast.makeText(TicketActivity.this, "Lỗi khi tạo thanh toán", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<PaymentDTO>> call, Throwable t) {
                    Toast.makeText(TicketActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Handle deep link
        handleDeepLink(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleDeepLink(intent);
    }

    private void handleDeepLink(Intent intent) {
        Uri uri = intent.getData();
        if (uri != null) {
            String bookingId = uri.getQueryParameter("bookingId");
            String status = uri.getQueryParameter("status");
            if (bookingId != null && status != null) {
                Log.d("TicketActivity", "Deep Link - Booking ID: " + bookingId + ", Status: " + status);
                if ("success".equalsIgnoreCase(status)) {
                    Toast.makeText(this, "Thanh toán thành công!", Toast.LENGTH_SHORT).show();
                    updateUIForSuccess();
                } else {
                    Toast.makeText(this, "Thanh toán thất bại hoặc bị hủy.", Toast.LENGTH_SHORT).show();
                    if (tvStatus != null) {
                        tvStatus.setText("Trạng thái: Chưa thanh toán");
                    }
                    btnPayTicket.setEnabled(true);
                    btnPayTicket.setText("Thanh Toán");
                }
            }
        }
    }

    private void updateUIForSuccess() {
        btnPayTicket.setEnabled(false);
        btnPayTicket.setText("Đã Thanh Toán");
        if (tvStatus != null) {
            tvStatus.setText("Trạng thái: Đã thanh toán");
        }

        // Redirect to MainActivity with navigation_tickets fragment
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("SELECTED_FRAGMENT", R.id.navigation_tickets); // Pass the fragment ID
        startActivity(intent);
        finish(); // Close TicketActivity
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof AutoCompleteTextView) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private void loadBanksFromApi() {
        String url = "https://api.vietqr.io/v2/banks";
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray dataArray = response.getJSONArray("data");
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject bankObj = dataArray.getJSONObject(i);
                            String name = bankObj.getString("name");
                            String code = bankObj.getString("code");
                            String shortName = bankObj.getString("shortName");
                            String logo = bankObj.getString("logo");

                            bankList.add(new Bank(name, code, shortName, logo));
                        }

                        bankAdapter = new BankAdapter(this, bankList);
                        autoCompleteBanks.setAdapter(bankAdapter);
                        autoCompleteBanks.setThreshold(1);
                        autoCompleteBanks.setOnClickListener(v -> autoCompleteBanks.showDropDown());

                        autoCompleteBanks.setOnItemClickListener((parent, view, position, id) -> {
                            Bank selectedBank = (Bank) parent.getItemAtPosition(position);
                            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                            if (imm != null) {
                                imm.hideSoftInputFromWindow(autoCompleteBanks.getWindowToken(), 0);
                            }
                            autoCompleteBanks.clearFocus();
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> error.printStackTrace()
        );

        queue.add(request);
    }
}