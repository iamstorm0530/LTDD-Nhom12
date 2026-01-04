package com.example.clothshop.Home;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clothshop.R;
import com.example.clothshop.model.Product;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ProductListActivity extends AppCompatActivity {

    private ProductAdapter adapter;
    private RecyclerView listProducts;

    private TextView tabAll, tabJacket, tabShirt, tabPant, tabTshirt;
    private TextView[] tabs;

    private final List<Product> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity_product_list);

        // ===== RecyclerView =====
        listProducts = findViewById(R.id.listProducts);
        listProducts.setLayoutManager(new GridLayoutManager(this, 2));

        adapter = new ProductAdapter(this, data);
        listProducts.setAdapter(adapter);

        // ===== Tabs =====
        tabAll = findViewById(R.id.tabAll);
        tabJacket = findViewById(R.id.tabJacket);
        tabShirt = findViewById(R.id.tabShirt);
        tabPant = findViewById(R.id.tabPant);
        tabTshirt = findViewById(R.id.tabTshirt);

        tabs = new TextView[]{tabAll, tabJacket, tabShirt, tabPant, tabTshirt};

        for (TextView tab : tabs) {
            tab.setOnClickListener(v -> selectTab(tab));
        }

        // ===== Load theo TAG nếu có =====
        String tag = getIntent().getStringExtra("TAG");
        if (tag != null) {
            loadProductByTag(tag);
        } else {
            loadAllProducts();
        }

        // ===== Back =====
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // ===== Home =====
        findViewById(R.id.imgHome).setOnClickListener(v -> {
            Intent intent = new Intent(ProductListActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });
    }

    // ===== LOAD ALL =====
    private void loadAllProducts() {
        FirebaseFirestore.getInstance()
                .collection("products")
                .whereEqualTo("status", "active")
                .get()
                .addOnSuccessListener(qs -> {
                    data.clear();

                    for (DocumentSnapshot doc : qs.getDocuments()) {
                        Product p = doc.toObject(Product.class);
                        if (p != null) {
                            p.setId(doc.getId()); // ✅ FIX private
                            data.add(p);
                        }
                    }

                    adapter.setOriginalList(data);
                    selectTab(tabAll);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Load product failed", Toast.LENGTH_SHORT).show()
                );
    }

    // ===== LOAD BY TAG (MALE / FEMALE ...) =====
    private void loadProductByTag(String tag) {
        FirebaseFirestore.getInstance()
                .collection("products")
                .whereEqualTo("tag", tag)
                .whereEqualTo("status", "active")
                .get()
                .addOnSuccessListener(qs -> {
                    data.clear();

                    for (DocumentSnapshot doc : qs.getDocuments()) {
                        Product p = doc.toObject(Product.class);
                        if (p != null) {
                            p.setId(doc.getId()); // ✅ FIX private
                            data.add(p);
                        }
                    }

                    adapter.setOriginalList(data);
                    selectTab(tabAll);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Load product failed", Toast.LENGTH_SHORT).show()
                );
    }

    // ===== TAB FILTER =====
    private void selectTab(TextView selectedTab) {
        for (TextView tab : tabs) {
            tab.setBackgroundResource(R.drawable.tab_unselected);
            tab.setTextColor(Color.BLACK);
        }

        selectedTab.setBackgroundResource(R.drawable.tab_selected);
        selectedTab.setTextColor(Color.WHITE);

        if (selectedTab == tabAll) adapter.filterByCategory("ALL");
        else if (selectedTab == tabJacket) adapter.filterByCategory("JACKET");
        else if (selectedTab == tabShirt) adapter.filterByCategory("SHIRT");
        else if (selectedTab == tabPant) adapter.filterByCategory("PANT");
        else if (selectedTab == tabTshirt) adapter.filterByCategory("TSHIRT");
    }
}
