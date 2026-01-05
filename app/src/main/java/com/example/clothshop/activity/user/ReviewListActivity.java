package com.example.clothshop.activity.user;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clothshop.R;
import com.example.clothshop.adapter.review.ReviewListAdapter;
import com.example.clothshop.model.Review;

import java.util.ArrayList;
import java.util.List;

public class ReviewListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_list);

        // Nút Back
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Setup RecyclerView
        RecyclerView rvReviews = findViewById(R.id.rvReviews);
        rvReviews.setLayoutManager(new LinearLayoutManager(this));

        // Fake Data
        List<Review> list = new ArrayList<>();
        list.add(new Review("1", "Jenny Wilson", 5, "Sản phẩm tuyệt vời, đúng như mô tả!", "10 Oct 2023"));
        list.add(new Review("2", "Ronald Richards", 4, "Giao hàng hơi chậm nhưng chất lượng tốt.", "12 Oct 2023"));
        list.add(new Review("3", "Guy Hawkins", 5, "Màu sắc đẹp, vải mềm mịn.", "15 Oct 2023"));
        list.add(new Review("4", "Savannah Nguyen", 3, "Size hơi rộng so với bảng size.", "18 Oct 2023"));

        ReviewListAdapter adapter = new ReviewListAdapter(list);
        rvReviews.setAdapter(adapter);
    }
}
