package com.example.clothshop.activity.admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.*;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.*;

import com.example.clothshop.R;
import com.example.clothshop.adapter.admin.AdminProductAdapter;
import com.example.clothshop.model.*;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.List;

public class AdminProductActivity extends AppCompatActivity {

    RecyclerView rvProducts;
    EditText edtSearch;
    LinearLayout layoutTags, layoutStatusFilter;
    ImageView btnSort;

    AdminProductAdapter adapter;
    List<Product> productList = new ArrayList<>();
    FirebaseFirestore db;
    ListenerRegistration listener;

    String currentTag = "ALL";
    String currentStatus = "ALL";
    String keyword = "";

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_admin_product);

        rvProducts = findViewById(R.id.rvProducts);
        edtSearch = findViewById(R.id.edtSearch);
        layoutTags = findViewById(R.id.layoutTags);
        layoutStatusFilter = findViewById(R.id.layoutStatusFilter);
        btnSort = findViewById(R.id.btnSort);

        rvProducts.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminProductAdapter(productList);
        rvProducts.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        listenProducts();
        setupSearch();
        setupTagFilter();          // 🔥 TAG 1
        setupStatusFilter();       // 🔥 TAG 2
        setupSort();
        setupItemClick();
    }

    // ================= LOAD =================
    void listenProducts() {
        listener = db.collection("products")
                .addSnapshotListener((qs, e) -> {
                    if (qs == null) return;

                    productList.clear();

                    for (var d : qs.getDocuments()) {
                        Product p = d.toObject(Product.class);
                        if (p == null) return;

                        p.setVariants(new ArrayList<>());
                        productList.add(p);
                        loadVariants(p);
                    }

                    adapter.filter(keyword, currentTag);
                });
    }
    void loadVariants(Product p) {
        db.collection("products")
                .document(p.getId())
                .collection("variants")
                .get()
                .addOnSuccessListener(qs -> {
                    List<Variant> variants = new ArrayList<>();

                    for (var vDoc : qs.getDocuments()) {
                        Variant v = vDoc.toObject(Variant.class);
                        if (v != null) variants.add(v);
                    }

                    p.setVariants(variants);

                    adapter.notifyDataSetChanged(); // 🔥 BẮT BUỘC
                });
    }


    // ================= SEARCH =================
    void setupSearch() {
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s,int a,int b,int c){}
            @Override public void afterTextChanged(Editable e){}
            @Override
            public void onTextChanged(CharSequence s,int a,int b,int c){
                keyword = s.toString();
                adapter.filter(keyword, currentTag);
            }
        });
    }

    // ================= TAG FILTER (GENDER) =================
    void setupTagFilter() {
        for (int i = 0; i < layoutTags.getChildCount(); i++) {
            View v = layoutTags.getChildAt(i);

            if (!(v instanceof TextView)) continue; // ❗ bỏ ImageView btnSort

            TextView tv = (TextView) v;

            if ("ALL".equalsIgnoreCase(tv.getText().toString())) {
                tv.setSelected(true);
            }

            tv.setOnClickListener(x -> {
                currentTag = tv.getText().toString();
                resetTagGroup(layoutTags);
                tv.setSelected(true);
                adapter.filter(keyword, currentTag);
            });
        }
    }

    // ================= TAG FILTER (STATUS) =================
    void setupStatusFilter() {
        for (int i = 0; i < layoutStatusFilter.getChildCount(); i++) {
            View v = layoutStatusFilter.getChildAt(i);

            if (!(v instanceof TextView)) continue;

            TextView tv = (TextView) v;

            if ("ALL".equalsIgnoreCase(tv.getText().toString())) {
                tv.setSelected(true);
            }

            tv.setOnClickListener(x -> {
                currentStatus = tv.getText().toString();
                resetTagGroup(layoutStatusFilter);
                tv.setSelected(true);

                // ⚠ hiện tại adapter CHƯA filter theo status
                // 👉 giữ đúng yêu cầu: CHỈ click chọn, KHÔNG đổi logic
            });
        }
    }

    // ================= RESET TAG =================
    void resetTagGroup(LinearLayout group) {
        for (int i = 0; i < group.getChildCount(); i++) {
            View v = group.getChildAt(i);
            if (v instanceof TextView) {
                v.setSelected(false);
            }
        }
    }
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

    // ================= SORT =================
    void setupSort() {
        btnSort.setOnClickListener(v -> {

            View popupView = getLayoutInflater()
                    .inflate(R.layout.window_admin_sort, null);

            PopupWindow popupWindow = new PopupWindow(
                    popupView,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    true
            );

            popupWindow.setOutsideTouchable(true);
            popupWindow.setElevation(12f);

            // ==== BẮT SỰ KIỆN CLICK ====
            popupView.findViewById(R.id.sortPriceAsc)
                    .setOnClickListener(x -> {
                        adapter.setSort(SortType.PRICE_ASC);
                        popupWindow.dismiss();
                    });

            popupView.findViewById(R.id.sortPriceDesc)
                    .setOnClickListener(x -> {
                        adapter.setSort(SortType.PRICE_DESC);
                        popupWindow.dismiss();
                    });

            popupView.findViewById(R.id.sortNameAsc)
                    .setOnClickListener(x -> {
                        adapter.setSort(SortType.NAME_ASC);
                        popupWindow.dismiss();
                    });

            popupView.findViewById(R.id.sortNameDesc)
                    .setOnClickListener(x -> {
                        adapter.setSort(SortType.NAME_DESC);
                        popupWindow.dismiss();
                    });

            // ==== HIỆN POPUP NGAY DƯỚI ICON FILTER ====
            popupWindow.showAsDropDown(btnSort, -120, 8);
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listener != null) listener.remove();
    }
}
