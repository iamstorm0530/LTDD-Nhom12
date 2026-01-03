package com.example.clothshop.activity.admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clothshop.R;
import com.example.clothshop.adapter.admin.AdminProductAdapter;
import com.example.clothshop.activity.admin.AdminProductDetailActivity;
import com.example.clothshop.model.Product;
import com.example.clothshop.model.Review;
import com.example.clothshop.model.Variant;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

public class AdminProductActivity extends AppCompatActivity {

    private RecyclerView rvProducts;
    private ImageView navDashboard;
    private EditText edtSearch;
    private LinearLayout layoutTags;

    private AdminProductAdapter adapter;
    private final List<Product> productList = new ArrayList<>();

    private FirebaseFirestore db;
    private ListenerRegistration productListener;

    private String currentTag = "ALL";
    private String currentKeyword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_product);

        bindViews();

        rvProducts.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminProductAdapter(productList);
        rvProducts.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        listenProducts();
        setupSearchAndTagFilter();
        setupBottomNavigation();
        setupItemClick(); // 🔥 THÊM DUY NHẤT
    }

    private void bindViews() {
        rvProducts = findViewById(R.id.rvProducts);
        navDashboard = findViewById(R.id.navDashboard);
        edtSearch = findViewById(R.id.edtSearch);
        layoutTags = findViewById(R.id.layoutTags);
    }

    // ================= LOAD PRODUCTS =================
    private void listenProducts() {
        productListener = db.collection("products")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null || snapshots == null) return;

                    productList.clear();

                    snapshots.getDocuments().forEach(doc -> {
                        Product p = doc.toObject(Product.class);
                        if (p == null) return;

                        p.setId(doc.getId());
                        p.setVariants(new ArrayList<>());
                        productList.add(p);

                        loadVariants(p);
                        loadReviews(p);
                    });

                    adapter.filter(currentKeyword, currentTag);
                });
    }

    // ================= LOAD VARIANTS =================
    private void loadVariants(Product p) {
        db.collection("products")
                .document(p.getId())
                .collection("variants")
                .get()
                .addOnSuccessListener(qs -> {
                    List<Variant> variants = new ArrayList<>();
                    qs.getDocuments().forEach(vDoc -> {
                        Variant v = vDoc.toObject(Variant.class);
                        if (v != null) variants.add(v);
                    });
                    p.setVariants(variants);
                    adapter.notifyDataSetChanged();
                });
    }

    // ================= LOAD REVIEWS =================
    private void loadReviews(Product p) {
        db.collection("products")
                .document(p.getId())
                .collection("reviews")
                .get()
                .addOnSuccessListener(qs -> {

                    if (qs.isEmpty()) {
                        p.setReviewStats(0, 0);
                        adapter.notifyDataSetChanged();
                        return;
                    }

                    double total = 0;
                    int count = 0;

                    for (var doc : qs.getDocuments()) {
                        Review r = doc.toObject(Review.class);
                        if (r != null) {
                            total += r.getRating();
                            count++;
                        }
                    }

                    double avg = total / count;
                    p.setReviewStats(avg, count);
                    adapter.notifyDataSetChanged();
                });
    }

    // ================= FILTER =================
    private void setupSearchAndTagFilter() {
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s,int a,int b,int c){}
            @Override public void afterTextChanged(Editable s){}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentKeyword = s.toString();
                adapter.filter(currentKeyword, currentTag);
            }
        });

        for (int i = 0; i < layoutTags.getChildCount(); i++) {
            TextView tv = (TextView) layoutTags.getChildAt(i);
            if ("ALL".equalsIgnoreCase(tv.getText().toString())) tv.setSelected(true);

            tv.setOnClickListener(v -> {
                currentTag = tv.getText().toString();
                resetTagUI();
                tv.setSelected(true);
                adapter.filter(currentKeyword, currentTag);
            });
        }
    }

    private void resetTagUI() {
        for (int i = 0; i < layoutTags.getChildCount(); i++) {
            ((TextView) layoutTags.getChildAt(i)).setSelected(false);
        }
    }

    private void setupBottomNavigation() {
        navDashboard.setOnClickListener(v -> {
            startActivity(new Intent(this, AdminMainActivity.class));
            finish();
        });
    }

    // ================= ITEM CLICK → DETAIL =================
    private void setupItemClick() {
        GestureDetector detector = new GestureDetector(
                this,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        return true;
                    }
                });

        rvProducts.addOnItemTouchListener(
                new RecyclerView.OnItemTouchListener() {
                    @Override
                    public boolean onInterceptTouchEvent(
                            RecyclerView rv, MotionEvent e) {

                        View child = rv.findChildViewUnder(e.getX(), e.getY());
                        if (child != null && detector.onTouchEvent(e)) {
                            int pos = rv.getChildAdapterPosition(child);
                            if (pos != RecyclerView.NO_POSITION) {
                                Product p = adapter.getItemAt(pos);

                                Intent i = new Intent(
                                        AdminProductActivity.this,
                                        AdminProductDetailActivity.class
                                );
                                i.putExtra("productId", p.getId());
                                startActivity(i);
                            }
                        }
                        return false;
                    }

                    @Override public void onTouchEvent(
                            RecyclerView rv, MotionEvent e) {}

                    @Override public void onRequestDisallowInterceptTouchEvent(
                            boolean disallowIntercept) {}
                }
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (productListener != null) productListener.remove();
    }
}
