package com.group8.busbookingapp.activity;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.group8.busbookingapp.R;
import com.group8.busbookingapp.databinding.ReviewFormBinding;
import com.group8.busbookingapp.dto.ApiResponse;
import com.group8.busbookingapp.dto.ReviewRequest;
import com.group8.busbookingapp.model.ReviewResponse;
import com.group8.busbookingapp.network.ApiClient;
import com.group8.busbookingapp.network.ApiService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewActivity extends AppCompatActivity {
    private static final Logger log = LoggerFactory.getLogger(ReviewActivity.class);
    private final String TAG = "Review Activity";
    private ReviewFormBinding binding;
    private int rating = 0;
    private String bookingId;
    private List<Uri> selectedImages = new ArrayList<>();
    private String jwtToken = "eyJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJkdDI1NiIsImlhdCI6MTc0NTU2MjE1OSwic3ViIjoiZGlhdGllbnNpbmhAZ21haWwuY29tIiwicm9sZSI6IlVTRVIiLCJpZCI6IjY4MGIyOWQ0YjY0MWE4Mjk2ODExMzc2YSIsImV4cCI6OTIyMzM3MjAzNjg1NDc3NX0.nzfO3v0PCpxaL6ci7RtXPDA78rXmBLtZcKaxttAj50OIJH0NT5Mu1Si1IawV5rmWnVyTTyl6Fwk_tEoej7Ojog"; // Ensure this is valid
    private static final int MAX_PHOTOS = 5; // Limit the number of photos
    private LinearLayout photoContainer;

    // Launcher to request permission
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    pickImages();
                } else {
                    Toast.makeText(this, "Cần cấp quyền để chọn ảnh", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.review_form);

        bookingId = getIntent().getStringExtra("bookingId");
        binding.tvRouteInfo.setText(getIntent().getStringExtra("routeInfo"));
        binding.tvTripDate.setText(getIntent().getStringExtra("tripDate"));
        binding.tvCompanyName.setText(getIntent().getStringExtra("companyName"));

        photoContainer = binding.photoContainer;

        // Setup star rating
        binding.ivStar1.setOnClickListener(v -> setRating(1));
        binding.ivStar2.setOnClickListener(v -> setRating(2));
        binding.ivStar3.setOnClickListener(v -> setRating(3));
        binding.ivStar4.setOnClickListener(v -> setRating(4));
        binding.ivStar5.setOnClickListener(v -> setRating(5));

        // Setup photo upload
        binding.cardAddPhoto.setOnClickListener(v -> {
            pickImages();
        });

        // Setup submit button
        binding.btnSubmitReview.setOnClickListener(v -> submitReview());
    }

    private void setRating(int selectedRating) {
        rating = selectedRating;

        binding.ivStar1.setImageResource(rating >= 1 ? R.drawable.ic_star_filled : R.drawable.ic_star_outline);
        binding.ivStar2.setImageResource(rating >= 2 ? R.drawable.ic_star_filled : R.drawable.ic_star_outline);
        binding.ivStar3.setImageResource(rating >= 3 ? R.drawable.ic_star_filled : R.drawable.ic_star_outline);
        binding.ivStar4.setImageResource(rating >= 4 ? R.drawable.ic_star_filled : R.drawable.ic_star_outline);
        binding.ivStar5.setImageResource(rating >= 5 ? R.drawable.ic_star_filled : R.drawable.ic_star_outline);

        String[] descriptions = {
                "Chạm vào sao để đánh giá",
                "Rất tệ",
                "Tệ",
                "Bình thường",
                "Tốt",
                "Tuyệt vời"
        };
        binding.tvRatingDescription.setText(descriptions[rating]);
    }

    private void pickImages() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Chọn ảnh"), 1);
    }

    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_MEDIA_IMAGES
        ) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestStoragePermission() {
        requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            ClipData clipData = data.getClipData();
            if (clipData != null) {
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    Uri imageUri = clipData.getItemAt(i).getUri();
                    if (!selectedImages.contains(imageUri) && selectedImages.size() < MAX_PHOTOS) {
                        selectedImages.add(imageUri);
                        addPhotoToContainer(imageUri);
                    } else if (selectedImages.size() >= MAX_PHOTOS) {
                        Toast.makeText(this, "Tối đa " + MAX_PHOTOS + " ảnh", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            } else {
                Uri imageUri = data.getData();
                if (!selectedImages.contains(imageUri) && selectedImages.size() < MAX_PHOTOS) {
                    selectedImages.add(imageUri);
                    addPhotoToContainer(imageUri);
                } else if (selectedImages.size() >= MAX_PHOTOS) {
                    Toast.makeText(this, "Tối đa " + MAX_PHOTOS + " ảnh", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void addPhotoToContainer(Uri uri) {
        LayoutInflater inflater = LayoutInflater.from(this);
        CardView cardView = (CardView) inflater.inflate(R.layout.item_photo_card, photoContainer, false);
        ImageView imageView = cardView.findViewById(R.id.ivPhoto);
        ImageView removeButton = cardView.findViewById(R.id.ivRemovePhoto);

        Glide.with(this)
                .load(uri)
                .centerCrop()
                .into(imageView);

        removeButton.setOnClickListener(v -> {
            photoContainer.removeView(cardView);
            selectedImages.remove(uri);
        });

        photoContainer.addView(cardView, photoContainer.getChildCount() - 1);
    }

    private void submitReview() {
        if (rating == 0) {
            Toast.makeText(this, "Vui lòng chọn số sao đánh giá", Toast.LENGTH_SHORT).show();
            return;
        }

        String comment = binding.etReviewComment.getText().toString().trim();
        if (comment.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập nhận xét", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create ReviewRequest object
        ReviewRequest reviewRequest = new ReviewRequest();
        reviewRequest.setBookingId(bookingId);
        reviewRequest.setRating(rating);
        reviewRequest.setComment(comment);

        // Convert ReviewRequest to JSON string
        String jsonData = new com.google.gson.Gson().toJson(reviewRequest);
        Log.d(TAG, "Request JSON: " + jsonData); // Log JSON for debugging
        RequestBody dataBody = RequestBody.create(MediaType.parse("application/json"), jsonData);

        // Prepare image parts
        List<MultipartBody.Part> imageParts = new ArrayList<>();
        for (Uri uri : selectedImages) {
            try {
                File file = new File(getRealPathFromURI(uri));
                Log.d(TAG, "Image File: " + file.getName() + ", Size: " + file.length()); // Log image details
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
                MultipartBody.Part part = MultipartBody.Part.createFormData("images", file.getName(), requestFile);
                imageParts.add(part);
            } catch (Exception e) {
                Toast.makeText(this, "Lỗi khi xử lý ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Image Processing Error: ", e);
                return;
            }
        }

        // Make API call
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ApiResponse<ReviewResponse>> call = apiService.createReview(
                "Bearer " + jwtToken,
                dataBody,
                imageParts
        );

        call.enqueue(new Callback<ApiResponse<ReviewResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<ReviewResponse>> call, Response<ApiResponse<ReviewResponse>> response) {
                Log.d(TAG, "Response: " + response.toString());
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(ReviewActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    String errorMessage = "Gửi đánh giá thất bại";
                    try {
                        // Attempt to extract error message from response body
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing response: ", e);
                    }
                    Toast.makeText(ReviewActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    Log.d(TAG, "Error Response: " + errorMessage);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ReviewResponse>> call, Throwable t) {
                Toast.makeText(ReviewActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "API Failure: ", t);
            }
        });
    }

    private String getRealPathFromURI(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        }
        return uri.getPath();
    }
}