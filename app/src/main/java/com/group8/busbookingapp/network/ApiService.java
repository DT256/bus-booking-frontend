package com.group8.busbookingapp.network;

import com.group8.busbookingapp.dto.ApiResponse;
import com.group8.busbookingapp.model.Trip;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("/api/trips/search")
    Call<ApiResponse<List<Trip>>> searchTrips(
            @Query("departure") String departure,
            @Query("destination") String arrival,
            @Query("date") String date // format: yyyy-MM-dd HH:mm:ss
    );
}
