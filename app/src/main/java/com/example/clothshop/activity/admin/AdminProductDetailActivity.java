package com.example.clothshop.activity.admin;

import android.graphics.Color;
import android.os.Bundle;
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

import java.text.NumberFormat;
import java.util.*;

public class AdminProductDetailActivity extends AppCompatActivity {

    private ImageView imgProduct;
    private TextView tvName, tvPrice, tvRating, tvTag, tvStatus, tvDescription;
    private LinearLayout layoutVariants, layoutReviews;

    private FirebaseFirestore db;
    private String productId;

    private static final List<String> SIZE_ORDER = Arrays.asList("S", "M", "L");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_product_detail);

        productId = getIntent().getStringExtra("productId");
        db = FirebaseFirestore.getInstance();

        bindViews();
        loadProduct();
    }

    private void bindViews() {
        imgProduct = findViewById(R.id.imgProduct);
        tvName = findViewById(R.id.tvName);
        tvPrice = findViewById(R.id.tvPrice);
        tvRating = findViewById(R.id.tvRating);
        tvTag = findViewById(R.id.tvTag);
        tvStatus = findViewById(R.id.tvStatus);
        tvDescription = findViewById(R.id.tvDescription);
        layoutVariants = findViewById(R.id.layoutVariants);
        layoutReviews = findViewById(R.id.layoutReviews);
    }

    private void loadProduct() {
        db.collection("products").document(productId)
                .get()
                .addOnSuccessListener(doc -> {
                    Product p = doc.toObject(Product.class);
                    if (p == null) return;

                    tvName.setText(p.getName());
                    tvPrice.setText(CurrencyUtils.format(p.getPrice()));
                    tvRating.setText(p.getAverageRating() + " ★ (" + p.getReviewCount() + ")");
                    tvTag.setText(p.getTag().toUpperCase());
                    tvDescription.setText(p.getDescription());

                    if ("hidden".equalsIgnoreCase(p.getStatus())) {
                        tvStatus.setText("HIDDEN");
                        tvStatus.setBackgroundResource(R.drawable.bg_chip_red);
                        tvStatus.setTextColor(Color.RED);
                    } else {
                        tvStatus.setText("ACTIVE");
                        tvStatus.setBackgroundResource(R.drawable.bg_chip_green);
                        tvStatus.setTextColor(Color.GREEN);
                    }

                    if (!p.getImages().isEmpty()) {
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
