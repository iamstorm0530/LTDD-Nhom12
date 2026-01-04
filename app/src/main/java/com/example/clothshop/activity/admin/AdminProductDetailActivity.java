package com.example.clothshop.activity.admin;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.clothshop.R;
import com.example.clothshop.model.Product;
import com.example.clothshop.model.Review;
import com.example.clothshop.model.Variant;
import com.example.clothshop.utils.CurrencyUtils;
import com.example.clothshop.utils.VariantUIFactory;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.*;

public class AdminProductDetailActivity extends AppCompatActivity {

    private ImageView imgProduct, btnBack;
    private TextView tvName, tvPrice, tvRating, tvTag, tvStatus, tvDescription;

    // 🔥 SALE (THÊM)
    private LinearLayout layoutSale;
    private TextView tvSaleStatus, tvSalePercent, tvSalePrice;

    private LinearLayout layoutVariants, layoutReviews;

    private FirebaseFirestore db;
    private String productId;

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
        imgProduct = findViewById(R.id.imgProduct);
        btnBack = findViewById(R.id.btnBack);

        tvName = findViewById(R.id.tvName);
        tvPrice = findViewById(R.id.tvPrice);
        tvRating = findViewById(R.id.tvRating);
        tvTag = findViewById(R.id.tvTag);
        tvStatus = findViewById(R.id.tvStatus);
        tvDescription = findViewById(R.id.tvDescription);

        // SALE
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

                    // ================= SALE LOGIC =================
                    // Nhờ Setter bên Product đã sửa, dữ liệu ở đây sẽ chính xác
                    if (p.isOnSale()) {
                        // Hiển thị giá đã giảm ở Main Price hoặc giá gốc tùy design,
                        // ở đây set SalePrice vào field Price chính
                        tvPrice.setText(CurrencyUtils.format(p.getSalePrice()));

                        layoutSale.setVisibility(View.VISIBLE);
                        tvSaleStatus.setText("ON");

                        if (p.getSalePercent() != null) {
                            tvSalePercent.setVisibility(View.VISIBLE);
                            tvSalePercent.setText("-" + p.getSalePercent() + "%");
                        } else {
                            tvSalePercent.setVisibility(View.GONE);
                        }

                        tvSalePrice.setVisibility(View.VISIBLE);
                        tvSalePrice.setText(
                                CurrencyUtils.format(p.getSalePrice())
                        );

                    } else {
                        tvPrice.setText(CurrencyUtils.format(p.getPrice()));

                        layoutSale.setVisibility(View.VISIBLE);
                        tvSaleStatus.setText("OFF");
                        tvSalePercent.setVisibility(View.GONE);
                        tvSalePrice.setVisibility(View.GONE);
                    }
                    // =======================================================

                    tvRating.setText(
                            p.getAverageRating() + " ★ (" + p.getReviewCount() + ")"
                    );

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

                    if (p.getImages() != null && !p.getImages().isEmpty()) {
                        Glide.with(this)
                                .load(p.getImages().get(0))
                                .into(imgProduct);
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

                        map.computeIfAbsent(v.getColor(), k -> new HashMap<>())
                                .put(v.getSize(), v);
                    }

                    for (String color : map.keySet()) {
                        layoutVariants.addView(
                                VariantUIFactory.createColorRow(
                                        this, color, map.get(color)
                                )
                        );
                    }
                });
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
}