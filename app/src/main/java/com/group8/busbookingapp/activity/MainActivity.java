package com.group8.busbookingapp.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.group8.busbookingapp.R;
import com.group8.busbookingapp.fragment.ProfileFragment;
import com.group8.busbookingapp.fragment.SearchFragment;
import com.group8.busbookingapp.fragment.TicketHistoryFragment;
import com.group8.busbookingapp.fragment.ChatFragment;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_search) {
                selectedFragment = new SearchFragment();
            } else if (itemId == R.id.navigation_tickets) {
                selectedFragment = new TicketHistoryFragment();
            } else if (itemId == R.id.navigation_chat) {
                selectedFragment = new ChatFragment();
            } else if (itemId == R.id.navigation_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
                return true;
            }
            return false;
        });

        // Check for intent extra to select a specific fragment
        Intent intent = getIntent();
        int selectedFragmentId = intent.getIntExtra("SELECTED_FRAGMENT", R.id.navigation_search);
        bottomNavigationView.setSelectedItemId(selectedFragmentId);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        int selectedFragmentId = intent.getIntExtra("SELECTED_FRAGMENT", R.id.navigation_search);
        bottomNavigationView.setSelectedItemId(selectedFragmentId);
    }
}