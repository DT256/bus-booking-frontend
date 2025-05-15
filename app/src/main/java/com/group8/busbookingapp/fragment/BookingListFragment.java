package com.group8.busbookingapp.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.group8.busbookingapp.adapter.TicketHistoryAdapter;
import com.group8.busbookingapp.databinding.FragmentBookingListBinding;
import com.group8.busbookingapp.model.Booking;
import com.group8.busbookingapp.viewmodel.TicketHistoryViewModel;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BookingListFragment extends Fragment {
    private static final String TAG = "BookingListFragment";
    private static final String ARG_FILTER_VALUE = "filter_value";
    private static final String ARG_FILTER_FIELD = "filter_field";
    private FragmentBookingListBinding binding;
    private TicketHistoryAdapter adapter;
    private TicketHistoryViewModel viewModel;
    private List<Booking> allBookings = new ArrayList<>();
    private String filterValue;
    private String filterField;

    public static BookingListFragment newInstance(String filterValue, String filterField) {
        BookingListFragment fragment = new BookingListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_FILTER_VALUE, filterValue);
        args.putString(ARG_FILTER_FIELD, filterField);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            filterValue = getArguments().getString(ARG_FILTER_VALUE);
            filterField = getArguments().getString(ARG_FILTER_FIELD);
            Log.d(TAG, "Fragment created for filter: " + filterField + "=" + filterValue);
        }
        viewModel = new ViewModelProvider(requireActivity()).get(TicketHistoryViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBookingListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        setupObservers();
    }

    private void setupRecyclerView() {
        adapter = new TicketHistoryAdapter(requireContext(), new ArrayList<>());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(adapter);
        Log.d(TAG, "RecyclerView set up for filter: " + filterField + "=" + filterValue);
    }

    private void setupObservers() {
        viewModel.getBookings().observe(getViewLifecycleOwner(), bookings -> {
            allBookings = bookings != null ? bookings : new ArrayList<>();
            Log.d(TAG, "Updating bookings for filter " + filterField + "=" + filterValue + ": " + allBookings.size() + " total bookings");
            filterBookings();
        });
    }

    private void filterBookings() {
        List<Booking> filteredBookings = allBookings.stream()
                .filter(booking -> {
                    if ("paymentStatus".equals(filterField)) {
                        return filterValue.equalsIgnoreCase(booking.getPaymentStatus());
                    } else {
                        return filterValue.equalsIgnoreCase(booking.getStatus());
                    }
                })
                .collect(Collectors.toList());
        Log.d(TAG, "Filtered bookings for " + filterField + "=" + filterValue + ": " + filteredBookings.size());
        adapter.updateData(filteredBookings);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        Log.d(TAG, "Fragment destroyed for filter: " + filterField + "=" + filterValue);
    }
}