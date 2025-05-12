package com.group8.busbookingapp.activity;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.group8.busbookingapp.R;
import com.group8.busbookingapp.adapter.BankAdapter;
import com.group8.busbookingapp.dto.Bank;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PaymentActivity extends AppCompatActivity {
    AutoCompleteTextView autoCompleteBanks;
    ArrayList<Bank> bankList = new ArrayList<>();
    BankAdapter bankAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        autoCompleteBanks = findViewById(R.id.autoCompleteBanks);

        loadBanksFromApi();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof AutoCompleteTextView) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                    v.clearFocus();
                    // Ẩn bàn phím
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private void loadBanksFromApi() {
        String url = "https://api.vietqr.io/v2/banks";
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray dataArray = response.getJSONArray("data");
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject bankObj = dataArray.getJSONObject(i);
                            String name = bankObj.getString("name");
                            String code = bankObj.getString("code");
                            String shortName = bankObj.getString("shortName");
                            String logo = bankObj.getString("logo");

                            bankList.add(new Bank(name, code, shortName, logo));
                        }

                        bankAdapter = new BankAdapter(this, bankList);
                        autoCompleteBanks.setAdapter(bankAdapter);
                        autoCompleteBanks.setThreshold(1);
                        autoCompleteBanks.setOnClickListener(v -> autoCompleteBanks.showDropDown());


                        autoCompleteBanks.setOnItemClickListener((parent, view, position, id) -> {
                            Bank selectedBank = (Bank) parent.getItemAtPosition(position);


                            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                            if (imm != null) {
                                imm.hideSoftInputFromWindow(autoCompleteBanks.getWindowToken(), 0);
                            }


                            autoCompleteBanks.clearFocus();
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> error.printStackTrace()
        );

        queue.add(request);
    }



}
