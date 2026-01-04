package com.example.clothshop.activity.admin;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.clothshop.R;
import com.example.clothshop.model.Product;
import com.example.clothshop.model.Review;
import com.example.clothshop.model.Variant;
import com.example.clothshop.utils.CurrencyUtils;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.*;

public class AdminProductDetailActivity extends AppCompatActivity {

    private ViewPager2 viewPagerImages;
    private TabLayout tabLayoutIndicator;

    private ImageView btnBack;
    private TextView tvName, tvPrice, tvRating, tvTag, tvStatus, tvDescription;
    private LinearLayout layoutSale;
    private TextView tvSaleStatus, tvSalePercent, tvSalePrice;

    private LinearLayout layoutVariants, layoutReviews;

    private FirebaseFirestore db;
    private String productId;

    // Định nghĩa thứ tự hiển thị Size
    private static final List<String> SIZE_ORDER = Arrays.asList("S", "M", "L", "XL", "XXL");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_product_detail);

        productId = getIntent().getStringExtra("productId");
        db = FirebaseFirestore.getInstance();

        bindViews();
        loadProduct();

        btnBack.setOnClickListener(v -> finish());
    }

    private void bindViews() {
        viewPagerImages = findViewById(R.id.viewPagerImages);
        tabLayoutIndicator = findViewById(R.id.tabLayoutIndicator);

        btnBack = findViewById(R.id.btnBack);
        tvName = findViewById(R.id.tvName);
        tvPrice = findViewById(R.id.tvPrice);
        tvRating = findViewById(R.id.tvRating);
        tvTag = findViewById(R.id.tvTag);
        tvStatus = findViewById(R.id.tvStatus);
        tvDescription = findViewById(R.id.tvDescription);

        layoutSale = findViewById(R.id.layoutSale);
        tvSaleStatus = findViewById(R.id.tvSaleStatus);
        tvSalePercent = findViewById(R.id.tvSalePercent);
        tvSalePrice = findViewById(R.id.tvSalePrice);

        layoutVariants = findViewById(R.id.layoutVariants);
        layoutReviews = findViewById(R.id.layoutReviews);
    }

    private void loadProduct() {
        if (productId == null) return;

        db.collection("products").document(productId)
                .get()
                .addOnSuccessListener(doc -> {
                    Product p = doc.toObject(Product.class);
                    if (p == null) return;

                    tvName.setText(p.getName());

                    // ================= IMAGE SLIDER =================
                    List<String> images = p.getImages();
                    if (images == null) images = new ArrayList<>();

                    ImageSliderAdapter sliderAdapter = new ImageSliderAdapter(this, images);
                    viewPagerImages.setAdapter(sliderAdapter);

                    if (images.size() > 1) {
                        tabLayoutIndicator.setVisibility(View.VISIBLE);
                        new TabLayoutMediator(tabLayoutIndicator, viewPagerImages, (tab, position) -> {}).attach();
                    } else {
                        tabLayoutIndicator.setVisibility(View.GONE);
                    }

                    // ================= SALE VIEW =================
                    if (p.isOnSale()) {
                        tvPrice.setText(CurrencyUtils.format(p.getSalePrice()));
                        tvPrice.setTextColor(Color.parseColor("#D32F2F"));

                        layoutSale.setVisibility(View.VISIBLE);
                        tvSaleStatus.setText("ON SALE");
                        tvSaleStatus.setTextColor(Color.RED);
                        tvSaleStatus.setBackgroundResource(R.drawable.bg_chip_red);

                        Integer percent = p.getSalePercent();
                        if (percent != null && percent > 0) {
                            tvSalePercent.setVisibility(View.VISIBLE);
                            tvSalePercent.setText("-" + percent + "%");
                        } else {
                            tvSalePercent.setVisibility(View.GONE);
                        }

                        tvSalePrice.setVisibility(View.VISIBLE);
                        tvSalePrice.setText(CurrencyUtils.format(p.getPrice()));
                        tvSalePrice.setPaintFlags(tvSalePrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                    } else {
                        tvPrice.setText(CurrencyUtils.format(p.getPrice()));
                        tvPrice.setTextColor(Color.parseColor("#2979FF"));

                        layoutSale.setVisibility(View.VISIBLE);
                        tvSaleStatus.setText("NO SALE");
                        tvSaleStatus.setTextColor(Color.GRAY);
                        tvSaleStatus.setBackgroundResource(R.drawable.bg_chip_gray);

                        tvSalePercent.setVisibility(View.GONE);
                        tvSalePrice.setVisibility(View.GONE);
                    }

                    tvRating.setText(p.getAverageRating() + " ★ (" + p.getReviewCount() + ")");

                    if (p.getTag() != null) {
                        tvTag.setText(p.getTag().toUpperCase());
                    }

                    if (p.getDescription() != null) {
                        tvDescription.setText(p.getDescription());
                    }

                    if ("hidden".equalsIgnoreCase(p.getStatus())) {
                        tvStatus.setText("HIDDEN");
                        tvStatus.setBackgroundResource(R.drawable.bg_chip_red);
                        tvStatus.setTextColor(Color.RED);
                    } else {
                        tvStatus.setText("ACTIVE");
                        tvStatus.setBackgroundResource(R.drawable.bg_chip_green);
                        tvStatus.setTextColor(Color.GREEN);
                    }

                    loadVariants();
                    loadReviews();
                });
    }

    private void loadVariants() {
        db.collection("products").document(productId)
                .collection("variants")
                .get()
                .addOnSuccessListener(qs -> {
                    Map<String, Map<String, Variant>> map = new LinkedHashMap<>();
                    for (var d : qs.getDocuments()) {
                        Variant v = d.toObject(Variant.class);
                        if (v == null) continue;
                        map.computeIfAbsent(v.getColor(), k -> new HashMap<>()).put(v.getSize(), v);
                    }

                    layoutVariants.removeAllViews();
                    for (String color : map.keySet()) {
                        // Gọi hàm tạo row variant tùy chỉnh
                        layoutVariants.addView(
                                createVariantRow(color, map.get(color))
                        );
                    }
                });
    }

    // Hàm tạo Row Variant hiển thị Quantity
    private View createVariantRow(String color, Map<String, Variant> sizeMap) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(0, 12, 0, 12);

        // 1. Tên Màu (Color Name)
        TextView tvColor = new TextView(this);
        tvColor.setText(color.toUpperCase());
        tvColor.setWidth(dp(60));
        tvColor.setTextSize(13);
        tvColor.setTypeface(null, android.graphics.Typeface.BOLD);
        tvColor.setTextColor(Color.parseColor("#1C1C1E"));
        row.addView(tvColor);

        // 2. Container chứa các Size Chips
        LinearLayout sizeContainer = new LinearLayout(this);
        sizeContainer.setOrientation(LinearLayout.HORIZONTAL);

        for (String size : SIZE_ORDER) {
            Variant v = sizeMap.get(size);
            if (v == null) continue;

            TextView chip = new TextView(this);
            // 🔥 Hiển thị Size kèm Quantity
            chip.setText(size + ": " + v.getQuantity());
            chip.setTextSize(12);
            chip.setPadding(20, 10, 20, 10);

            // Margin giữa các chip
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            lp.setMargins(0, 0, 16, 0);
            chip.setLayoutParams(lp);

            // Logic màu sắc dựa trên số lượng
            int qty = v.getQuantity();
            if (qty == 0) {
                // Hết hàng: Đỏ
                chip.setBackgroundResource(R.drawable.bg_chip_red);
                chip.setTextColor(Color.RED);
            } else if (qty <= 10) {
                // Sắp hết: Vàng
                chip.setBackgroundResource(R.drawable.bg_chip_yellow);
                chip.setTextColor(Color.parseColor("#F57F17"));
            } else {
                // Còn hàng: Xanh
                chip.setBackgroundResource(R.drawable.bg_chip_green);
                chip.setTextColor(Color.parseColor("#2E7D32"));
            }
            sizeContainer.addView(chip);
        }

        row.addView(sizeContainer);
        return row;
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }

    private void loadReviews() {
        db.collection("products").document(productId)
                .collection("reviews")
                .get()
                .addOnSuccessListener(qs -> {
                    if (qs.isEmpty()) return;
                    for (var d : qs.getDocuments()) {
                        Review r = d.toObject(Review.class);
                        if (r == null) continue;
                        TextView tv = new TextView(this);
                        tv.setText("★ " + r.getRating() + "  " + r.getComment());
                        tv.setTextSize(13);
                        tv.setTextColor(Color.parseColor("#1C1C1E"));
                        tv.setPadding(0, 6, 0, 6);
                        layoutReviews.addView(tv);
                    }
                });
    }

    // ================= INNER ADAPTER FOR VIEWPAGER2 =================
    private static class ImageSliderAdapter extends RecyclerView.Adapter<ImageSliderAdapter.ImageViewHolder> {
        private final List<String> imageUrls;
        private final Context context;

        public ImageSliderAdapter(Context context, List<String> imageUrls) {
            this.context = context;
            this.imageUrls = imageUrls;
        }

        @NonNull
        @Override
        public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ImageView imageView = new ImageView(context);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            return new ImageViewHolder(imageView);
        }

        @Override
        public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
            String url = imageUrls.get(position);
            Glide.with(context)
                    .load(url)
                    .placeholder(R.drawable.ic_launcher_background)
                    .into((ImageView) holder.itemView);
        }

        @Override
        public int getItemCount() {
            return imageUrls.size();
        }

        static class ImageViewHolder extends RecyclerView.ViewHolder {
            public ImageViewHolder(@NonNull View itemView) {
                super(itemView);
            }
        }
    }
}