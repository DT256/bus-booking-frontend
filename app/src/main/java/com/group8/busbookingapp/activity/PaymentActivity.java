package com.group8.busbookingapp.activity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.group8.busbookingapp.R;
import com.group8.busbookingapp.dto.Bank;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PaymentActivity extends AppCompatActivity {
    Spinner spinnerBanks;
    ArrayList<Bank> bankList = new ArrayList<>();
    ArrayAdapter<Bank> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment); // đổi theo tên layout của bạn

        spinnerBanks = findViewById(R.id.spinnerBanks);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, bankList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBanks.setAdapter(adapter);

        loadBanksFromApi();
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
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Lỗi đọc dữ liệu ngân hàng", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Lỗi kết nối API ngân hàng", Toast.LENGTH_SHORT).show();
                });

        queue.add(request);
    }

}
