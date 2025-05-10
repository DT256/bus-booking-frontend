package com.group8.busbookingapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.group8.busbookingapp.R;
import com.group8.busbookingapp.model.StopPoint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StopPointAdapter extends RecyclerView.Adapter<StopPointAdapter.StopPointViewHolder> {
    private List<StopPoint> stopPoints;
    private OnStopPointClickListener listener;
    private int selectedPosition = -1;
    private static final SimpleDateFormat inputFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
    private static final SimpleDateFormat outputFormatter = new SimpleDateFormat("HH:mm", Locale.getDefault());

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

        // Update isSelected state
        if (previousSelected != -1 && previousSelected < stopPoints.size()) {
            stopPoints.get(previousSelected).setSelected(false);
            notifyItemChanged(previousSelected);
        }
        if (selectedPosition != -1 && selectedPosition < stopPoints.size()) {
            stopPoints.get(selectedPosition).setSelected(true);
            notifyItemChanged(selectedPosition);
        }
    }

    public void updateStopPoints(List<StopPoint> newStopPoints) {
        this.stopPoints = newStopPoints;
        selectedPosition = -1; // Reset selection
        for (int i = 0; i < stopPoints.size(); i++) {
            if (stopPoints.get(i).isSelected()) {
                selectedPosition = i;
                break;
            }
        }
        notifyDataSetChanged();
    }

    class StopPointViewHolder extends RecyclerView.ViewHolder {
        private TextView tvStopPointName;
        private TextView tvStopPointAddress;
        private TextView tvEstimatedTime;
        private CheckBox cbSelect;

        public StopPointViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStopPointName = itemView.findViewById(R.id.tvStopPointName);
            tvStopPointAddress = itemView.findViewById(R.id.tvStopPointAddress);
            tvEstimatedTime = itemView.findViewById(R.id.tvEstimatedTime);
            cbSelect = itemView.findViewById(R.id.cbSelect);
        }

        public void bind(StopPoint stopPoint, int position) {
            tvStopPointName.setText(stopPoint.getName());
            tvStopPointAddress.setText(stopPoint.getAddress());

            // Format estimated time as HH:mm
            String formattedTime;
            try {
                Date estimatedTime = inputFormatter.parse(stopPoint.getEstimatedTime());
                formattedTime = "Dự kiến: " + outputFormatter.format(estimatedTime);
            } catch (ParseException e) {
                // Fallback if parsing fails
                formattedTime = "Dự kiến: " + stopPoint.getEstimatedTime();
            }
            tvEstimatedTime.setText(formattedTime);

            // Update CheckBox state based on isSelected
            cbSelect.setChecked(stopPoint.isSelected());

            // Handle item click
            itemView.setOnClickListener(v -> {
                setSelectedPosition(position);
                listener.onStopPointClick(stopPoint, position);
            });

            // Handle CheckBox click
            cbSelect.setOnClickListener(v -> {
                setSelectedPosition(position);
                listener.onStopPointClick(stopPoint, position);
            });
        }
    }
}