package com.example.clothshop.adapter.admin;

import android.content.Context;
import android.graphics.Color;
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
            Arrays.asList("S", "M", "L");

    private static final int LOW_STOCK_THRESHOLD = 30;

    public AdminProductAdapter(List<Product> productList) {
        this.productList = productList;
        displayList.addAll(productList);
    }

    // ================= FILTER (🔥 FIX AND LOGIC) =================
    public void filter(String keyword, String tag, Set<String> statuses) {
        displayList.clear();

        boolean filterActive = statuses.contains("ACTIVE");
        boolean filterHidden = statuses.contains("HIDDEN");

        boolean filterOut = statuses.contains("OUT_OF_STOCK");
        boolean filterLow = statuses.contains("LOW_STOCK");
        boolean filterIn  = statuses.contains("IN_STOCK");

        for (Product p : productList) {

            // ===== SEARCH =====
            if (keyword != null && !keyword.isEmpty()) {
                if (p.getName() == null ||
                        !p.getName().toLowerCase()
                                .contains(keyword.toLowerCase())) {
                    continue;
                }
            }

            // ===== TAG (GENDER) =====
            if (!"ALL".equalsIgnoreCase(tag)) {
                if (p.getTag() == null ||
                        !p.getTag().equalsIgnoreCase(tag)) {
                    continue;
                }
            }

            // ===== STATUS (ACTIVE / HIDDEN) =====
            if (filterActive || filterHidden) {
                if (filterActive && filterHidden) {
                    // both allowed → pass
                } else if (filterActive) {
                    if (!"active".equalsIgnoreCase(p.getStatus())) continue;
                } else {
                    if (!"hidden".equalsIgnoreCase(p.getStatus())) continue;
                }
            }

            // ===== STOCK STATUS (VARIANT-BASED) =====
            if (filterOut || filterLow || filterIn) {

                boolean hasOut = false;
                boolean hasLow = false;
                boolean hasIn  = false;

                if (p.getVariants() != null) {
                    for (Variant v : p.getVariants()) {
                        int q = v.getQuantity();

                        if (q == 0) hasOut = true;
                        else if (q < LOW_STOCK_THRESHOLD) hasLow = true;
                        else hasIn = true;
                    }
                }

                // 🔥 AND tuyệt đối
                if (filterOut && !hasOut) continue;
                if (filterLow && !hasLow) continue;
                if (filterIn  && !hasIn)  continue;
            }

            displayList.add(p);
        }

        applySort();
        notifyDataSetChanged();
    }

    // ================= SORT =================
    private void applySort() {
        switch (currentSort) {
            case PRICE_ASC:
                displayList.sort(Comparator.comparingDouble(Product::getPrice));
                break;

            case PRICE_DESC:
                displayList.sort((a, b) ->
                        Double.compare(b.getPrice(), a.getPrice()));
                break;

            case NAME_ASC:
                displayList.sort((a, b) ->
                        a.getName().compareToIgnoreCase(b.getName()));
                break;

            case NAME_DESC:
                displayList.sort((a, b) ->
                        b.getName().compareToIgnoreCase(a.getName()));
                break;

            case NONE:
            default:
                break;
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
    public ProductViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_product, parent, false);
        return new ProductViewHolder(v);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ProductViewHolder h, int pos) {

        Product p = displayList.get(pos);

        h.tvName.setText(p.getName());
        h.tvPrice.setText(CurrencyUtils.format(p.getPrice()));
        h.tvStock.setText("Stock: " + p.getTotalQuantity());
        h.tvRating.setText(
                p.getAverageRating() + " ★ (" + p.getReviewCount() + ")"
        );

        if (p.getTag() != null) {
            h.tvTag.setText(p.getTag().toUpperCase());
        }

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
            Glide.with(h.itemView.getContext())
                    .load(p.getImages().get(0))
                    .centerCrop()
                    .into(h.imgProduct);
        }

        h.layoutVariants.removeAllViews();

        if (p.getVariants() == null) {
            p.setVariants(new ArrayList<>());
        }

        Map<String, Map<String, Variant>> colorMap = new LinkedHashMap<>();

        for (Variant v : p.getVariants()) {
            if (v == null) continue;
            if (v.getColor() == null || v.getSize() == null) continue;

            colorMap
                    .computeIfAbsent(v.getColor(), k -> new HashMap<>())
                    .put(v.getSize(), v);
        }

        for (String color : colorMap.keySet()) {
            h.layoutVariants.addView(
                    createColorRow(
                            h.itemView.getContext(),
                            color,
                            colorMap.get(color)
                    )
            );
        }
    }

    // ================= COLOR ROW =================
    private View createColorRow(
            Context ctx,
            String color,
            Map<String, Variant> sizeMap) {

        LinearLayout row = new LinearLayout(ctx);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(0, 6, 0, 6);

        TextView tvColor = new TextView(ctx);
        tvColor.setText(color.toUpperCase());
        tvColor.setWidth(dp(ctx, 48));
        tvColor.setTextSize(12);
        tvColor.setTextColor(Color.parseColor("#1C1C1E"));
        row.addView(tvColor);

        LinearLayout sizeWrap = new LinearLayout(ctx);
        sizeWrap.setOrientation(LinearLayout.HORIZONTAL);

        for (String size : SIZE_ORDER) {
            Variant v = sizeMap.get(size);
            if (v == null) continue;

            TextView chip = new TextView(ctx);
            chip.setText(size);
            chip.setTextSize(11);
            chip.setPadding(12, 6, 12, 6);

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
        return (int) (v * c.getResources()
                .getDisplayMetrics().density);
    }

    @Override
    public int getItemCount() {
        return displayList.size();
    }

    // ================= VIEW HOLDER =================
    static class ProductViewHolder extends RecyclerView.ViewHolder {

        ImageView imgProduct;
        TextView tvName, tvPrice, tvStock, tvRating, tvTag, tvStatus;
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
            layoutVariants = v.findViewById(R.id.layoutVariants);
        }
    }

    // ================= GET ITEM =================
    public Product getItemAt(int position) {
        if (position < 0 || position >= displayList.size()) return null;
        return displayList.get(position);
    }
}
