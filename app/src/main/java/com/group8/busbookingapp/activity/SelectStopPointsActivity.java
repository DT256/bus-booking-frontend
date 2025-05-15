package com.group8.busbookingapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.group8.busbookingapp.R;
import com.group8.busbookingapp.adapter.StopPointAdapter;
import com.group8.busbookingapp.dto.ApiResponse;
import com.group8.busbookingapp.dto.TripDetailsResponse;
import com.group8.busbookingapp.model.StopPoint;
import com.group8.busbookingapp.network.ApiClient;
import com.group8.busbookingapp.network.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectStopPointsActivity extends AppCompatActivity {
    private static final String TAG = "SelectStopPointsActivity";
    private RecyclerView rvPickupPoints, rvDropoffPoints;
    private TextView tvRouteName, tvDateTime;
    private Button btnContinue;
    private ImageButton btnBack;
    private StopPointAdapter pickupAdapter, dropoffAdapter;
    private String tripId;
    private StopPoint selectedPickupPoint, selectedDropoffPoint;
    private View loadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_stop_points);

        loadingView = getLayoutInflater().inflate(R.layout.layout_loading, null);
        loadingView.setVisibility(View.GONE);
        ((ViewGroup) findViewById(android.R.id.content)).addView(loadingView);

        tripId = getIntent().getStringExtra("TRIP_ID");
        String routeName = getIntent().getStringExtra("ROUTE_NAME");
        String dateTime = getIntent().getStringExtra("DATE_TIME");
        if (tripId == null) {
            Toast.makeText(this, "Không tìm thấy thông tin chuyến đi", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        rvPickupPoints = findViewById(R.id.rvPickupPoints);
        rvDropoffPoints = findViewById(R.id.rvDropoffPoints);
        btnContinue = findViewById(R.id.btnContinue);
        btnBack = findViewById(R.id.btn_back);
        tvRouteName = findViewById(R.id.tvRouteName);
        tvDateTime = findViewById(R.id.tvDateTime);

        tvRouteName.setText(routeName);
        tvDateTime.setText(dateTime);

        rvPickupPoints.setLayoutManager(new LinearLayoutManager(this));
        rvDropoffPoints.setLayoutManager(new LinearLayoutManager(this));

        pickupAdapter = new StopPointAdapter(new ArrayList<>(), (stopPoint, position) -> {
            selectedPickupPoint = stopPoint;
            updateContinueButton();
        });
        dropoffAdapter = new StopPointAdapter(new ArrayList<>(), (stopPoint, position) -> {
            selectedDropoffPoint = stopPoint;
            updateContinueButton();
        });

        rvPickupPoints.setAdapter(pickupAdapter);
        rvDropoffPoints.setAdapter(dropoffAdapter);

        loadStopPoints();

        btnContinue.setOnClickListener(v -> {
            if (selectedPickupPoint != null && selectedDropoffPoint != null) {
                Intent intent = new Intent(SelectStopPointsActivity.this, PassengerInfoActivity.class);
                intent.putExtra("TRIP_ID", tripId);
                intent.putExtra("ROUTE_NAME", tvRouteName.getText().toString());
                intent.putExtra("DATE_TIME", tvDateTime.getText().toString());
                intent.putExtra("ARRIVAL_TIME", getIntent().getStringExtra("ARRIVAL_TIME"));
                Log.d(TAG, "onCreate: " + getIntent().getStringExtra("ARRIVAL_TIME"));
                intent.putExtra("PICKUP_POINT_ID", selectedPickupPoint.getId());
                intent.putExtra("DROPOFF_POINT_ID", selectedDropoffPoint.getId());
                intent.putExtra("PICKUP_ADDRESS", selectedPickupPoint.getName());
                intent.putExtra("DROPOFF_ADDRESS", selectedDropoffPoint.getName());
                intent.putStringArrayListExtra("SELECTED_SEAT_IDS", getIntent().getStringArrayListExtra("SELECTED_SEAT_IDS"));
                intent.putStringArrayListExtra("SELECTED_SEAT_NUMBER", getIntent().getStringArrayListExtra("SELECTED_SEAT_NUMBER"));
                intent.putExtra("TOTAL_PRICE", getIntent().getDoubleExtra("TOTAL_PRICE", 0.0));
                startActivity(intent);
            } else {
                Toast.makeText(this, "Vui lòng chọn điểm dừng", Toast.LENGTH_SHORT).show();
            }
        });

        btnBack.setOnClickListener(v -> finish());
    }

    private void loadStopPoints() {
        showLoading();
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ApiResponse<TripDetailsResponse>> call = apiService.getTripDetails(tripId);
        call.enqueue(new Callback<ApiResponse<TripDetailsResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<TripDetailsResponse>> call, Response<ApiResponse<TripDetailsResponse>> response) {
                hideLoading();
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    TripDetailsResponse tripDetails = response.body().getData();
                    List<StopPoint> allStopPoints = tripDetails.getStopPoints();

                    List<StopPoint> pickupPoints = new ArrayList<>();
                    List<StopPoint> dropoffPoints = new ArrayList<>();

                    for (StopPoint point : allStopPoints) {
                        if ("PICKUP".equals(point.getType())) {
                            pickupPoints.add(point);
                        } else if ("DROPOFF".equals(point.getType())) {
                            dropoffPoints.add(point);
                        }
                    }

                    pickupAdapter.updateStopPoints(pickupPoints);
                    dropoffAdapter.updateStopPoints(dropoffPoints);
                } else {
                    Toast.makeText(SelectStopPointsActivity.this, "Không thể tải thông tin điểm dừng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<TripDetailsResponse>> call, Throwable t) {
                hideLoading();
                Toast.makeText(SelectStopPointsActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateContinueButton() {
        btnContinue.setEnabled(selectedPickupPoint != null && selectedDropoffPoint != null);
    }

    private void showLoading() {
        loadingView.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        loadingView.setVisibility(View.GONE);
    }
}