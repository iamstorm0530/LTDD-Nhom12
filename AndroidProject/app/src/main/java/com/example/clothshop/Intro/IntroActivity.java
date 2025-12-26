package com.example.clothshop.Intro;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.clothshop.Login.LoginActivity;
import com.example.clothshop.MainActivity;
import com.example.clothshop.R;

import java.util.ArrayList;
import java.util.List;

public class IntroActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private Button btnGetStarted;
    private LinearLayout layoutDots;

    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable sliderRunnable;

    private List<IntroItem> introList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        // Ánh xạ view
        viewPager = findViewById(R.id.viewPager);
        btnGetStarted = findViewById(R.id.btnGetStarted);
        layoutDots = findViewById(R.id.layoutDots);

        // Data cho intro
        introList = new ArrayList<>();
        introList.add(new IntroItem(
                R.drawable.intro1,
                "Happy\nShopping",
                "Discover the latest fashion products"
        ));
        introList.add(new IntroItem(
                R.drawable.intro2,
                "Modern\nOutfit",
                "Simple and elegant design"
        ));
        introList.add(new IntroItem(
                R.drawable.intro3,
                "Discover\nYour Style",
                "Buy your favorite clothes with ease"
        ));

        // Set adapter
        IntroAdapter adapter = new IntroAdapter(introList);
        viewPager.setAdapter(adapter);

        // Khởi tạo dot ban đầu
        updateDots(0);

        // Lắng nghe khi lướt trang
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateDots(position);
            }
        });

        // Auto slide mỗi 3 giây
        sliderRunnable = new Runnable() {
            @Override
            public void run() {
                int current = viewPager.getCurrentItem();
                if (current < introList.size() - 1) {
                    viewPager.setCurrentItem(current + 1);
                } else {
                    viewPager.setCurrentItem(0);
                }
                handler.postDelayed(this, 3000);
            }
        };
        handler.postDelayed(sliderRunnable, 3000);

        // Button Get Started
        btnGetStarted.setOnClickListener(v -> {
            startActivity(new Intent(IntroActivity.this, LoginActivity.class));
            finish();
        });
    }

    /**
     * Update dot indicator theo position
     */
    private void updateDots(int position) {
        for (int i = 0; i < layoutDots.getChildCount(); i++) {
            View dot = layoutDots.getChildAt(i);
            if (i == position) {
                dot.setBackgroundResource(R.drawable.dot_active);
            } else {
                dot.setBackgroundResource(R.drawable.dot_inactive);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(sliderRunnable);
    }
}
