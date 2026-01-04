package com.example.clothshop.activity.admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.*;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.*;

import com.example.clothshop.R;
import com.example.clothshop.adapter.admin.AdminProductAdapter;
import com.example.clothshop.model.*;
import com.google.firebase.firestore.*;

import java.util.*;

public class AdminProductActivity extends AppCompatActivity {

    // ================= VIEW =================
    private RecyclerView rvProducts;
    private EditText edtSearch;
    private LinearLayout layoutTags, layoutStatusFilter;
    private ImageView btnSort, navDashboard, btnBack;

    // ================= DATA =================
    private final List<Product> productList = new ArrayList<>();
    private AdminProductAdapter adapter;
    private FirebaseFirestore db;
    private ListenerRegistration listener;

    private String keyword = "";
    private String currentTag = "ALL";

    // 🔥 MULTI STATUS
    private final Set<String> selectedStatuses = new HashSet<>();

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_admin_product);

        bindViews();

        rvProducts.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminProductAdapter(productList);
        rvProducts.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        listenProducts();
        setupSearch();
        setupTagFilter();
        setupStatusFilter();
        setupSort();
        setupItemClick();
        setupBottomNavigation();

        btnBack.setOnClickListener(v -> finish());
    }

    // ================= LOAD PRODUCTS =================
    private void listenProducts() {
        listener = db.collection("products")
                .addSnapshotListener((qs, e) -> {
                    if (qs == null) return;

                    productList.clear();

                    for (var d : qs.getDocuments()) {
                        Product p = d.toObject(Product.class);
                        if (p == null) continue;

                        // ❗ dùng @DocumentId
                        p.setVariants(new ArrayList<>());
                        productList.add(p);

                        loadVariants(p);
                    }

                    adapter.filter(keyword, currentTag, buildEffectiveStatuses());
                });
    }

    private void loadVariants(Product p) {
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
                    adapter.notifyDataSetChanged();
                });
    }

    // ================= SEARCH =================
    private void setupSearch() {
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s,int a,int b,int c){}
            @Override public void afterTextChanged(Editable e){}
            @Override
            public void onTextChanged(CharSequence s,int a,int b,int c){
                keyword = s.toString();
                adapter.filter(keyword, currentTag, buildEffectiveStatuses());
            }
        });
    }

    // ================= TAG FILTER (GENDER) =================
    private void setupTagFilter() {
        for (int i = 0; i < layoutTags.getChildCount(); i++) {
            View v = layoutTags.getChildAt(i);
            if (!(v instanceof TextView)) continue;

            TextView tv = (TextView) v;
            if ("ALL".equalsIgnoreCase(tv.getText().toString())) {
                tv.setSelected(true);
            }

            tv.setOnClickListener(x -> {
                currentTag = tv.getText().toString();
                resetTagGroup(layoutTags);
                tv.setSelected(true);
                adapter.filter(keyword, currentTag, buildEffectiveStatuses());
            });
        }
    }

    // ================= STATUS FILTER (MULTI) =================
    private void setupStatusFilter() {
        for (int i = 0; i < layoutStatusFilter.getChildCount(); i++) {
            View v = layoutStatusFilter.getChildAt(i);
            if (!(v instanceof TextView)) continue;

            TextView tv = (TextView) v;
            String status = tv.getText().toString();

            if ("ALL".equalsIgnoreCase(status)) {
                tv.setSelected(true);
                selectedStatuses.clear();
            }

            tv.setOnClickListener(x -> {
                if ("ALL".equalsIgnoreCase(status)) {
                    selectedStatuses.clear();
                    resetTagGroup(layoutStatusFilter);
                    tv.setSelected(true);
                } else {
                    deselectTag(layoutStatusFilter, "ALL");

                    if (selectedStatuses.contains(status)) {
                        selectedStatuses.remove(status);
                        tv.setSelected(false);
                    } else {
                        selectedStatuses.add(status);
                        tv.setSelected(true);
                    }

                    if (selectedStatuses.isEmpty()) {
                        selectTag(layoutStatusFilter, "ALL");
                    }
                }

                adapter.filter(keyword, currentTag, buildEffectiveStatuses());
            });
        }
    }

    // ================= 🔥 STATUS LOGIC CORE =================
    private Set<String> buildEffectiveStatuses() {
        Set<String> result = new HashSet<>(selectedStatuses);

        boolean hasActive = result.contains("ACTIVE");
        boolean hasHidden = result.contains("HIDDEN");

        boolean hasStock =
                result.contains("OUT_OF_STOCK") ||
                        result.contains("LOW_STOCK") ||
                        result.contains("IN_STOCK");

        // 👉 chỉ chọn stock status → mặc định ALL status
        if (hasStock && !hasActive && !hasHidden) {
            result.add("ACTIVE");
            result.add("HIDDEN");
        }

        return result;
    }

    // ================= HELPERS =================
    private void resetTagGroup(LinearLayout group) {
        for (int i = 0; i < group.getChildCount(); i++) {
            View v = group.getChildAt(i);
            if (v instanceof TextView) v.setSelected(false);
        }
    }

    private void deselectTag(LinearLayout group, String text) {
        for (int i = 0; i < group.getChildCount(); i++) {
            View v = group.getChildAt(i);
            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                if (text.equalsIgnoreCase(tv.getText().toString())) {
                    tv.setSelected(false);
                }
            }
        }
    }

    private void selectTag(LinearLayout group, String text) {
        for (int i = 0; i < group.getChildCount(); i++) {
            View v = group.getChildAt(i);
            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                if (text.equalsIgnoreCase(tv.getText().toString())) {
                    tv.setSelected(true);
                }
            }
        }
    }

    // ================= ITEM CLICK =================
    private void setupItemClick() {
        GestureDetector detector = new GestureDetector(
                this,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override public boolean onSingleTapUp(MotionEvent e) {
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

                    @Override public void onTouchEvent(RecyclerView rv, MotionEvent e) {}
                    @Override public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}
                }
        );
    }

    // ================= SORT =================
    private void setupSort() {
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

            popupWindow.showAsDropDown(btnSort, -120, 8);
        });
    }

    // ================= NAV =================
    private void setupBottomNavigation() {
        navDashboard.setOnClickListener(v -> {
            startActivity(new Intent(
                    AdminProductActivity.this,
                    AdminMainActivity.class
            ));
        });
    }

    private void bindViews() {
        rvProducts = findViewById(R.id.rvProducts);
        edtSearch = findViewById(R.id.edtSearch);
        layoutTags = findViewById(R.id.layoutTags);
        layoutStatusFilter = findViewById(R.id.layoutStatusFilter);
        btnSort = findViewById(R.id.btnSort);
        navDashboard = findViewById(R.id.navDashboard);
        btnBack = findViewById(R.id.btnBack);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listener != null) listener.remove();
    }
}
