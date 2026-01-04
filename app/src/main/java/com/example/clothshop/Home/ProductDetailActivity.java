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

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.clothshop.R;
import com.example.clothshop.model.Product;

public class ProductDetailActivity extends AppCompatActivity {

    // ====== STATE ======
    private boolean isExpanded = false;
    private String selectedSize = null;
    private String selectedColor = "";

    // ====== VIEW ======
    private TextView tvTotalPrice;
    private EditText edtQuantity;
    private TextView btnPlus, btnMinus;

    // ====== DATA ======
    private double pricePerItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        // ====== GET PRODUCT ======
        Product product = (Product) getIntent().getSerializableExtra("product");
        if (product == null) {
            finish();
            return;
        }

        // ====== FIND VIEW ======
        ViewPager2 viewPagerImages = findViewById(R.id.viewPagerImages);
        TextView tvTag = findViewById(R.id.tvTag);
        TextView tvName = findViewById(R.id.tvName);
        TextView tvDescription = findViewById(R.id.tvDescription);
        TextView tvReadMore = findViewById(R.id.tvReadMore);
        LinearLayout layoutSizes = findViewById(R.id.layoutSizes);
        Spinner spinnerColor = findViewById(R.id.spinnerColor);
        Button btnAddToCart = findViewById(R.id.btnAddToCart);

        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        edtQuantity = findViewById(R.id.edtQuantity);
        btnPlus = findViewById(R.id.btnPlus);
        btnMinus = findViewById(R.id.btnMinus);

        // ====== SET BASIC DATA ======
        tvTag.setText(product.tag);
        tvName.setText(product.name);
        tvDescription.setText(product.description);

        pricePerItem = product.getPrice();
        edtQuantity.setText("1");
        updateTotalPrice(1);

        viewPagerImages.setAdapter(new ImagePagerAdapter(this, product.images));

        // ====== READ MORE ======
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

        // ====== BACK ======
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // ====== SIZE ======
        if (product.sizes != null) {
            for (String size : product.sizes) {
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
                    for (int i = 0; i < layoutSizes.getChildCount(); i++) {
                        Button child = (Button) layoutSizes.getChildAt(i);
                        child.setBackgroundColor(Color.parseColor("#EEEEEE"));
                        child.setTextColor(Color.BLACK);
                    }

                    btnSize.setBackgroundColor(Color.parseColor("#222222"));
                    btnSize.setTextColor(Color.WHITE);
                    selectedSize = size;
                });

                layoutSizes.addView(btnSize);
            }
        }

        // ====== COLOR ======
        if (product.colors != null && !product.colors.isEmpty()) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_spinner_item,
                    product.colors
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerColor.setAdapter(adapter);

            selectedColor = product.colors.get(0);
            spinnerColor.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                    selectedColor = product.colors.get(position);
                }

                @Override
                public void onNothingSelected(android.widget.AdapterView<?> parent) {}
            });
        }

        // ====== QUANTITY ======
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
    }

    // ====== UPDATE TOTAL ======
    private void updateTotalPrice(int quantity) {
        double total = pricePerItem * quantity;
        tvTotalPrice.setText(formatPrice(total));
    }

    private String formatPrice(double price) {
        return String.format("%,.0fđ", price).replace(',', '.');
    }
}
