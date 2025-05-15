package com.group8.busbookingapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.tabs.TabLayoutMediator;
import com.group8.busbookingapp.R;
import com.group8.busbookingapp.adapter.BookingPagerAdapter;
import com.group8.busbookingapp.databinding.ActivityTickethistoryBinding;
import com.group8.busbookingapp.dto.ApiResponse;
import com.group8.busbookingapp.model.Booking;
import com.group8.busbookingapp.network.ApiClient;
import com.group8.busbookingapp.network.ApiService;
import com.group8.busbookingapp.viewmodel.TicketHistoryViewModel;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TicketHistoryActivity extends AppCompatActivity {
    private static final String TAG = "TicketHistoryActivity";
    private ActivityTickethistoryBinding binding;
    private TicketHistoryViewModel viewModel;
    private BookingPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_tickethistory);
        viewModel = new ViewModelProvider(this).get(TicketHistoryViewModel.class);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        setupViewPager();
        setupButtons();
        setupObservers();
        fetchBookingHistory();
    }

    private void setupViewPager() {
//        pagerAdapter = new BookingPagerAdapter(this);
        binding.viewPager.setAdapter(pagerAdapter);
        binding.viewPager.setOffscreenPageLimit(pagerAdapter.getItemCount()); // Ensure all fragments are created
        new TabLayoutMediator(binding.tabLayout, binding.viewPager,
                (tab, position) -> tab.setText(pagerAdapter.getTabTitle(position))
        ).attach();
        Log.d(TAG, "ViewPager set up with " + pagerAdapter.getItemCount() + " tabs");
    }

    private void setupButtons() {
        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnBookTicket.setOnClickListener(v -> {
            // TODO: Navigate to booking activity
            Toast.makeText(this, R.string.navigate_to_booking, Toast.LENGTH_SHORT).show();
        });
    }

    private void setupObservers() {
        // Bind empty state visibility
        viewModel.isEmpty().observe(this, isEmpty -> {
            Log.d(TAG, "Empty state: " + isEmpty);
            binding.emptyStateLayout.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        });
    }

    private void fetchBookingHistory() {
        viewModel.setLoading(true);
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String jwtToken = sharedPreferences.getString("token", null);

        if (jwtToken == null) {
            Toast.makeText(this, R.string.please_login, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class)); // Assumes LoginActivity exists
            finish();
            return;
        }
//        String jwtToken = "eyJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJkdDI1NiIsImlhdCI6MTc0NjI4NTU1NCwic3ViIjoiZGlhdGllbnNpbmhAZ21haWwuY29tIiwicm9sZSI6IlVTRVIiLCJpZCI6IjY4MGIyOWQ0YjY0MWE4Mjk2ODExMzc2YSIsImV4cCI6OTIyMzM3MjAzNjg1NDc3NX0.Y90T0XlbNqO8PheqTHkZFN2y2NE5umSkTymXq-Iv2FHI2-hMuGDLTavTtIev4ZoZ8akzhzvK4uFPuoHD5NK53w";

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ApiResponse<List<Booking>>> call = apiService.getBookingHistory("Bearer " + jwtToken);
        call.enqueue(new Callback<ApiResponse<List<Booking>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Booking>>> call, Response<ApiResponse<List<Booking>>> response) {
                viewModel.setLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Booking>> apiResponse = response.body();
                    if ("success".equals(apiResponse.getStatus())) {
                        List<Booking> bookings = apiResponse.getData();
                        Log.d(TAG, "Fetched bookings: " + (bookings != null ? bookings.size() : 0));
                        viewModel.setBookings(bookings);
                        viewModel.setEmpty(bookings == null || bookings.isEmpty());
                    } else {
                        Log.e(TAG, "API error: " + apiResponse.getMessage());
                        viewModel.setBookings(null);
                        viewModel.setEmpty(true);
                        Toast.makeText(TicketHistoryActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "Response failed: " + response.code());
                    viewModel.setBookings(null);
                    viewModel.setEmpty(true);
                    Toast.makeText(TicketHistoryActivity.this, "Lỗi server: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Booking>>> call, Throwable t) {
                viewModel.setLoading(false);
                viewModel.setBookings(null);
                viewModel.setEmpty(true);
                String errorMessage = t instanceof java.io.IOException ?
                        getString(R.string.network_error) : "Lỗi: " + t.getMessage();
                Log.e(TAG, "Network failure: " + errorMessage);
                Toast.makeText(TicketHistoryActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void refreshBookings() {
        fetchBookingHistory();
    }
}