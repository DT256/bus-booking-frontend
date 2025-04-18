package com.group8.busbookingapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.group8.busbookingapp.R;
import com.group8.busbookingapp.model.Trip;

import java.util.List;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {

    private List<Trip> tripList;

    public TripAdapter(List<Trip> tripList) {
        this.tripList = tripList;
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bus, parent, false);
        return new TripViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        Trip trip = tripList.get(position);

        holder.tvBusCompanyName.setText("Nhà xe");
        holder.tvBusType.setText(trip.getBusType());
        holder.tvDepartureTime.setText(trip.getDepartureTime().substring(11, 16));
        holder.tvArrivalTime.setText(trip.getArrivalTime().substring(11, 16));
        holder.tvPickupLocation.setText(trip.getStartPointCity());
        holder.tvDropoffLocation.setText(trip.getEndPointCity());
        holder.tvDuration.setText(String.valueOf(trip.getDuration()));
        holder.tvPrice.setText(String.format("%,d VNĐ", trip.getPrice()));
        holder.tvAvailableSeats.setText("Còn " + trip.getAvailableSeats() + " chỗ");

        Glide.with(holder.itemView.getContext()).load(trip.getAvatar()).into(holder.ivBusCompanyLogo);
    }

    @Override
    public int getItemCount() {
        return tripList.size();
    }

    public class TripViewHolder extends RecyclerView.ViewHolder {

        ImageView ivBusCompanyLogo;
        TextView tvBusCompanyName, tvBusType, tvDepartureTime, tvArrivalTime,
                tvPickupLocation, tvDropoffLocation, tvDuration, tvPrice, tvAvailableSeats;

        public TripViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBusCompanyLogo = itemView.findViewById(R.id.ivBusCompanyLogo);
            tvBusCompanyName = itemView.findViewById(R.id.tvBusCompanyName);
            tvBusType = itemView.findViewById(R.id.tvBusType);
            tvDepartureTime = itemView.findViewById(R.id.tvDepartureTime);
            tvArrivalTime = itemView.findViewById(R.id.tvArrivalTime);
            tvPickupLocation = itemView.findViewById(R.id.tvPickupLocation);
            tvDropoffLocation = itemView.findViewById(R.id.tvDropoffLocation);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvAvailableSeats = itemView.findViewById(R.id.tvAvailableSeats);
        }
    }
}

