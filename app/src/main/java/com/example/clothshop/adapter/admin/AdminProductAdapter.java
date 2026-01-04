package com.example.clothshop.adapter.admin;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.clothshop.R;
import com.example.clothshop.model.Product;
import com.example.clothshop.model.SortType;
import com.example.clothshop.model.Variant;
import com.example.clothshop.utils.CurrencyUtils;

import java.util.*;

public class AdminProductAdapter
        extends RecyclerView.Adapter<AdminProductAdapter.ProductViewHolder> {

    private final List<Product> productList;
    private final List<Product> displayList = new ArrayList<>();
    private SortType currentSort = SortType.NONE;

    // ===== SIZE ORDER =====
    private static final List<String> SIZE_ORDER =
            Arrays.asList("S", "M", "L", "XL", "XXL");

    private static final int LOW_STOCK_THRESHOLD = 30;

    public AdminProductAdapter(List<Product> productList) {
        this.productList = productList;
        displayList.addAll(productList);
    }

    // ================= FILTER & SORT =================
    public void filter(String keyword, String tag, Set<String> statuses) {
        displayList.clear();

        // --- GROUP 1: STATUS ---
        boolean filterActive = statuses.contains("ACTIVE");
        boolean filterHidden = statuses.contains("HIDDEN");

        // --- GROUP 2: STOCK ---
        boolean filterOut = statuses.contains("OUT_OF_STOCK");
        boolean filterLow = statuses.contains("LOW_STOCK");
        boolean filterIn  = statuses.contains("IN_STOCK");

        // --- GROUP 3: SALE  ---
        boolean filterOnSale = statuses.contains("ON_SALE");
        boolean filterNoSale = statuses.contains("NO_SALE");

        for (Product p : productList) {

            // 1. Search Logic
            if (keyword != null && !keyword.isEmpty()) {
                if (p.getName() == null || !p.getName().toLowerCase().contains(keyword.toLowerCase())) continue;
            }

            // 2. Tag Logic
            if (!"ALL".equalsIgnoreCase(tag)) {
                if (p.getTag() == null || !p.getTag().equalsIgnoreCase(tag)) continue;
            }

            // 3. Status Logic (AND)
            if (filterActive || filterHidden) {
                if (filterActive && filterHidden) {
                    // allow both
                } else if (filterActive) {
                    if (!"active".equalsIgnoreCase(p.getStatus())) continue;
                } else {
                    if (!"hidden".equalsIgnoreCase(p.getStatus())) continue;
                }
            }

            // 4. Stock Logic (AND)
            if (filterOut || filterLow || filterIn) {
                boolean hasOut = false, hasLow = false, hasIn = false;
                if (p.getVariants() != null) {
                    for (Variant v : p.getVariants()) {
                        int q = v.getQuantity();
                        if (q == 0) hasOut = true; else if (q < LOW_STOCK_THRESHOLD) hasLow = true; else hasIn = true;
                    }
                }
                if (filterOut && !hasOut) continue;
                if (filterLow && !hasLow) continue;
                if (filterIn && !hasIn) continue;
            }

            // 5. Sale Logic (AND)
            if (filterOnSale || filterNoSale) {
                boolean matchesSale = false;

                if (filterOnSale && p.isOnSale()) matchesSale = true;
                if (filterNoSale && !p.isOnSale()) matchesSale = true;
                if (!matchesSale) continue;
            }

            displayList.add(p);
        }
        applySort();
        notifyDataSetChanged();
    }

    private void applySort() {
        switch (currentSort) {
            case PRICE_ASC: displayList.sort(Comparator.comparingDouble(Product::getDisplayPrice)); break;
            case PRICE_DESC: displayList.sort((a, b) -> Double.compare(b.getDisplayPrice(), a.getDisplayPrice())); break;
            case NAME_ASC: displayList.sort((a, b) -> a.getName().compareToIgnoreCase(b.getName())); break;
            case NAME_DESC: displayList.sort((a, b) -> b.getName().compareToIgnoreCase(a.getName())); break;
            default: break;
        }
    }

    public void setSort(SortType sortType) {
        this.currentSort = sortType;
        applySort();
        notifyDataSetChanged();
    }

    // ================= VIEW =================
    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_product, parent, false);
        return new ProductViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder h, int pos) {
        Product p = displayList.get(pos);

        h.tvName.setText(p.getName());

        // ================= LOGIC SALE UI =================
        if (p.isOnSale()) {
            h.tvPrice.setText(CurrencyUtils.format(p.getDisplayPrice()));
            h.tvPrice.setTextColor(Color.parseColor("#D32F2F"));

            h.layoutSale.setVisibility(View.VISIBLE);

            h.tvSaleStatus.setText("ON SALE");
            h.tvSaleStatus.setBackgroundResource(R.drawable.bg_border_red);
            h.tvSaleStatus.setTextColor(Color.parseColor("#D32F2F"));

            Integer percent = p.getSalePercent();
            if (percent != null && percent > 0) {
                h.tvSalePercent.setVisibility(View.VISIBLE);
                h.tvSalePercent.setText("-" + percent + "%");
            } else {
                h.tvSalePercent.setVisibility(View.GONE);
            }

            h.tvSalePrice.setVisibility(View.VISIBLE);
            h.tvSalePrice.setText(CurrencyUtils.format(p.getPrice()));
            h.tvSalePrice.setPaintFlags(h.tvSalePrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        } else {
            h.tvPrice.setText(CurrencyUtils.format(p.getPrice()));
            h.tvPrice.setTextColor(Color.parseColor("#1C1C1E")); // Đen

            h.layoutSale.setVisibility(View.GONE);
            h.tvSalePrice.setPaintFlags(h.tvSalePrice.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        h.tvStock.setText("Stock: " + p.getTotalQuantity());
        h.tvRating.setText(p.getAverageRating() + " ★ (" + p.getReviewCount() + ")");

        if (p.getTag() != null) h.tvTag.setText(p.getTag().toUpperCase());

        if ("hidden".equalsIgnoreCase(p.getStatus())) {
            h.tvStatus.setText("HIDDEN");
            h.tvStatus.setBackgroundResource(R.drawable.bg_chip_red);
            h.tvStatus.setTextColor(Color.RED);
        } else {
            h.tvStatus.setText("ACTIVE");
            h.tvStatus.setBackgroundResource(R.drawable.bg_chip_green);
            h.tvStatus.setTextColor(Color.GREEN);
        }

        if (p.getImages() != null && !p.getImages().isEmpty()) {
            Glide.with(h.itemView.getContext()).load(p.getImages().get(0)).centerCrop().into(h.imgProduct);
        } else {
            h.imgProduct.setImageResource(R.drawable.ic_launcher_background);
        }

        // ===== VARIANTS =====
        h.layoutVariants.removeAllViews();
        if (p.getVariants() == null) p.setVariants(new ArrayList<>());

        Map<String, Map<String, Variant>> colorMap = new LinkedHashMap<>();
        for (Variant v : p.getVariants()) {
            if (v == null || v.getColor() == null || v.getSize() == null) continue;
            colorMap.computeIfAbsent(v.getColor(), k -> new HashMap<>()).put(v.getSize(), v);
        }

        for (String color : colorMap.keySet()) {
            h.layoutVariants.addView(createColorRow(h.itemView.getContext(), color, colorMap.get(color)));
        }
    }

    private View createColorRow(Context ctx, String color, Map<String, Variant> sizeMap) {
        LinearLayout row = new LinearLayout(ctx);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(0, 4, 0, 4);

        TextView tvColor = new TextView(ctx);
        tvColor.setText(color.toUpperCase());
        tvColor.setWidth(dp(ctx, 48));
        tvColor.setTextSize(11);
        tvColor.setTextColor(Color.parseColor("#1C1C1E"));
        row.addView(tvColor);

        LinearLayout sizeWrap = new LinearLayout(ctx);
        sizeWrap.setOrientation(LinearLayout.HORIZONTAL);

        for (String size : SIZE_ORDER) {
            Variant v = sizeMap.get(size);
            if (v == null) continue;

            TextView chip = new TextView(ctx);
            chip.setText(size);
            chip.setTextSize(10);
            chip.setPadding(10, 4, 10, 4);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-2, -2);
            lp.setMargins(4, 0, 4, 0);
            chip.setLayoutParams(lp);

            int qty = v.getQuantity();
            if (qty == 0) {
                chip.setBackgroundResource(R.drawable.bg_size_red);
                chip.setTextColor(Color.WHITE);
            } else if (qty < LOW_STOCK_THRESHOLD) {
                chip.setBackgroundResource(R.drawable.bg_chip_yellow);
                chip.setTextColor(Color.BLACK);
            } else {
                chip.setBackgroundResource(R.drawable.bg_size_green);
                chip.setTextColor(Color.WHITE);
            }
            sizeWrap.addView(chip);
        }
        row.addView(sizeWrap);
        return row;
    }

    private int dp(Context c, int v) {
        return (int) (v * c.getResources().getDisplayMetrics().density);
    }

    @Override
    public int getItemCount() {
        return displayList.size();
    }

    public Product getItemAt(int position) {
        if (position < 0 || position >= displayList.size()) return null;
        return displayList.get(position);
    }

    // ================= VIEW HOLDER =================
    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvName, tvPrice, tvStock, tvRating, tvTag, tvStatus;
        LinearLayout layoutSale;
        TextView tvSaleStatus, tvSalePercent, tvSalePrice;
        LinearLayout layoutVariants;

        ProductViewHolder(@NonNull View v) {
            super(v);
            imgProduct = v.findViewById(R.id.imgProduct);
            tvName = v.findViewById(R.id.tvName);
            tvPrice = v.findViewById(R.id.tvPrice);
            tvStock = v.findViewById(R.id.tvStock);
            tvRating = v.findViewById(R.id.tvRating);
            tvTag = v.findViewById(R.id.tvTag);
            tvStatus = v.findViewById(R.id.tvStatus);

            layoutSale = v.findViewById(R.id.layoutSale);
            tvSaleStatus = v.findViewById(R.id.tvSaleStatus);
            tvSalePercent = v.findViewById(R.id.tvSalePercent);
            tvSalePrice = v.findViewById(R.id.tvSalePrice);

            layoutVariants = v.findViewById(R.id.layoutVariants);
        }
    }
}