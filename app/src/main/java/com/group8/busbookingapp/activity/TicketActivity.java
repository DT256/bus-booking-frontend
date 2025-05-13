package com.group8.busbookingapp.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.group8.busbookingapp.R;
import com.group8.busbookingapp.adapter.BankAdapter;
import com.group8.busbookingapp.dto.ApiResponse;
import com.group8.busbookingapp.dto.Bank;
import com.group8.busbookingapp.dto.PaymentDTO;
import com.group8.busbookingapp.network.ApiClient;
import com.group8.busbookingapp.network.ApiService;
import com.group8.busbookingapp.viewmodel.TicketHistoryViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TicketActivity extends AppCompatActivity {


    AutoCompleteTextView autoCompleteBanks;
    ArrayList<Bank> bankList = new ArrayList<>();
    BankAdapter bankAdapter;
    ImageView imageViewQrCode;
    Button btnPayTicket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticketinfo);
        autoCompleteBanks = findViewById(R.id.autoCompleteBanks);
        btnPayTicket = findViewById(R.id.btnPayTicket);

        loadBanksFromApi();

        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        btnPayTicket.setOnClickListener(v -> {
            Bank selectedBank = null;
            for (Bank bank : bankList) {
                if (bank.getCode().equals(autoCompleteBanks.getText().toString())) {
                    selectedBank = bank;
                    break;
                }
            }
            Log.d("dsfjkhf", "onCreate: " + autoCompleteBanks.getText().toString());
            Log.d("dsfjkhf", "onCreate: " + bankList.toString());
            Log.d("dsfjkhf", "onCreate: " + bankList.size());
            if (selectedBank == null) {
                Toast.makeText(this, "Vui lòng chọn ngân hàng hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            int amount = 1000000;
            String orderId = "682014b9079efb094fbee2e6";

            Call<ApiResponse<PaymentDTO>> call = apiService.createVnPayPayment(amount, selectedBank.getCode(), orderId);
            call.enqueue(new Callback<ApiResponse<PaymentDTO>>() {
                @Override
                public void onResponse(Call<ApiResponse<PaymentDTO>> call, Response<ApiResponse<PaymentDTO>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String url = response.body().getData().getPaymentUrl();
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(browserIntent);
                    } else {
                        Toast.makeText(TicketActivity.this, "Lỗi khi tạo thanh toán", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<PaymentDTO>> call, Throwable t) {
                    Toast.makeText(TicketActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
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
