package com.group8.busbookingapp.activity;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.group8.busbookingapp.R;
import com.group8.busbookingapp.adapter.TicketHistoryAdapter;
import com.group8.busbookingapp.dto.ApiResponse;
import com.group8.busbookingapp.model.Booking;
import com.group8.busbookingapp.network.ApiClient;
import com.group8.busbookingapp.network.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class TicketHistoryActivity extends AppCompatActivity {
    private RecyclerView recyclerViewTickets;
    private LinearLayout emptyStateLayout;
    private ProgressBar progressBar;
    private TicketHistoryAdapter adapter;
    private List<Booking> bookingList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tickethistory);

        // Initialize views
        recyclerViewTickets = findViewById(R.id.recyclerViewTickets);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        progressBar = findViewById(R.id.progressBar);
        ImageButton btnBack = findViewById(R.id.btnBack);
        TextView tvTitle = findViewById(R.id.tvTitle);
        Button btnBookTicket = findViewById(R.id.btnBookTicket);

        // Set up RecyclerView
        recyclerViewTickets.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TicketHistoryAdapter(this, bookingList);
        recyclerViewTickets.setAdapter(adapter);

        // Set up back button
        btnBack.setOnClickListener(v -> finish());

        // Set up book ticket button
        btnBookTicket.setOnClickListener(v -> {
            // Implement book ticket logic
        });

        // Fetch booking history
        fetchBookingHistory();
    }

    private void fetchBookingHistory() {
        progressBar.setVisibility(View.VISIBLE);
        emptyStateLayout.setVisibility(View.GONE);
        recyclerViewTickets.setVisibility(View.GONE);

        // Giả sử bạn lưu token sau khi login vào SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String jwtToken = sharedPreferences.getString("token", null);

        if (jwtToken == null) {
            // Token không có => chưa đăng nhập
            progressBar.setVisibility(View.GONE);
            emptyStateLayout.setVisibility(View.VISIBLE);
            return;
        }

        //String jwtToken = "eyJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJkdDI1NiIsImlhdCI6MTc0NTU2MjE1OSwic3ViIjoiZGlhdGllbnNpbmhAZ21haWwuY29tIiwicm9sZSI6IlVTRVIiLCJpZCI6IjY4MGIyOWQ0YjY0MWE4Mjk2ODExMzc2YSIsImV4cCI6OTIyMzM3MjAzNjg1NDc3NX0.nzfO3v0PCpxaL6ci7RtXPDA78rXmBLtZcKaxttAj50OIJH0NT5Mu1Si1IawV5rmWnVyTTyl6Fwk_tEoej7Ojog";

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ApiResponse<List<Booking>>> call = apiService.getBookingHistory("Bearer " + jwtToken);
        call.enqueue(new Callback<ApiResponse<List<Booking>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Booking>>> call, Response<ApiResponse<List<Booking>>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().getStatus().equals("success")) {
                    bookingList = response.body().getData();
                    if (bookingList != null && !bookingList.isEmpty()) {
                        adapter.updateData(bookingList);
                        recyclerViewTickets.setVisibility(View.VISIBLE);
                        emptyStateLayout.setVisibility(View.GONE);
                    } else {
                        recyclerViewTickets.setVisibility(View.GONE);
                        emptyStateLayout.setVisibility(View.VISIBLE);
                    }
                } else {
                    recyclerViewTickets.setVisibility(View.GONE);
                    emptyStateLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Booking>>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                recyclerViewTickets.setVisibility(View.GONE);
                emptyStateLayout.setVisibility(View.VISIBLE);
            }
        });
    }

}
