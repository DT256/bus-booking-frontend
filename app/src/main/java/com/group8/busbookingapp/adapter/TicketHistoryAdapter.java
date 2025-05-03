package com.group8.busbookingapp.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.group8.busbookingapp.R;
import com.group8.busbookingapp.activity.ReviewActivity;
import com.group8.busbookingapp.dto.ApiResponse;
import com.group8.busbookingapp.model.Booking;
import com.group8.busbookingapp.network.ApiClient;
import com.group8.busbookingapp.network.ApiService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TicketHistoryAdapter extends RecyclerView.Adapter<TicketHistoryAdapter.TicketViewHolder> {
    private Context context;
    private List<Booking> bookingList;

    public TicketHistoryAdapter(Context context, List<Booking> bookingList) {
        this.context = context;
        this.bookingList = bookingList;
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ticket_history, parent, false);
        return new TicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        Booking booking = bookingList.get(position);

        // Status
        String status = booking.getStatus();
        holder.tvStatus.setText(status);
        switch (status.toUpperCase()) {
            case "COMPLETED":
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_completed);
                holder.tvStatus.setText("Hoàn thành");
                break;
            case "CANCELLED":
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_cancelled);
                holder.tvStatus.setText("Đã hủy");
                break;
            case "PENDING":
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_pending);
                holder.tvStatus.setText("Đang chờ");
                break;
            case "CONFIRMED":
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_pending);
                holder.tvStatus.setText("Xác nhận");
                break;
            default:
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_pending);
                break;
        }

        // Route (startCity - endCity)
        holder.tvRouteName.setText(booking.getStartCity() + " - " + booking.getEndCity());

        Glide.with(context)
                .load(booking.getBusImage())
                .placeholder(R.drawable.ic_bus)
                .error(R.drawable.ic_bus)
                .into(holder.imgBusLogo);

        // Booking code
        holder.tvBookingCode.setText(booking.getBookingCode());

        // Booking time
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy, HH:mm", Locale.getDefault());
            Date date = inputFormat.parse(booking.getCreatedAt().split("\\.")[0]);
            holder.tvBookingTime.setText(outputFormat.format(date));
        } catch (Exception e) {
            holder.tvBookingTime.setText(booking.getCreatedAt());
        }

        // Departure time
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy, HH:mm", Locale.getDefault());
            Date date = inputFormat.parse(booking.getDepartureTime().split("\\.")[0]);
            holder.tvDepartureTime.setText(outputFormat.format(date));
        } catch (Exception e) {
            holder.tvDepartureTime.setText(booking.getDepartureTime());
        }

        // Ticket quantity
        holder.tvTicketQuantity.setText(String.valueOf(booking.getSeats()));

        // Total price
        holder.tvTotalPrice.setText(String.format(Locale.getDefault(), "%,d VNĐ", booking.getTotalPrice()));

        // Button actions
        holder.btnViewDetails.setOnClickListener(v -> {
            // TODO: handle view details
        });

        // Show/hide review button based on trip status
        if ("COMPLETED".equals(booking.getStatus())) {
            holder.btnReview.setVisibility(View.VISIBLE);
            holder.btnReview.setOnClickListener(v -> {
                Intent intent = new Intent(context, ReviewActivity.class);
                intent.putExtra("bookingId", booking.getId());
                intent.putExtra("routeInfo", booking.getStartCity() + " - " + booking.getEndCity());
                intent.putExtra("tripDate", booking.getDepartureTime());
                intent.putExtra("companyName", booking.getStartCity());
                context.startActivity(intent);
            });
        } else {
            holder.btnReview.setVisibility(View.GONE);
        }

        // Show/hide cancel button based on trip status
        if ("PENDING".equals(booking.getStatus())) {
            holder.btnCancel.setVisibility(View.VISIBLE);
            holder.btnCancel.setOnClickListener(v -> {
                confirmCancelBooking(booking.getBookingCode(), position);
            });
        } else {
            holder.btnCancel.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public void updateData(List<Booking> newBookingList) {
        this.bookingList = newBookingList;
        notifyDataSetChanged();
    }

    private void confirmCancelBooking(String bookingCode, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Xác nhận")
                .setMessage("Bạn có chắc chắn muốn hủy đặt vé này không?")
                .setPositiveButton("Có", (dialog, which) -> {
                    cancelBooking(bookingCode, position);
                })
                .setNegativeButton("Không", null)
                .show();
    }

    private void cancelBooking(String bookingCode, int position) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ApiResponse<Booking>> call = apiService.cancelBooking(bookingCode);
        call.enqueue(new Callback<ApiResponse<Booking>>() {
            @Override
            public void onResponse(Call<ApiResponse<Booking>> call, Response<ApiResponse<Booking>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getStatus().equals("success")) {
                    Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    // Update the booking list
                    bookingList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, bookingList.size());
                } else {
                    String errorMessage = response.body() != null ? response.body().getMessage() : "Lỗi khi hủy vé";
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Booking>> call, Throwable t) {
                Toast.makeText(context, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("apdapter", t.getMessage());
            }
        });
    }

    static class TicketViewHolder extends RecyclerView.ViewHolder {
        TextView tvStatus, tvBusNumber, tvRouteName, tvBookingTime, tvDepartureTime,
                tvTicketQuantity, tvTotalPrice, tvBookingCode;
        Button btnViewDetails, btnReview, btnCancel;
        ImageView imgBusLogo;

        public TicketViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvBusNumber = itemView.findViewById(R.id.tvBusNumber);
            tvRouteName = itemView.findViewById(R.id.tvRouteName);
            tvBookingTime = itemView.findViewById(R.id.tvBookingTime);
            tvDepartureTime = itemView.findViewById(R.id.tvDepartureTime);
            tvTicketQuantity = itemView.findViewById(R.id.tvTicketQuantity);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
            tvBookingCode = itemView.findViewById(R.id.tvBookingCode);
            btnViewDetails = itemView.findViewById(R.id.btnViewDetails);
            btnReview = itemView.findViewById(R.id.btnReview);
            btnCancel = itemView.findViewById(R.id.btnCancel);
            imgBusLogo = itemView.findViewById(R.id.imgBusLogo);
        }
    }
}