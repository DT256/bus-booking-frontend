package com.group8.busbookingapp.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.chip.Chip;
import com.group8.busbookingapp.R;
import com.group8.busbookingapp.activity.LoginActivity;
import com.group8.busbookingapp.adapter.ChatAdapter;
import com.group8.busbookingapp.model.ChatManager;
import com.group8.busbookingapp.model.ChatMessage;
import com.group8.busbookingapp.network.ApiClient;
import com.group8.busbookingapp.network.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatFragment extends Fragment {
    private final String TAG = "ChatFragment";
    private RecyclerView chatRecyclerView;
    private EditText messageInput;
    private ImageButton sendButton;
    private Chip chipFindRoute;
    private Chip chipViewBookings;
    private Chip chipCancelBooking;
    private ChatAdapter chatAdapter;
    private ChatManager chatManager;
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        // Initialize views
        chatRecyclerView = view.findViewById(R.id.chatRecyclerView);
        messageInput = view.findViewById(R.id.messageInput);
        sendButton = view.findViewById(R.id.sendButton);
        chipFindRoute = view.findViewById(R.id.chip_find_route);
        chipViewBookings = view.findViewById(R.id.chip_view_bookings);
        chipCancelBooking = view.findViewById(R.id.chip_cancel_booking);

        // Initialize managers
        chatManager = ChatManager.getInstance(requireContext());

        // Initialize API service
        apiService = ApiClient.getClient().create(ApiService.class);

        // Setup RecyclerView
        chatAdapter = new ChatAdapter(chatManager.getChatHistory());
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        chatRecyclerView.setAdapter(chatAdapter);

        // Send button click listener
        sendButton.setOnClickListener(v -> {
            String messageText = messageInput.getText().toString().trim();
            if (!TextUtils.isEmpty(messageText)) {
                sendMessage(messageText);
                messageInput.setText("");
            }
        });

        // Quick messages click listeners
        chipFindRoute.setOnClickListener(v -> sendMessage("Tìm tuyến xe"));
        chipViewBookings.setOnClickListener(v -> sendMessage("Xem vé của tôi"));
        chipCancelBooking.setOnClickListener(v -> sendMessage("Hủy vé"));

        messageInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                sendButton.setEnabled(!TextUtils.isEmpty(s.toString().trim()));
            }
        });
        sendButton.setEnabled(false);

        // Load chat history
        updateChatDisplay();

        return view;
    }

    private void sendMessage(String message) {
        // Create and add user message
        ChatMessage userMessage = new ChatMessage("user", message);
        chatManager.addMessage(userMessage);
        updateChatDisplay();

        // Get auth token
//        String jwtToken = "eyJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJkdDI1NiIsImlhdCI6MTc0NjI4NTU1NCwic3ViIjoiZGlhdGllbnNpbmhAZ21haWwuY29tIiwicm9sZSI6IlVTRVIiLCJpZCI6IjY4MGIyOWQ0YjY0MWE4Mjk2ODExMzc2YSIsImV4cCI6OTIyMzM3MjAzNjg1NDc3NX0.Y90T0XlbNqO8PheqTHkZFN2y2NE5umSkTymXq-Iv2FHI2-hMuGDLTavTtIev4ZoZ8akzhzvK4uFPuoHD5NK53w";
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyAppPrefs", 0);
        String jwtToken = sharedPreferences.getString("token", null);

        if (jwtToken == null) {
            // Handle unauthenticated state
            ChatMessage errorMessage = new ChatMessage("assistant",
                    "Vui lòng đăng nhập để sử dụng tính năng này.");
            chatManager.addMessage(errorMessage);
            updateChatDisplay();
            return;
        }

        // Send to API and get response
        apiService.sendChatMessage("Bearer " + jwtToken, userMessage).enqueue(new Callback<ChatMessage>() {
            @Override
            public void onResponse(Call<ChatMessage> call, Response<ChatMessage> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ChatMessage botResponse = response.body();
                    chatManager.addMessage(botResponse);
                    updateChatDisplay();
                } else {
                    // Handle error response
                    ChatMessage errorMessage = new ChatMessage("assistant",
                            "Xin lỗi, có lỗi xảy ra. Vui lòng thử lại sau.");
                    chatManager.addMessage(errorMessage);
                    updateChatDisplay();
                }
            }

            @Override
            public void onFailure(Call<ChatMessage> call, Throwable t) {
                // Handle network error
                Log.d(TAG, "onFailure: " + t.getMessage());
                ChatMessage errorMessage = new ChatMessage("assistant",
                        "Xin lỗi, có lỗi xảy ra. Vui lòng thử lại sau.");
                chatManager.addMessage(errorMessage);
                updateChatDisplay();
            }
        });
    }

    private void updateChatDisplay() {
        chatAdapter.updateMessages(chatManager.getChatHistory());
        chatRecyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
    }
} 