package com.group8.busbookingapp.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.group8.busbookingapp.network.ApiClient;
import com.group8.busbookingapp.network.ApiService;
import com.group8.busbookingapp.R;
import com.group8.busbookingapp.dto.ApiResponse;
import com.group8.busbookingapp.model.Trip;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {

    private AutoCompleteTextView spinnerDepartureLocation, spinnerArrivalLocation;
    private TextInputEditText etDepartureDate;
    private Button btnSearch;
    private Calendar selectedCalendar;
    private final String BASE_URL = "http://localhost:8080/api/trips/search"; // chỉnh lại URL đúng

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        spinnerDepartureLocation = findViewById(R.id.spinnerDepartureLocation);
        spinnerArrivalLocation = findViewById(R.id.spinnerArrivalLocation);
        etDepartureDate = findViewById(R.id.etDepartureDate);
        btnSearch = findViewById(R.id.btnSearch);

        setupSpinners();
        setupDatePicker();
        setupSearchButton();
    }

    private void setupSpinners() {
        String[] locations = {"Hà Nội", "Hồ Chí Minh", "Đà Nẵng", "Cần Thơ", "Nha Trang", "Phú Yên"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, locations);
        spinnerDepartureLocation.setAdapter(adapter);
        spinnerArrivalLocation.setAdapter(adapter);
    }

    private void setupDatePicker() {
        selectedCalendar = Calendar.getInstance();

        etDepartureDate.setOnClickListener(v -> {
            int year = selectedCalendar.get(Calendar.YEAR);
            int month = selectedCalendar.get(Calendar.MONTH);
            int day = selectedCalendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    SearchActivity.this,
                    (view, y, m, d) -> {
                        selectedCalendar.set(y, m, d, 9, 30, 52); // set giờ mặc định 09:30:52
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        etDepartureDate.setText(sdf.format(selectedCalendar.getTime()));
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });
    }

    private void setupSearchButton() {
        btnSearch.setOnClickListener(v -> {
            String departure = spinnerDepartureLocation.getText().toString();
            String arrival = spinnerArrivalLocation.getText().toString();
            String dateInput = etDepartureDate.getText().toString(); // dd/MM/yyyy

            if (departure.isEmpty() || arrival.isEmpty() || dateInput.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            // Chuyển format ngày từ dd/MM/yyyy sang yyyy-MM-dd HH:mm:ss
            String formattedDate = convertDateFormat(dateInput); // ví dụ: 2025-03-15 00:00:00

            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            Call<ApiResponse<List<Trip>>> call = apiService.searchTrips(departure, arrival, formattedDate);

            call.enqueue(new Callback<ApiResponse<List<Trip>>>() {
                @Override
                public void onResponse(Call<ApiResponse<List<Trip>>> call, Response<ApiResponse<List<Trip>>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Trip> trips = response.body().getData();

                        Intent intent = new Intent(SearchActivity.this, SearchResultActivity.class);
                        intent.putExtra("trip_list", new ArrayList<>(trips));
                        intent.putExtra("departure", departure);
                        intent.putExtra("arrival", arrival);
                        intent.putExtra("dateInput", dateInput);
                        startActivity(intent);
                    } else {
                        Toast.makeText(SearchActivity.this, "Không tìm thấy chuyến xe!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<List<Trip>>> call, Throwable t) {
                    t.printStackTrace();
                    Toast.makeText(SearchActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                }
            });

        });
    }

    private String convertDateFormat(String input) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return outputFormat.format(inputFormat.parse(input));
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }



}
