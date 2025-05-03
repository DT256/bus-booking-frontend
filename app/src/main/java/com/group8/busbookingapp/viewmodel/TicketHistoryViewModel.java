package com.group8.busbookingapp.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.group8.busbookingapp.model.Booking;
import java.util.List;

public class TicketHistoryViewModel extends ViewModel {
    private final MutableLiveData<List<Booking>> _bookings = new MutableLiveData<>();
    public LiveData<List<Booking>> getBookings() { return _bookings; }

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(true);
    public LiveData<Boolean> isLoading() { return _isLoading; }

    private final MutableLiveData<Boolean> _isEmpty = new MutableLiveData<>(false);
    public LiveData<Boolean> isEmpty() { return _isEmpty; }

    public void setBookings(List<Booking> bookings) {
        _bookings.setValue(bookings);
    }

    public void setLoading(boolean loading) {
        _isLoading.setValue(loading);
    }

    public void setEmpty(boolean empty) {
        _isEmpty.setValue(empty);
    }
}