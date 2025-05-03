package com.group8.busbookingapp.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.group8.busbookingapp.R;
import com.group8.busbookingapp.adapter.TripAdapter;
import com.group8.busbookingapp.model.Trip;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class SearchResultActivity extends AppCompatActivity {
    private RecyclerView rvBusList;
    private TripAdapter tripAdapter;
    private ArrayList<Trip> tripList;
    private Chip btnChipSortPrice, btnChipSortTime;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        rvBusList = findViewById(R.id.rvBusList);
        tripList = (ArrayList<Trip>) getIntent().getSerializableExtra("trip_list");
        String departure = getIntent().getStringExtra("departure");
        String arrival = getIntent().getStringExtra("arrival");
        String dateInput = getIntent().getStringExtra("dateInput");

        // Ánh xạ các TextView trong Trip Summary Card
        TextView tvFromLocation = findViewById(R.id.tvFromLocation);
        TextView tvToLocation = findViewById(R.id.tvToLocation);
        TextView tvDate = findViewById(R.id.tvDate);
        TextView tvResultCount = findViewById(R.id.tvResultCount);

        tvFromLocation.setText(departure);
        tvToLocation.setText(arrival);
        tvDate.setText(dateInput);
        tvResultCount.setText(tripList.size() + " chuyến xe tìm thấy");

        tripAdapter = new TripAdapter(this, tripList);
        rvBusList.setLayoutManager(new LinearLayoutManager(this));
        rvBusList.setAdapter(tripAdapter);

        // Sắp xếp theo giá
        btnChipSortPrice = findViewById(R.id.chipSortPrice);
        btnChipSortTime = findViewById(R.id.chipSortTime);

        btnChipSortPrice.setOnCheckedChangeListener((chip, isChecked) -> {
            if (isChecked) {
                Collections.sort(tripList, Comparator.comparingDouble(Trip::getPrice));
                tripAdapter.notifyDataSetChanged();
                btnChipSortTime.setChecked(false);
            }
        });

        // Sắp xếp theo thời gian khởi hành
        btnChipSortTime.setOnCheckedChangeListener((chip, isChecked) -> {
            if (isChecked) {
                Collections.sort(tripList, Comparator.comparing(trip -> parseDate(trip.getDepartureTime())));
                tripAdapter.notifyDataSetChanged();
                btnChipSortPrice.setChecked(false);
            }
        });

        // Xử lý nút quay lại
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }

    // Chuyển đổi chuỗi thời gian thành Date để sắp xếp
    private Date parseDate(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date(); // fallback nếu lỗi
        }
    }
}

