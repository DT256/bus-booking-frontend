package com.group8.busbookingapp.network;

import com.group8.busbookingapp.dto.ApiResponse;
import com.group8.busbookingapp.dto.BookingRequest;
import com.group8.busbookingapp.dto.TripDetailsResponse;
import com.group8.busbookingapp.model.Login;
import com.group8.busbookingapp.model.LoginResponse;
import com.group8.busbookingapp.model.Booking;
import com.group8.busbookingapp.model.Register;
import com.group8.busbookingapp.model.RegisterResponse;
import com.group8.busbookingapp.model.ReviewResponse;
import com.group8.busbookingapp.model.Trip;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Header;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @POST("/api/bookings/book")
    Call<ApiResponse<Booking>> bookTrip(@Header("Authorization") String token,
                                        @Body BookingRequest request);
    @GET("/api/bookings/history")
    Call<ApiResponse<List<Booking>>> getBookingHistory(@Header("Authorization") String token);

    @POST("/api/bookings/cancel")
    Call<ApiResponse<Booking>> cancelBooking(@Query("bookingCode") String bookingCode);

    @GET("api/trips/search")
    Call<ApiResponse<List<Trip>>> searchTrips(
            @Query("departure") String departure,
            @Query("destination") String arrival,
            @Query("date") String date // format: yyyy-MM-dd HH:mm:ss
    );

    @GET("api/trips/{tripId}")
    Call<ApiResponse<TripDetailsResponse>> getTripDetails(@Path("tripId") String tripId);

    @POST("/api/auth/login")
    Call<ApiResponse<LoginResponse>> login(@Body Login request);

    @POST("/api/auth/register")
    Call<ApiResponse<RegisterResponse>> register(@Body Register request);

    @POST("/api/auth/active-account")
    Call<ApiResponse<String>> activeAccount(@Query("email") String email, @Query("otp") String otp);

    @GET("/api/auth/send-otp")
    Call<ApiResponse<String>> resendOtp(@Query("email") String email);

    @FormUrlEncoded
    @POST("/api/auth/forgot-password")
    Call<ApiResponse<String>> resetPassword(
            @Field("email") String email,
            @Field("newPassword") String newPassword
    );


    @GET("getMessages")
    Call<List<Map<String, Object>>> getMessages(
            @Query("senderId") String senderId,
            @Query("receiverId") String receiverId
    );

    @Multipart
    @POST("/api/reviews")
    Call<ApiResponse<ReviewResponse>> createReview(
            @Header("Authorization") String authorization,
            @Part("data") RequestBody data,
            @Part List<MultipartBody.Part> images
    );
}
