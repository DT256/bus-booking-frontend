package com.group8.busbookingapp.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.group8.busbookingapp.fragment.BookingListFragment;

public class BookingPagerAdapter extends FragmentStateAdapter {
    private static final String[] TAB_TITLES = {"Chờ thanh toán", "Chờ đi", "Đã hủy", "Hoàn thành"};
    private static final String[] FILTER_VALUES = {"PENDING", "CONFIRMED", "CANCELLED", "COMPLETED"};
    private static final String[] FILTER_FIELDS = {"paymentStatus", "status", "status", "status"};

    public BookingPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return BookingListFragment.newInstance(FILTER_VALUES[position], FILTER_FIELDS[position]);
    }

    @Override
    public int getItemCount() {
        return TAB_TITLES.length;
    }

    public String getTabTitle(int position) {
        return TAB_TITLES[position];
    }
}