package com.example.clothshop.Home;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.clothshop.R;
import com.example.clothshop.model.Product;
import com.example.clothshop.model.Variant;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProductDetailActivity extends AppCompatActivity {

    // ===== STATE =====
    private boolean isExpanded = false;
    private String selectedSize = null;
    private String selectedColor = null;
    private Variant selectedVariant = null;

    // ===== VIEW =====
    private TextView tvTotalPrice;
    private EditText edtQuantity;
    private TextView btnPlus, btnMinus;
    private LinearLayout layoutSizes;
    private Spinner spinnerColor;

    // ===== DATA =====
    private double pricePerItem;
    private TextView tvStock;
    private final List<Variant> variants = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        // ===== GET PRODUCT =====
        Product product = (Product) getIntent().getSerializableExtra("product");
        if (product == null) {
            finish();
            return;
        }

        // ===== FIND VIEW =====
        ViewPager2 viewPagerImages = findViewById(R.id.viewPagerImages);
        TextView tvTag = findViewById(R.id.tvTag);
        TextView tvName = findViewById(R.id.tvName);
        TextView tvDescription = findViewById(R.id.tvDescription);
        TextView tvReadMore = findViewById(R.id.tvReadMore);
        Button btnAddToCart = findViewById(R.id.btnAddToCart);

        layoutSizes = findViewById(R.id.layoutSizes);
        spinnerColor = findViewById(R.id.spinnerColor);

        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        edtQuantity = findViewById(R.id.edtQuantity);
        btnPlus = findViewById(R.id.btnPlus);
        btnMinus = findViewById(R.id.btnMinus);

        // ===== BASIC DATA =====
        tvTag.setText(product.getTag());
        tvName.setText(product.getName());
        tvDescription.setText(product.getDescription());

        pricePerItem = product.getPrice();
        edtQuantity.setText("1");
        updateTotalPrice(1);

        viewPagerImages.setAdapter(
                new ImagePagerAdapter(this, product.getImages())
        );

        // Stock
        tvStock = findViewById(R.id.tvStock);


        // ===== READ MORE =====
        tvReadMore.setOnClickListener(v -> {
            if (!isExpanded) {
                tvDescription.setMaxLines(Integer.MAX_VALUE);
                tvDescription.setEllipsize(null);
                tvReadMore.setText("Read less");
            } else {
                tvDescription.setMaxLines(3);
                tvDescription.setEllipsize(TextUtils.TruncateAt.END);
                tvReadMore.setText("Read more");
            }
            isExpanded = !isExpanded;
        });

        // ===== BACK =====
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // ===== LOAD VARIANTS FROM FIRESTORE =====
        loadVariants(product.getId());

        // ===== QUANTITY =====
        btnPlus.setOnClickListener(v -> {
            int qty = Integer.parseInt(edtQuantity.getText().toString());
            qty++;
            edtQuantity.setText(String.valueOf(qty));
            updateTotalPrice(qty);
        });

        btnMinus.setOnClickListener(v -> {
            int qty = Integer.parseInt(edtQuantity.getText().toString());
            if (qty > 1) {
                qty--;
                edtQuantity.setText(String.valueOf(qty));
                updateTotalPrice(qty);
            }
        });

        // ===== ADD TO CART =====
        btnAddToCart.setOnClickListener(v -> {
            if (selectedVariant == null) {
                Toast.makeText(this, "Vui lòng chọn size và màu", Toast.LENGTH_SHORT).show();
                return;
            }
            // TODO: add selectedVariant + quantity vào cart
        });
    }

    // ===== LOAD VARIANTS =====
    private void loadVariants(String productId) {
        FirebaseFirestore.getInstance()
                .collection("products")
                .document(productId)
                .collection("variants")
                .get()
                .addOnSuccessListener(qs -> {
                    variants.clear();

                    Set<String> sizeSet = new HashSet<>();
                    Set<String> colorSet = new HashSet<>();

                    for (DocumentSnapshot doc : qs.getDocuments()) {
                        Variant v = doc.toObject(Variant.class);
                        if (v != null) {
                            v.setId(doc.getId());
                            variants.add(v);

                            if (v.getSize() != null) sizeSet.add(v.getSize());
                            if (v.getColor() != null) colorSet.add(v.getColor());
                        }
                    }

                    renderSizes(sizeSet);
                    renderColors(colorSet);
                });
    }

    // ===== RENDER SIZE =====
    private void renderSizes(Set<String> sizeSet) {
        layoutSizes.removeAllViews();
        if (sizeSet.isEmpty()) return;

        for (String size : sizeSet) {
            Button btnSize = new Button(this);
            btnSize.setText(size);
            btnSize.setAllCaps(false);
            btnSize.setTextSize(12);
            btnSize.setBackgroundColor(Color.parseColor("#EEEEEE"));

            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
            params.setMarginEnd(12);
            btnSize.setLayoutParams(params);

            btnSize.setOnClickListener(v -> {
                selectedSize = size;
                highlightSelectedSize(btnSize);
                resolveSelectedVariant();
            });

            layoutSizes.addView(btnSize);
        }
    }

    private void highlightSelectedSize(Button selectedBtn) {
        for (int i = 0; i < layoutSizes.getChildCount(); i++) {
            Button b = (Button) layoutSizes.getChildAt(i);
            b.setBackgroundColor(Color.parseColor("#EEEEEE"));
            b.setTextColor(Color.BLACK);
        }
        selectedBtn.setBackgroundColor(Color.parseColor("#222222"));
        selectedBtn.setTextColor(Color.WHITE);
    }

    // ===== RENDER COLOR =====
    private void renderColors(Set<String> colorSet) {
        if (colorSet.isEmpty()) return;

        List<String> colors = new ArrayList<>(colorSet);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                colors
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerColor.setAdapter(adapter);

        selectedColor = colors.get(0);
        resolveSelectedVariant();

        spinnerColor.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                selectedColor = colors.get(position);
                resolveSelectedVariant();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
    }

    // ===== FIND SELECTED VARIANT =====
    private void resolveSelectedVariant() {
        selectedVariant = null;
        for (Variant v : variants) {
            if (v.getSize() != null && v.getColor() != null &&
                    v.getSize().equals(selectedSize) &&
                    v.getColor().equals(selectedColor)) {
                selectedVariant = v;
                break;
            }
        }
        if (selectedVariant != null) {
            int stock = selectedVariant.getQuantity();
            tvStock.setText("Stock: " + stock);

            // Optional: đổi màu nếu hết hàng
            if (stock == 0) {
                tvStock.setTextColor(Color.RED);
            } else {
                tvStock.setTextColor(Color.parseColor("#888888"));
            }
        } else {
            tvStock.setText("Stock: -");
        }

    }

    // ===== PRICE =====
    private void updateTotalPrice(int quantity) {
        double total = pricePerItem * quantity;
        tvTotalPrice.setText(formatPrice(total));
    }

    private String formatPrice(double price) {
        return String.format("%,.0fđ", price).replace(',', '.');
    }
}
