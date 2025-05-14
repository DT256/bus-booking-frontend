package com.group8.busbookingapp.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.group8.busbookingapp.R;
import com.group8.busbookingapp.adapter.BookingPagerAdapter;
import com.group8.busbookingapp.dto.ApiResponse;
import com.group8.busbookingapp.model.Booking;
import com.group8.busbookingapp.network.ApiClient;
import com.group8.busbookingapp.network.ApiService;
import com.group8.busbookingapp.viewmodel.TicketHistoryViewModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TicketHistoryFragment extends Fragment {
    private static final String TAG = "TicketHistoryFragment";
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private Button btnBookTicket;
    private LinearLayout emptyStateLayout;
    private TicketHistoryViewModel viewModel;
    private BookingPagerAdapter pagerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ticket_history, container, false);

        // Initialize views
        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);
        btnBookTicket = view.findViewById(R.id.btnBookTicket);
        emptyStateLayout = view.findViewById(R.id.emptyStateLayout);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(TicketHistoryViewModel.class);

        setupViewPager();
        setupButtons();
        setupObservers();
        fetchBookingHistory();

        return view;
    }

    private void setupViewPager() {
        pagerAdapter = new BookingPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(pagerAdapter.getItemCount());
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(pagerAdapter.getTabTitle(position))
        ).attach();
        Log.d(TAG, "ViewPager set up with " + pagerAdapter.getItemCount() + " tabs");
    }

    private void setupButtons() {
        btnBookTicket.setOnClickListener(v -> {
            // TODO: Navigate to booking fragment
            Toast.makeText(requireContext(), R.string.navigate_to_booking, Toast.LENGTH_SHORT).show();
        });
    }

    private void setupObservers() {
        viewModel.isEmpty().observe(getViewLifecycleOwner(), isEmpty -> {
            Log.d(TAG, "Empty state: " + isEmpty);
            emptyStateLayout.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        });
    }

    private void fetchBookingHistory() {
        viewModel.setLoading(true);
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyAppPrefs", 0);
        String jwtToken = "eyJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJkdDI1NiIsImlhdCI6MTc0NjI4NTU1NCwic3ViIjoiZGlhdGllbnNpbmhAZ21haWwuY29tIiwicm9sZSI6IlVTRVIiLCJpZCI6IjY4MGIyOWQ0YjY0MWE4Mjk2ODExMzc2YSIsImV4cCI6OTIyMzM3MjAzNjg1NDc3NX0.Y90T0XlbNqO8PheqTHkZFN2y2NE5umSkTymXq-Iv2FHI2-hMuGDLTavTtIev4ZoZ8akzhzvK4uFPuoHD5NK53w";

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
                        Toast.makeText(requireContext(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "Response failed: " + response.code());
                    viewModel.setBookings(null);
                    viewModel.setEmpty(true);
                    Toast.makeText(requireContext(), "Lỗi server: " + response.code(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void refreshBookings() {
        fetchBookingHistory();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        tabLayout = null;
        viewPager = null;
        btnBookTicket = null;
        emptyStateLayout = null;
    }
}