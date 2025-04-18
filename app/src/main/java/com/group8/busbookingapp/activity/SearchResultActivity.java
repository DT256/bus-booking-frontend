package com.group8.busbookingapp.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.group8.busbookingapp.R;
import com.group8.busbookingapp.adapter.TripAdapter;
import com.group8.busbookingapp.model.Trip;

import java.util.ArrayList;

public class SearchResultActivity extends AppCompatActivity {

    private RecyclerView rvBusList;
    private TripAdapter tripAdapter;
    private ArrayList<Trip> tripList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        rvBusList = findViewById(R.id.rvBusList);
        tripList = (ArrayList<Trip>) getIntent().getSerializableExtra("trip_list");

        tripAdapter = new TripAdapter(tripList);
        rvBusList.setLayoutManager(new LinearLayoutManager(this));
        rvBusList.setAdapter(tripAdapter);
    }
}

