package com.group8.busbookingapp.activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.group8.busbookingapp.BusTrip;
import com.group8.busbookingapp.BusTripAdapter;
import com.group8.busbookingapp.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BusTripAdapter adapter;
    private List<BusTrip> busTripList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_result);

        recyclerView = findViewById(R.id.rvBusList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Tạo danh sách dữ liệu mẫu
        busTripList = new ArrayList<>();
        busTripList.add(new BusTrip(
                "Phương Trang", "Ghế ngồi", "4.5", "06:00", "12:00", "6h",
                "Sài Gòn", "Nha Trang",
                Arrays.asList("WiFi", "Máy lạnh", "Nước suối"),
                "250.000đ", "20 chỗ trống"
        ));

        busTripList.add(new BusTrip(
                "Mai Linh", "Giường nằm", "4.7", "08:00", "14:00", "6h",
                "Sài Gòn", "Đà Lạt",
                Arrays.asList("Máy lạnh", "Sạc điện thoại", "Mền"),
                "300.000đ", "15 chỗ trống"
        ));

        busTripList.add(new BusTrip(
                "Hoàng Long", "Limousine", "4.9", "09:30", "15:30", "6h",
                "Hà Nội", "Hải Phòng",
                Arrays.asList("WiFi", "TV", "Snack", "Massage"),
                "400.000đ", "10 chỗ trống"
        ));

        adapter = new BusTripAdapter(busTripList);
        recyclerView.setAdapter(adapter);
    }
}