package com.group8.busbookingapp.fragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.group8.busbookingapp.R;
import com.group8.busbookingapp.activity.SearchActivity;
import com.group8.busbookingapp.activity.SearchResultActivity;
import com.group8.busbookingapp.dto.ApiResponse;
import com.group8.busbookingapp.model.Trip;
import com.group8.busbookingapp.network.ApiClient;
import com.group8.busbookingapp.network.ApiService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {
    private AutoCompleteTextView spinnerDepartureLocation, spinnerArrivalLocation;
    private TextInputEditText etDepartureDate;
    private Button btnSearch;
    private Calendar selectedCalendar;
    private final String BASE_URL = "http://localhost:8080/api/trips/search";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        
        spinnerDepartureLocation = view.findViewById(R.id.spinnerDepartureLocation);
        spinnerArrivalLocation = view.findViewById(R.id.spinnerArrivalLocation);
        etDepartureDate = view.findViewById(R.id.etDepartureDate);
        btnSearch = view.findViewById(R.id.btnSearch);

        setupSpinners();
        setupDatePicker();
        setupSearchButton();

        return view;
    }

    private void setupSpinners() {
        String[] locations = {"Hà Nội", "Hồ Chí Minh", "Đà Nẵng", "Cần Thơ", "Nha Trang", "Phú Yên"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, locations);
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
                    requireContext(),
                    (view, y, m, d) -> {
                        selectedCalendar.set(y, m, d, 9, 30, 52);
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
            String dateInput = etDepartureDate.getText().toString();

            if (departure.isEmpty() || arrival.isEmpty() || dateInput.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            String formattedDate = convertDateFormat(dateInput);

            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            Call<ApiResponse<List<Trip>>> call = apiService.searchTrips(departure, arrival, formattedDate);

            call.enqueue(new Callback<ApiResponse<List<Trip>>>() {
                @Override
                public void onResponse(Call<ApiResponse<List<Trip>>> call, Response<ApiResponse<List<Trip>>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Trip> trips = response.body().getData();
                        // TODO: Navigate to search results fragment
                        Intent intent = new Intent(requireContext(), SearchResultActivity.class);
                        intent.putExtra("trip_list", new ArrayList<>(trips));
                        intent.putExtra("departure", departure);
                        intent.putExtra("arrival", arrival);
                        intent.putExtra("dateInput", dateInput);
                        startActivity(intent);
                        Toast.makeText(requireContext(), "Tìm thấy " + trips.size() + " chuyến xe", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), "Không tìm thấy chuyến xe!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<List<Trip>>> call, Throwable t) {
                    t.printStackTrace();
                    Toast.makeText(requireContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
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