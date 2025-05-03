package com.group8.busbookingapp.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.group8.busbookingapp.fragment.BookingListFragment;

public class BookingPagerAdapter extends FragmentStateAdapter {
    private static final String[] STATUSES = {"PENDING", "CONFIRMED", "CANCELLED", "COMPLETED"};
    private static final String[] TAB_TITLES = {"Đang chờ", "Xác nhận", "Đã hủy", "Hoàn thành"};

    public BookingPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return BookingListFragment.newInstance(STATUSES[position]);
    }

    @Override
    public int getItemCount() {
        return STATUSES.length;
    }

    public String getTabTitle(int position) {
        return TAB_TITLES[position];
    }
}