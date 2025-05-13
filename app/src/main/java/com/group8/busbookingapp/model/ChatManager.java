package com.group8.busbookingapp.model;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ChatManager {
    private static final String PREF_NAME = "chat_history";
    private static final String KEY_CHAT_HISTORY = "chat_history";
    private static ChatManager instance;
    private final SharedPreferences preferences;
    private final Gson gson;
    private List<ChatMessage> chatHistory;

    private ChatManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
        loadChatHistory();
    }

    public static synchronized ChatManager getInstance(Context context) {
        if (instance == null) {
            instance = new ChatManager(context.getApplicationContext());
        }
        return instance;
    }

    private void loadChatHistory() {
        String json = preferences.getString(KEY_CHAT_HISTORY, null);
        if (json != null) {
            Type type = new TypeToken<ArrayList<ChatMessage>>(){}.getType();
            chatHistory = gson.fromJson(json, type);
        } else {
            chatHistory = new ArrayList<>();
        }
    }

    private void saveChatHistory() {
        String json = gson.toJson(chatHistory);
        preferences.edit().putString(KEY_CHAT_HISTORY, json).apply();
    }

    public void addMessage(ChatMessage message) {
        chatHistory.add(message);
        saveChatHistory();
    }

    public List<ChatMessage> getChatHistory() {
        return new ArrayList<>(chatHistory);
    }

    public void clearChatHistory() {
        chatHistory.clear();
        saveChatHistory();
    }
} 