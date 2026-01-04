package com.example.clothshop.Intro;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.clothshop.Login.LoginActivity;
import com.example.clothshop.Home.HomeActivity;
import com.example.clothshop.R;
import com.example.clothshop.util.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class IntroActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private LinearLayout layoutDots;

    private MaterialButtonToggleGroup toggleAuth;
    private MaterialButton btnLogin, btnGuest;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable sliderRunnable;

    private List<IntroItem> introList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        // Ánh xạ view
        viewPager = findViewById(R.id.viewPager);
        layoutDots = findViewById(R.id.layoutDots);

        // Nút segmented
        toggleAuth = findViewById(R.id.toggleAuth);
        btnLogin = findViewById(R.id.btnLogin);
        btnGuest = findViewById(R.id.btnGuest);

        // Data cho intro
        introList = new ArrayList<>();
        introList.add(new IntroItem(R.drawable.intro1, "Happy\nShopping", "Discover the latest fashion products"));
        introList.add(new IntroItem(R.drawable.intro2, "Modern\nOutfit", "Simple and elegant design"));
        introList.add(new IntroItem(R.drawable.intro3, "Discover\nYour Style", "Buy your favorite clothes with ease"));

        // Set adapter
        IntroAdapter adapter = new IntroAdapter(introList);
        viewPager.setAdapter(adapter);

        // Tạo dot theo số slide và set dot ban đầu
        buildDots();
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
                int next = (current < introList.size() - 1) ? current + 1 : 0;
                viewPager.setCurrentItem(next, true);
                handler.postDelayed(this, 3000);
            }
        };

        // chọn mặc định Guest (để giống "Get Started" trước đây)
        if (toggleAuth != null) {
            toggleAuth.check(R.id.btnGuest);
        }

        // Guest -> MainActivity
        btnGuest.setOnClickListener(v -> {
            // nếu trước đó có login Firebase, signOut để tránh dính session
            FirebaseAuth.getInstance().signOut();

            new SessionManager(this).setGuestMode();

            Intent i = new Intent(IntroActivity.this, HomeActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        });

        // Login -> LoginActivity
        btnLogin.setOnClickListener(v -> {
            startActivity(new Intent(IntroActivity.this, LoginActivity.class));
        });
    }

    /**
     * Tạo đúng số dots theo số slide
     */
    private void buildDots() {
        if (layoutDots == null) return;

        layoutDots.removeAllViews();
        for (int i = 0; i < introList.size(); i++) {
            View dot = new View(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(18, 18);
            lp.setMargins(10, 0, 10, 0);
            dot.setLayoutParams(lp);
            dot.setBackgroundResource(R.drawable.dot_inactive);
            layoutDots.addView(dot);
        }
    }

    /**
     * Update dot indicator theo position
     */
    private void updateDots(int position) {
        if (layoutDots == null) return;

        for (int i = 0; i < layoutDots.getChildCount(); i++) {
            View dot = layoutDots.getChildAt(i);
            dot.setBackgroundResource(i == position ? R.drawable.dot_active : R.drawable.dot_inactive);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(sliderRunnable, 3000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(sliderRunnable);
    }
}
