package com.example.clothshop.Home;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clothshop.R;
import com.example.clothshop.model.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private TextView tvHello;
    private TextView tvSeeAll;
    private RecyclerView rvProducts;
    private EditText edtSearch;
    private LinearLayout layoutMale;;
    private LinearLayout layoutFemale;;
    private LinearLayout layoutKids;;
    private LinearLayout layoutUnisex;;
    private ProductAdapter adapter;
    private final List<Product> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        // 1) bind views
        tvHello = findViewById(R.id.tvHello);
        tvSeeAll = findViewById(R.id.tvSeeAll);
        rvProducts = findViewById(R.id.rvProducts);

        // 2) greeting
        bindUserNameOrGuest();

        // 3) RecyclerView
        rvProducts.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new ProductAdapter(this, data);
        rvProducts.setAdapter(adapter);

        // 4) See all
        tvSeeAll.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, ProductListActivity.class))
        );

        // 5) load products
        loadProductsFromFirestore();

        // bắt sự kiện nút home
        ImageView imgHome = findViewById(R.id.imgHome);
        imgHome.setOnClickListener(v -> {
        });

        // xử lí nut search
        edtSearch = findViewById(R.id.edtSearch);
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterBySearchKeyword(s.toString());
            }
        });

        layoutMale = findViewById(R.id.layoutMale);
        layoutMale.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProductListActivity.class);
            intent.putExtra("TAG", "male");
            startActivity(intent);
        });

        layoutUnisex  = findViewById(R.id.layoutUnisex);
        layoutUnisex.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProductListActivity.class);
            intent.putExtra("TAG", "unisex");
            startActivity(intent);
        });

        layoutFemale = findViewById(R.id.layoutFemale);
        layoutFemale.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProductListActivity.class);
            intent.putExtra("TAG", "female");
            startActivity(intent);
        });

        layoutKids = findViewById(R.id.layoutKids);
        layoutKids.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProductListActivity.class);
            intent.putExtra("TAG", "kids");
            startActivity(intent);
        });

    }

    private void bindUserNameOrGuest() {
        FirebaseUser u = FirebaseAuth.getInstance().getCurrentUser();
        if (u == null) {
            tvHello.setText("Guest");
            return;
        }

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(u.getUid()) // bạn lưu user theo uid
                .get()
                .addOnSuccessListener(doc -> {
                    String name = doc.getString("name");
                    if (name == null || name.trim().isEmpty()) name = "User";
                    tvHello.setText(name);
                })
                .addOnFailureListener(e -> tvHello.setText("User"));
    }

    private void loadProductsFromFirestore() {
        FirebaseFirestore.getInstance()
                .collection("products")
                .whereEqualTo("status", "active")  // nếu bạn có status
                .limit(10)
                .get()
                .addOnSuccessListener(qs -> {
                    data.clear();
                    for (DocumentSnapshot doc : qs.getDocuments()) {
                        Product p = doc.toObject(Product.class);
                        if (p != null) {
                            p.setId(doc.getId());
                            data.add(p);
                        }
                    }
                    adapter.setOriginalList(data);
                })
                .addOnFailureListener(Throwable::printStackTrace);
    }



    private void filterBySearchKeyword(String input){
        if (input == null || input.trim().isEmpty()){
            adapter.filter(data);
            return;
        }
        String key = input.trim().toLowerCase();
        String category = null;

        if (key.contains("tshirt") || key.contains("t-shirt") || key.contains("TSHIRT") || key.contains("TShirt")){
            category = "TSHIRT";
        } else if  (key.contains("shirt") || key.contains("SHIRT") || key.contains("Shirt")){
            category = "SHIRT";
        } else if (key.contains("jacket") || key.contains("Jacket") || key.contains("JACKET")){
            category = "JACKET";
        } else if (key.contains("pant") || key.contains("Pant") || key.contains("PANT")){
            category = "PANT";
        }

        List<Product> filtered = new ArrayList<>();
        for (Product p : data){
            boolean matchName = false;
            boolean matchCategory = false;

            if (p.getName() != null && p.getName().toLowerCase().contains(key)){
                    matchName = true;
                }


            if (category != null && p.getCategoryId() != null && p.getCategoryId().equalsIgnoreCase(category)){
                matchCategory = true;
            }

            if (matchName || matchCategory){
                filtered.add(p);
            }
        }
        adapter.filter(filtered);
    }
}
