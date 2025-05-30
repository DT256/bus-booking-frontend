package com.group8.busbookingapp.activity;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
import com.group8.busbookingapp.dto.Bank;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;

public class PaymentActivity extends AppCompatActivity {
    AutoCompleteTextView autoCompleteBanks;
    ArrayList<Bank> bankList = new ArrayList<>();
    BankAdapter bankAdapter;
    ImageView imageViewQrCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        imageViewQrCode = findViewById(R.id.imageViewQrCode);
        autoCompleteBanks = findViewById(R.id.autoCompleteBanks);

        loadBanksFromApi();
        generateQRCode();
    }
    private void generateQRCode() {
        try {
            String paymentUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html?vnp_Amount=100000000&vnp_BankCode=NCB&vnp_Command=pay&vnp_CreateDate=20250512212438&vnp_CurrCode=VND&vnp_ExpireDate=20250512213938&vnp_IpAddr=0%3A0%3A0%3A0%3A0%3A0%3A0%3A1&vnp_Locale=vn&vnp_OrderInfo=Thanh+to%3Fn+%3F%3Fn+h%3Fng%3A75778174&vnp_OrderType=other&vnp_ReturnUrl=http%3A%2F%2Flocalhost%3A8080%2Fapi%2Fpayments%2Fvn-pay-callback%3Forderid%3D682014b9079efb094fbee2e6&vnp_TmnCode=6B1GMMOU&vnp_TxnRef=61086922&vnp_Version=2.1.0&vnp_SecureHash=72450788eb2a7986abad3a2b64c2e0118fae6c871e372e4861ef8ce3750ae580a18168a62012bca95324473a529fa24ff31d4adf964168af04592367adf0a8b9";  // URL thanh toán

            // Sử dụng ZXing để tạo mã QR
            MultiFormatWriter writer = new MultiFormatWriter();
            Hashtable<EncodeHintType, String> hints = new Hashtable<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            BitMatrix bitMatrix = writer.encode(paymentUrl, BarcodeFormat.QR_CODE, 512, 512, hints);

            // Tạo bitmap từ BitMatrix
            Bitmap bitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.RGB_565);
            for (int x = 0; x < 512; x++) {
                for (int y = 0; y < 512; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? 0x000000 : 0xFFFFFF);  // Màu đen cho mã QR, trắng cho nền
                }
            }

            // Hiển thị mã QR trên ImageView
            imageViewQrCode.setImageBitmap(bitmap);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi tạo mã QR", Toast.LENGTH_SHORT).show();
        }
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
