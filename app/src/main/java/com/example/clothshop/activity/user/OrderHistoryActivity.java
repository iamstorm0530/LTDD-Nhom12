package com.example.clothshop.activity.user;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.clothshop.R;
import com.example.clothshop.fragment.OrderListFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class OrderHistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager = findViewById(R.id.viewPager);

        // Nút Back
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Setup ViewPager Adapter
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // Liên kết TabLayout và ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0: tab.setText("Active"); break;
                case 1: tab.setText("Completed"); break;
                case 2: tab.setText("Cancelled"); break;
            }
        }).attach();
    }

    // Inner Class cho Adapter của ViewPager
    class ViewPagerAdapter extends FragmentStateAdapter {

        public ViewPagerAdapter(@NonNull AppCompatActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0: return OrderListFragment.newInstance("Active");
                case 1: return OrderListFragment.newInstance("Completed");
                case 2: return OrderListFragment.newInstance("Cancelled");
                default: return OrderListFragment.newInstance("Active");
            }
        }

        @Override
        public int getItemCount() {
            return 3; // 3 tabs
        }
    }
}
