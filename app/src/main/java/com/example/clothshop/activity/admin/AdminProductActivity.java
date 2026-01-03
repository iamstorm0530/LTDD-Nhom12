package com.example.clothshop.activity.admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clothshop.R;
import com.example.clothshop.adapter.admin.AdminProductAdapter;
import com.example.clothshop.model.Product;
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

    // 🔥 filter state
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

        listenProductsFromFirestore();
        setupBottomNavigation();
        setupSearchAndTagFilter(); // ✅ chỉ thêm – không phá
    }

    private void bindViews() {
        rvProducts = findViewById(R.id.rvProducts);
        navDashboard = findViewById(R.id.navDashboard);
        edtSearch = findViewById(R.id.edtSearch);
        layoutTags = findViewById(R.id.layoutTags);
    }

    // 🔥 READ FIRESTORE REALTIME (GIỮ NGUYÊN)
    private void listenProductsFromFirestore() {
        productListener = db.collection("products")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null || snapshots == null) return;

                    productList.clear();

                    snapshots.getDocuments().forEach(doc -> {
                        Product p = doc.toObject(Product.class);
                        if (p != null) {
                            p.id = doc.getId();
                            productList.add(p);
                        }
                    });

                    adapter.filter(currentKeyword, currentTag);
                });
    }

    // ================= SEARCH + TAG FILTER =================
    private void setupSearchAndTagFilter() {

        // 🔍 search realtime
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s,int a,int b,int c){}
            @Override public void afterTextChanged(Editable s){}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentKeyword = s.toString();
                adapter.filter(currentKeyword, currentTag);
            }
        });

        // 🏷 tag click (DÙNG selected state – KHÔNG drawable)
        for (int i = 0; i < layoutTags.getChildCount(); i++) {
            TextView tagView = (TextView) layoutTags.getChildAt(i);

            // mặc định ALL được chọn
            if ("ALL".equalsIgnoreCase(tagView.getText().toString())) {
                tagView.setSelected(true);
            }

            tagView.setOnClickListener(v -> {
                currentTag = tagView.getText().toString();
                resetTagUI();
                tagView.setSelected(true); // ✅ CHUẨN
                adapter.filter(currentKeyword, currentTag);
            });
        }
    }

    // ❌ KHÔNG set background nữa
    // ✅ chỉ reset selected state
    private void resetTagUI() {
        for (int i = 0; i < layoutTags.getChildCount(); i++) {
            TextView tv = (TextView) layoutTags.getChildAt(i);
            tv.setSelected(false);
        }
    }

    private void setupBottomNavigation() {
        navDashboard.setOnClickListener(v -> {
            startActivity(new Intent(
                    AdminProductActivity.this,
                    AdminMainActivity.class
            ));
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (productListener != null) {
            productListener.remove();
        }
    }
}
