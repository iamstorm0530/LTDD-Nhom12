package com.example.clothshop.utils;

import android.content.Context;
import android.graphics.Color;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.clothshop.R;
import com.example.clothshop.model.Variant;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class VariantUIFactory {

    private static final List<String> SIZE_ORDER =
            Arrays.asList("S", "M", "L");

    public static LinearLayout createColorRow(
            Context ctx,
            String color,
            Map<String, Variant> sizeMap
    ) {
        LinearLayout row = new LinearLayout(ctx);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(0, 6, 0, 6);

        // COLOR NAME
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
            } else if (qty < 30) {
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

    private static int dp(Context c, int v) {
        return (int) (v * c.getResources()
                .getDisplayMetrics().density);
    }
}
