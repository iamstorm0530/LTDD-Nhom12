package com.example.clothshop.activity.admin;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.clothshop.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdminMainActivity extends AppCompatActivity {

    // ================= VIEW =================
    private PieChart pieOrderChart;
    private LineChart lineRevenueChart;
    private Spinner spinnerRevenue;

    private TextView tvTotalOrders, tvNewOrder, tvShippingOrder,
            tvCompletedOrder, tvCancelledOrder, tvRevenue, tvProductCount;

    private ImageView navProducts, btnBack;

    // ================= FIREBASE =================
    private FirebaseFirestore db;

    // ================= ORDER MOCK =================
    private final int newOrder = 120;
    private final int shipping = 340;
    private final int completed = 700;
    private final int cancelled = 88;

    // ================= REVENUE MOCK =================
    private final long REVENUE_DAY   = 1_789_000L;
    private final long REVENUE_MONTH = 30_500_000L;
    private final long REVENUE_YEAR  = 400_250_000L;

    // ================= CUSTOM DATE =================
    private Calendar fromDate, toDate;

    // ================= HOURS =================
    private final String[] HOURS = {
            "8h","9h","10h","11h","12h",
            "13h","14h","15h","16h",
            "17h","18h","19h","20h"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        db = FirebaseFirestore.getInstance();

        bindViews();

        setupOrderText();
        setupOrderPieChart();
        setupSpinnerRevenue();

        setupRevenueThisDay();
        setupOtherText(REVENUE_DAY);

        loadProductCount();
        setupProductClick();
        setupBottomNavigation();

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            finish();
        });
    }

    // ================= CLICK PRODUCT =================
    private void setupProductClick() {

        View.OnClickListener goProduct = v -> {
            Intent intent = new Intent(
                    AdminMainActivity.this,
                    AdminProductActivity.class
            );
            startActivity(intent);
        };
        View productBox = (View) tvProductCount.getParent();
        if (productBox != null) {
            productBox.setClickable(true);
            productBox.setFocusable(true);
            productBox.setOnClickListener(goProduct);
        }
        navProducts.setOnClickListener(goProduct);
    }

    // ================= LOAD PRODUCT COUNT =================
    private void loadProductCount() {
        db.collection("products")
                .get()
                .addOnSuccessListener(qs ->
                        tvProductCount.setText(String.valueOf(qs.size()))
                )
                .addOnFailureListener(e -> {
                    tvProductCount.setText("0");
                    e.printStackTrace();
                });
    }

    // ================= BIND =================
    private void bindViews() {
        pieOrderChart = findViewById(R.id.pieOrderChart);
        lineRevenueChart = findViewById(R.id.lineRevenueChart);
        spinnerRevenue = findViewById(R.id.spinnerRevenue);

        tvTotalOrders = findViewById(R.id.tvTotalOrders);
        tvNewOrder = findViewById(R.id.tvNewOrder);
        tvShippingOrder = findViewById(R.id.tvShippingOrder);
        tvCompletedOrder = findViewById(R.id.tvCompletedOrder);
        tvCancelledOrder = findViewById(R.id.tvCancelledOrder);
        tvRevenue = findViewById(R.id.tvRevenue);
        tvProductCount = findViewById(R.id.tvProductCount);

        navProducts = findViewById(R.id.navProducts);
    }

    // ================= SPINNER =================
    private void setupSpinnerRevenue() {
        spinnerRevenue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                switch (parent.getItemAtPosition(pos).toString()) {
                    case "This Day":
                        setupRevenueThisDay();
                        setupOtherText(REVENUE_DAY);
                        break;
                    case "This Month":
                        setupRevenueThisMonthByWeek();
                        setupOtherText(REVENUE_MONTH);
                        break;
                    case "This Year":
                        setupRevenueThisYear();
                        setupOtherText(REVENUE_YEAR);
                        break;
                    case "Custom":
                        openDateRangePicker();
                        break;
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }


    // ================= REVENUE =================
    private void setupRevenueThisDay() {
        List<Entry> entries = new ArrayList<>();
        int[] data = {
                80_000,120_000,150_000,180_000,210_000,
                190_000,160_000,170_000,180_000,
                160_000,140_000,120_000,129_000
        };

        for (int i = 0; i < HOURS.length; i++) {
            entries.add(new Entry(i, data[i]));
        }
        drawLineChart(entries, HOURS);
    }

    private void setupRevenueThisMonthByWeek() {
        List<Entry> entries = new ArrayList<>();
        String[] weeks = {"W1","W2","W3","W4"};
        int[] revenue = {7_200_000,8_100_000,8_900_000,6_300_000};

        for (int i = 0; i < weeks.length; i++) {
            entries.add(new Entry(i, revenue[i]));
        }
        drawLineChart(entries, weeks);
    }

    private void setupRevenueThisYear() {
        List<Entry> entries = new ArrayList<>();
        String[] months = {"Jan","Feb","Mar","Apr","May","Jun",
                "Jul","Aug","Sep","Oct","Nov","Dec"};

        int[] revenue = {
                25_000_000,28_000_000,30_000_000,32_000_000,
                35_000_000,34_000_000,33_000_000,36_000_000,
                38_000_000,40_000_000,36_000_000,33_000_000
        };

        for (int i = 0; i < months.length; i++) {
            entries.add(new Entry(i, revenue[i]));
        }
        drawLineChart(entries, months);
    }
    // ================= CUSTOM =================
    private void openDateRangePicker() {
        fromDate = Calendar.getInstance();
        toDate = Calendar.getInstance();

        new DatePickerDialog(this,
                (v, y, m, d) -> {
                    fromDate.set(y, m, d);
                    new DatePickerDialog(this,
                            (v2, y2, m2, d2) -> {
                                toDate.set(y2, m2, d2);
                                validateAndSetupCustom();
                            },
                            toDate.get(Calendar.YEAR),
                            toDate.get(Calendar.MONTH),
                            toDate.get(Calendar.DAY_OF_MONTH)
                    ).show();
                },
                fromDate.get(Calendar.YEAR),
                fromDate.get(Calendar.MONTH),
                fromDate.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void validateAndSetupCustom() {
        long days = (toDate.getTimeInMillis() - fromDate.getTimeInMillis())
                / (1000 * 60 * 60 * 24);

        if (days > 92) {
            Toast.makeText(this, "Custom range maximum 3 months", Toast.LENGTH_SHORT).show();
            setupRevenueThisMonthByWeek();
            setupOtherText(REVENUE_MONTH);
            return;
        }

        setupRevenueCustom();
        setupOtherText(18_650_000L);
    }
    private void setupRevenueCustom() {
        List<Entry> entries = new ArrayList<>();
        String[] labels = {"W1","W2","W3","W4","W5","W6"};
        int[] revenue = {2_800_000, 3_100_000, 3_400_000, 3_000_000, 3_200_000, 3_150_000};

        for (int i = 0; i < labels.length; i++) {
            entries.add(new Entry(i, revenue[i]));
        }
        drawLineChart(entries, labels);
    }

    // ================= CHART =================
    private void drawLineChart(List<Entry> entries, String[] labels) {
        LineDataSet dataSet = new LineDataSet(entries, "");
        dataSet.setColor(Color.parseColor("#2979FF"));
        dataSet.setLineWidth(2.5f);
        dataSet.setCircleRadius(4f);
        dataSet.setCircleColor(Color.parseColor("#2979FF"));
        dataSet.setDrawValues(false);

        lineRevenueChart.setData(new LineData(dataSet));

        XAxis xAxis = lineRevenueChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(labels.length, true);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int i = (int) value;
                return (i >= 0 && i < labels.length) ? labels[i] : "";
            }
        });

        YAxis left = lineRevenueChart.getAxisLeft();
        left.setAxisMinimum(0f);
        lineRevenueChart.getAxisRight().setEnabled(false);
        lineRevenueChart.getLegend().setEnabled(false);
        lineRevenueChart.getDescription().setEnabled(false);
        lineRevenueChart.invalidate();
    }

    // ================= PIE =================
    private void setupOrderPieChart() {
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(newOrder));
        entries.add(new PieEntry(shipping));
        entries.add(new PieEntry(completed));
        entries.add(new PieEntry(cancelled));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(
                Color.parseColor("#F4C430"),
                Color.parseColor("#2979FF"),
                Color.parseColor("#2ECC71"),
                Color.parseColor("#FF0000")
        );

        PieData pieData = new PieData(dataSet);
        pieData.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format(Locale.US, "%.1f%%", value);
            }
        });

        pieOrderChart.setData(pieData);
        pieOrderChart.setUsePercentValues(true);
        pieOrderChart.getLegend().setEnabled(false);
        pieOrderChart.getDescription().setEnabled(false);
        pieOrderChart.invalidate();
    }

    // ================= TEXT =================
    private void setupOrderText() {
        int total = newOrder + shipping + completed + cancelled;
        tvTotalOrders.setText(NumberFormat.getInstance().format(total));
        tvNewOrder.setText("● New • " + newOrder);
        tvShippingOrder.setText("● Shipping • " + shipping);
        tvCompletedOrder.setText("● Completed • " + completed);
        tvCancelledOrder.setText("● Cancelled • " + cancelled);
    }

    private void setupOtherText(long revenue) {
        tvRevenue.setText(formatCurrency(revenue));
    }
    private void setupBottomNavigation() {
        navProducts.setOnClickListener(v -> {
            Intent intent = new Intent(
                    AdminMainActivity.this,
                    AdminProductActivity.class
            );
            startActivity(intent);
        });
    }


    private String formatCurrency(long value) {
        double m = value / 1_000_000.0;
        return (m == Math.floor(m))
                ? String.format(Locale.US, "%.0fM ₫", m)
                : String.format(Locale.US, "%.2fM ₫", m);
    }
}
