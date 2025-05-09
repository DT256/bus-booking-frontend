package com.group8.busbookingapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.group8.busbookingapp.R;
import com.group8.busbookingapp.model.StopPoint;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class StopPointAdapter extends RecyclerView.Adapter<StopPointAdapter.StopPointViewHolder> {
    private List<StopPoint> stopPoints;
    private OnStopPointClickListener listener;
    private int selectedPosition = -1;

    public interface OnStopPointClickListener {
        void onStopPointClick(StopPoint stopPoint, int position);
    }

    public StopPointAdapter(List<StopPoint> stopPoints, OnStopPointClickListener listener) {
        this.stopPoints = stopPoints;
        this.listener = listener;
    }

    @NonNull
    @Override
    public StopPointViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_stop_point, parent, false);
        return new StopPointViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StopPointViewHolder holder, int position) {
        StopPoint stopPoint = stopPoints.get(position);
        holder.bind(stopPoint, position);
    }

    @Override
    public int getItemCount() {
        return stopPoints.size();
    }

    public void setSelectedPosition(int position) {
        int previousSelected = selectedPosition;
        selectedPosition = position;
        if (previousSelected != -1) {
            notifyItemChanged(previousSelected);
        }
        if (selectedPosition != -1) {
            notifyItemChanged(selectedPosition);
        }
    }

    public void updateStopPoints(List<StopPoint> newStopPoints) {
        this.stopPoints = newStopPoints;
        notifyDataSetChanged();
    }

    class StopPointViewHolder extends RecyclerView.ViewHolder {
        private TextView tvStopPointName;
        private TextView tvStopPointAddress;
        private TextView tvEstimatedTime;

        public StopPointViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStopPointName = itemView.findViewById(R.id.tvStopPointName);
            tvStopPointAddress = itemView.findViewById(R.id.tvStopPointAddress);
            tvEstimatedTime = itemView.findViewById(R.id.tvEstimatedTime);
        }

        public void bind(StopPoint stopPoint, int position) {
            tvStopPointName.setText(stopPoint.getName());
            tvStopPointAddress.setText(stopPoint.getAddress());

            tvEstimatedTime.setText("Thời gian dự kiến: " + stopPoint.getEstimatedTime());

            itemView.setSelected(position == selectedPosition);
            itemView.setOnClickListener(v -> {
                setSelectedPosition(position);
                listener.onStopPointClick(stopPoint, position);
            });
        }
    }
} 