package com.group8.busbookingapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.group8.busbookingapp.R;
import com.group8.busbookingapp.dto.ApiResponse;
import com.group8.busbookingapp.dto.Seat;
import com.group8.busbookingapp.dto.TripDetailsResponse;
import com.group8.busbookingapp.network.ApiClient;
import com.group8.busbookingapp.network.ApiService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SeatSelectionActivity extends AppCompatActivity {
    private static final String TAG = "SeatSelectionActivity";
    private static final int MAX_SEATS = 5; // Maximum number of seats that can be selected
    private static final int REQUEST_SELECT_STOP_POINTS = 1;
    private Toolbar toolbar;
    private TextView tvRouteName, tvDateTime, tvBusType;
    private TextView tvSelectedSeats, tvTotal;
    private Button btnContinue;
    private ImageButton btnBack;
    private List<View> seatViews = new ArrayList<>();
    private List<String> selectedSeats = new ArrayList<>();
    private double pricePerSeat;
    private String tripId;
    private View loadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_seat_selection);

        // Initialize loading view
        loadingView = getLayoutInflater().inflate(R.layout.layout_loading, null);
        loadingView.setVisibility(View.GONE);
        ((ViewGroup) findViewById(android.R.id.content)).addView(loadingView);

        // Get tripId from Intent
        tripId = getIntent().getStringExtra("tripId");
        if (tripId != null) {
            showLoading();
            fetchTripDetails(tripId);
        } else {
            Toast.makeText(this, "Trip ID không đúng", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void showLoading() {
        loadingView.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        loadingView.setVisibility(View.GONE);
    }

    private void fetchTripDetails(String tripId) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ApiResponse<TripDetailsResponse>> call = apiService.getTripDetails(tripId);
        call.enqueue(new Callback<ApiResponse<TripDetailsResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<TripDetailsResponse>> call, Response<ApiResponse<TripDetailsResponse>> response) {
                hideLoading();
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "API Response: " + response.body().toString());
                    if (response.body().getStatus().equals("success")) {
                        TripDetailsResponse trip = response.body().getData();
                        setupLayout(trip);
                        updateUI(trip);
                    } else {
                        Log.e(TAG, "API Error: " + response.body().getMessage());
                        Toast.makeText(SeatSelectionActivity.this, "Không thể tải thông tin chuyến: " + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    Log.e(TAG, "HTTP Error: " + response.code() + ", Message: " + response.message());
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        Log.e(TAG, "Error Body: " + errorBody);
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading error body: " + e.getMessage());
                    }
                    Toast.makeText(SeatSelectionActivity.this, "Không thể tải thông tin chuyến", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<TripDetailsResponse>> call, Throwable t) {
                hideLoading();
                Log.e(TAG, "Network Error: " + t.getMessage(), t);
                Toast.makeText(SeatSelectionActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void setupLayout(TripDetailsResponse trip) {
        int capacity = trip.getCapacity();
        if (capacity == 34) {
            setContentView(R.layout.activity_seat_selection_34);
        } else if (capacity == 44) {
            setContentView(R.layout.activity_seat_selection_44);
        } else {
            Log.e(TAG, "Unsupported bus capacity: " + capacity);
            Toast.makeText(this, "Dung lượng xe không được hỗ trợ: " + capacity, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize views after setting content view
        toolbar = findViewById(R.id.toolbar);
        tvRouteName = findViewById(R.id.tvRouteName);
        tvDateTime = findViewById(R.id.tvDateTime);
        tvBusType = findViewById(R.id.tvBusType);
        tvSelectedSeats = findViewById(R.id.tvSelectedSeats);
        tvTotal = findViewById(R.id.tvTotal);
        btnContinue = findViewById(R.id.btnContinue);
        btnBack = findViewById(R.id.btnBack);

        // Setup toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Back button click listener
        btnBack.setOnClickListener(v -> finish());

        // Continue button click listener
        btnContinue.setOnClickListener(v -> {
            if (selectedSeats.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn ít nhất một ghế", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Ghế đã chọn: " + String.join(", ", selectedSeats), Toast.LENGTH_LONG).show();
                onSeatsSelected();
            }
        });
    }

    private void updateUI(TripDetailsResponse trip) {
        // Update trip info
        tvRouteName.setText(trip.getStartPointCity() + " - " + trip.getEndPointCity());
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.getDefault());
            Date date = inputFormat.parse(trip.getDepartureTime());
            tvDateTime.setText(outputFormat.format(date));
        } catch (Exception e) {
            Log.e(TAG, "Error parsing departureTime: " + trip.getDepartureTime(), e);
            tvDateTime.setText(trip.getDepartureTime());
        }
        tvBusType.setText("Giường nằm " + trip.getCapacity() + " chỗ");
        pricePerSeat = trip.getPrice();

        // Initialize seat views
        initializeSeatViews(trip.getSeats(), trip.getCapacity());
    }

    private void initializeSeatViews(List<Seat> seats, int capacity) {
        // Define seat IDs for both layouts
        int[] lowerFloorSeatIds_34 = {
                R.id.seat_A1, R.id.seat_A2, R.id.seat_A3, R.id.seat_A4, R.id.seat_A5,
                R.id.seat_A6, R.id.seat_A7, R.id.seat_A8, R.id.seat_A9, R.id.seat_A10,
                R.id.seat_A11, R.id.seat_A12, R.id.seat_A13, R.id.seat_A14, R.id.seat_A15,
                R.id.seat_A16, R.id.seat_A17
        };
        int[] upperFloorSeatIds_34 = {
                R.id.seat_B1, R.id.seat_B2, R.id.seat_B3, R.id.seat_B4, R.id.seat_B5,
                R.id.seat_B6, R.id.seat_B7, R.id.seat_B8, R.id.seat_B9, R.id.seat_B10,
                R.id.seat_B11, R.id.seat_B12, R.id.seat_B13, R.id.seat_B14, R.id.seat_B15,
                R.id.seat_B16, R.id.seat_B17
        };
        int[] lowerFloorSeatIds_44 = {
                R.id.seat_A1, R.id.seat_A2, R.id.seat_A3, R.id.seat_A4, R.id.seat_A5,
                R.id.seat_A6, R.id.seat_A7, R.id.seat_A8, R.id.seat_A9, R.id.seat_A10,
                R.id.seat_A11, R.id.seat_A12, R.id.seat_A13, R.id.seat_A14, R.id.seat_A15,
                R.id.seat_A16, R.id.seat_A17, R.id.seat_A18, R.id.seat_A19, R.id.seat_A20,
                R.id.seat_A21, R.id.seat_A22
        };
        int[] upperFloorSeatIds_44 = {
                R.id.seat_B1, R.id.seat_B2, R.id.seat_B3, R.id.seat_B4, R.id.seat_B5,
                R.id.seat_B6, R.id.seat_B7, R.id.seat_B8, R.id.seat_B9, R.id.seat_B10,
                R.id.seat_B11, R.id.seat_B12, R.id.seat_B13, R.id.seat_B14, R.id.seat_B15,
                R.id.seat_B16, R.id.seat_B17, R.id.seat_B18, R.id.seat_B19, R.id.seat_B20,
                R.id.seat_B21, R.id.seat_B22
        };

        int[] lowerFloorSeatIds = (capacity == 34) ? lowerFloorSeatIds_34 : lowerFloorSeatIds_44;
        int[] upperFloorSeatIds = (capacity == 34) ? upperFloorSeatIds_34 : upperFloorSeatIds_44;

        // Process seats
        for (Seat seat : seats) {
            int viewId = -1;
            if (seat.getFloor() == 1) {
                // Lower floor
                int index = Integer.parseInt(seat.getSeatNumber().substring(1)) - 1;
                if (index < lowerFloorSeatIds.length) {
                    viewId = lowerFloorSeatIds[index];
                }
            } else if (seat.getFloor() == 2) {
                // Upper floor
                int index = Integer.parseInt(seat.getSeatNumber().substring(1)) - 1;
                if (index < upperFloorSeatIds.length) {
                    viewId = upperFloorSeatIds[index];
                }
            }

            if (viewId != -1) {
                View seatView = findViewById(viewId);
                if (seatView != null) {
                    // Cast to CardView
                    if (seatView instanceof CardView) {
                        CardView cardView = (CardView) seatView;
                        // Find RelativeLayout inside CardView
                        View relativeLayout = cardView.getChildAt(0); // Assumes RelativeLayout is the first child
                        if (relativeLayout instanceof RelativeLayout) {
                            RelativeLayout seatLayout = (RelativeLayout) relativeLayout;
                            TextView seatNumber = seatLayout.findViewById(R.id.tvSeatNumber);
                            if (seatNumber != null) {
                                seatNumber.setText(seat.getSeatNumber());
                                seatViews.add(seatView);

                                if (seat.getStatus().equals("AVAILABLE")) {
                                    seatLayout.setBackgroundResource(R.drawable.bg_status_completed);
                                    seatView.setOnClickListener(v -> toggleSeatSelection(seat, seatView, seatLayout));
                                } else {
                                    seatLayout.setBackgroundResource(R.drawable.bg_status_unavailable);
                                    seatView.setEnabled(false);
                                }
                            } else {
                                Log.e(TAG, "tvSeatNumber not found in seat view: " + seat.getSeatNumber());
                            }
                        } else {
                            Log.e(TAG, "Expected RelativeLayout inside CardView for seat: " + seat.getSeatNumber());
                        }
                    } else {
                        Log.e(TAG, "Seat view is not a CardView for seat: " + seat.getSeatNumber() + ", class: " + seatView.getClass().getName());
                    }
                } else {
                    Log.w(TAG, "Seat view not found for ID: " + viewId);
                }
            } else {
                Log.w(TAG, "Invalid seat index for seat: " + seat.getSeatNumber());
            }
        }

        updateSelectedSeatsUI();
    }

    private void toggleSeatSelection(Seat seat, View seatView, RelativeLayout seatLayout) {
        String seatNumber = seat.getSeatNumber();
        if (selectedSeats.contains(seatNumber)) {
            selectedSeats.remove(seatNumber);
            seatLayout.setBackgroundResource(R.drawable.bg_status_completed);
        } else {
            if (selectedSeats.size() < MAX_SEATS) {
                selectedSeats.add(seatNumber);
                seatLayout.setBackgroundResource(R.drawable.bg_status_pending);
            } else {
                Toast.makeText(this, "Bạn chỉ có thể chọn tối đa 5 ghế", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        updateSelectedSeatsUI();
    }

    private void updateSelectedSeatsUI() {
        if (selectedSeats.isEmpty()) {
            tvSelectedSeats.setText("Không có ghế nào được chọn");
            tvTotal.setText("0 VND");
        } else {
            tvSelectedSeats.setText(String.join(", ", selectedSeats));
            double total = selectedSeats.size() * pricePerSeat;
            tvTotal.setText(String.format(Locale.getDefault(), "%,.0f VND", total));
        }
    }

    private void onSeatsSelected() {
        // Start SelectStopPointsActivity
        Intent intent = new Intent(this, SelectStopPointsActivity.class);
        intent.putExtra("TRIP_ID", tripId);
        intent.putExtra("ROUTE_NAME", tvRouteName.getText().toString());
        intent.putExtra("DATE_TIME", tvDateTime.getText().toString());
        intent.putExtra("BUS_TYPE", tvBusType.getText().toString());
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SELECT_STOP_POINTS && resultCode == RESULT_OK && data != null) {
            String pickupPointId = data.getStringExtra("PICKUP_POINT_ID");
            String dropoffPointId = data.getStringExtra("DROPOFF_POINT_ID");
            
            // Continue with booking process using selected seats and stop points
            // You can start the next activity or process the booking here
        }
    }
}