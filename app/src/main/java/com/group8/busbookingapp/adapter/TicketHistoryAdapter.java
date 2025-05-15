package com.group8.busbookingapp.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.group8.busbookingapp.R;
import com.group8.busbookingapp.activity.ReviewActivity;
import com.group8.busbookingapp.activity.TicketActivity;
import com.group8.busbookingapp.activity.TicketHistoryActivity;
import com.group8.busbookingapp.dto.ApiResponse;
import com.group8.busbookingapp.model.Booking;
import com.group8.busbookingapp.network.ApiClient;
import com.group8.busbookingapp.network.ApiService;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TicketHistoryAdapter extends RecyclerView.Adapter<TicketHistoryAdapter.TicketViewHolder> {
    private Context context;
    private List<Booking> bookingList;
    private ApiService apiService;

    public TicketHistoryAdapter(Context context, List<Booking> bookingList) {
        this.context = context;
        this.bookingList = new ArrayList<>(bookingList);
        this.apiService = ApiClient.getClient().create(ApiService.class);
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
        switch (status.toUpperCase()) {
            case "COMPLETED":
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_completed);
                holder.tvStatus.setText(R.string.completed);
                break;
            case "CANCELLED":
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_cancelled);
                holder.tvStatus.setText(R.string.cancelled);
                break;
            case "PENDING":
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_pending);
                holder.tvStatus.setText(R.string.pending);
                break;
            case "CONFIRMED":
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_confirmed);
                holder.tvStatus.setText(R.string.confirmed);
                break;
            default:
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_pending);
                holder.tvStatus.setText(status);
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
            String dateStr = booking.getCreatedAt();
            if (dateStr.contains(".")) {
                dateStr = dateStr.split("\\.")[0];
            }
            Date date = inputFormat.parse(dateStr);
            holder.tvBookingTime.setText(outputFormat.format(date));
        } catch (Exception e) {
            holder.tvBookingTime.setText(R.string.unknown_date);
        }

        // Departure time
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy, HH:mm", Locale.getDefault());
            String dateStr = booking.getDepartureTime();
            if (dateStr.contains(".")) {
                dateStr = dateStr.split("\\.")[0];
            }
            Date date = inputFormat.parse(dateStr);
            holder.tvDepartureTime.setText(outputFormat.format(date));
        } catch (Exception e) {
            holder.tvDepartureTime.setText(R.string.unknown_date);
        }

        // Ticket quantity
        holder.tvTicketQuantity.setText(String.valueOf(booking.getSeats()));

        // Total price
        holder.tvTotalPrice.setText(String.format(Locale.getDefault(), "%,d VNĐ", booking.getTotalPrice()));

        // Button actions
        holder.btnViewDetails.setOnClickListener(v -> {
            Intent intent = new Intent(context, TicketActivity.class);
            intent.putExtra("BOOKING_ID", booking.getId());
            intent.putExtra("CITY_START", booking.getStartCity());
            intent.putExtra("CITY_END", booking.getEndCity());
            intent.putExtra("TOTAL_PRICE", booking.getTotalPrice());
            intent.putExtra("BOOKING_CODE", booking.getBookingCode());
            // Convert seatNames List to a comma-separated String
            intent.putExtra("SEAT", String.join(", ", booking.getSeatNames()));
            intent.putExtra("TIME", booking.getDepartureTime());
            intent.putExtra("ARRIVAL_TIME", booking.getArrivalTime());
            intent.putExtra("PICKUP", booking.getStartCity());
            intent.putExtra("DROPOFF", booking.getEndCity());
            intent.putExtra("PAYMENT_STATUS", booking.getPaymentStatus()); // Pass paymentStatus
            context.startActivity(intent);
        });

        // Show/hide review button
        holder.btnReview.setVisibility("COMPLETED".equals(booking.getStatus()) ? View.VISIBLE : View.GONE);
        holder.btnReview.setOnClickListener(v -> {
            Intent intent = new Intent(context, ReviewActivity.class);
            intent.putExtra("bookingId", booking.getId());
            intent.putExtra("routeInfo", booking.getStartCity() + " - " + booking.getEndCity());
            intent.putExtra("tripDate", booking.getDepartureTime());
            intent.putExtra("companyName", booking.getStartCity());
            context.startActivity(intent);
        });

        // Show/hide cancel button
        holder.btnCancel.setVisibility("PENDING".equals(booking.getStatus()) ? View.VISIBLE : View.GONE);
        holder.btnCancel.setOnClickListener(v -> confirmCancelBooking(booking.getBookingCode()));
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public void updateData(List<Booking> newBookingList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffCallback(bookingList, newBookingList));
        bookingList.clear();
        bookingList.addAll(newBookingList);
        diffResult.dispatchUpdatesTo(this);
    }

    private static class DiffCallback extends DiffUtil.Callback {
        private final List<Booking> oldList;
        private final List<Booking> newList;

        DiffCallback(List<Booking> oldList, List<Booking> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).getId().equals(newList.get(newItemPosition).getId());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
        }
    }

    private void confirmCancelBooking(String bookingCode) {
        new AlertDialog.Builder(context)
                .setTitle(R.string.confirm)
                .setMessage(R.string.cancel_booking_confirmation)
                .setPositiveButton(R.string.yes, (dialog, which) -> cancelBooking(bookingCode))
                .setNegativeButton(R.string.no, null)
                .show();
    }

    private void cancelBooking(String bookingCode) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String jwtToken = sharedPreferences.getString("token", null);
        if (jwtToken == null) {
            Toast.makeText(context, R.string.please_login, Toast.LENGTH_SHORT).show();
            return;
        }

        Call<ApiResponse<Booking>> call = apiService.cancelBooking(bookingCode);
        call.enqueue(new Callback<ApiResponse<Booking>>() {
            @Override
            public void onResponse(Call<ApiResponse<Booking>> call, Response<ApiResponse<Booking>> response) {
                if (response.isSuccessful() && response.body() != null && "success".equals(response.body().getStatus())) {
                    Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    if (context instanceof TicketHistoryActivity) {
                        ((TicketHistoryActivity) context).refreshBookings();
                    }
                } else {
                    String errorMessage = response.body() != null ? response.body().getMessage() : "getString(R.string.cancel_booking_error)";
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Booking>> call, Throwable t) {
                String errorMessage = t instanceof java.io.IOException ?
                        "getString(R.string.network_error)" : "Lỗi: " + t.getMessage();
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
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