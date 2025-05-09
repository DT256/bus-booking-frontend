package com.group8.busbookingapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
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
    private RecyclerView rvPickupPoints;
    private RecyclerView rvDropoffPoints;
    private Button btnContinue;
    private StopPointAdapter pickupAdapter;
    private StopPointAdapter dropoffAdapter;
    private String tripId;
    private StopPoint selectedPickupPoint;
    private StopPoint selectedDropoffPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_stop_points);

        // Get trip ID from intent
        tripId = getIntent().getStringExtra("TRIP_ID");
        if (tripId == null) {
            Toast.makeText(this, "Không tìm thấy thông tin chuyến đi", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize views
        rvPickupPoints = findViewById(R.id.rvPickupPoints);
        rvDropoffPoints = findViewById(R.id.rvDropoffPoints);
        btnContinue = findViewById(R.id.btnContinue);

        // Setup RecyclerViews
        rvPickupPoints.setLayoutManager(new LinearLayoutManager(this));
        rvDropoffPoints.setLayoutManager(new LinearLayoutManager(this));

        // Initialize adapters
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

        // Load stop points
        loadStopPoints();

        // Setup continue button
        btnContinue.setOnClickListener(v -> {
            if (selectedPickupPoint != null && selectedDropoffPoint != null) {
                // Pass selected points back to previous activity
//                Intent resultIntent = new Intent();
//                resultIntent.putExtra("PICKUP_POINT_ID", selectedPickupPoint.getId());
//                resultIntent.putExtra("DROPOFF_POINT_ID", selectedDropoffPoint.getId());
//                setResult(RESULT_OK, resultIntent);
//                finish();
                Toast.makeText(this, selectedPickupPoint.getAddress(), Toast.LENGTH_SHORT).show();
                Toast.makeText(this, selectedDropoffPoint.getAddress(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Vui lòng chọn điểm dừng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadStopPoints() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ApiResponse<TripDetailsResponse>> call = apiService.getTripDetails(tripId);
        call.enqueue(new Callback<ApiResponse<TripDetailsResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<TripDetailsResponse>> call, Response<ApiResponse<TripDetailsResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    TripDetailsResponse tripDetails = response.body().getData();
                    List<StopPoint> allStopPoints = tripDetails.getStopPoints();
                    
                    // Filter pickup and dropoff points
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
                    Toast.makeText(SelectStopPointsActivity.this, 
                        "Không thể tải thông tin điểm dừng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<TripDetailsResponse>> call, Throwable t) {
                Toast.makeText(SelectStopPointsActivity.this, 
                    "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateContinueButton() {
        btnContinue.setEnabled(selectedPickupPoint != null && selectedDropoffPoint != null);
    }
} 