package com.example.clothshop.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clothshop.R;
import com.example.clothshop.adapter.order.OrderAdapter;
import com.example.clothshop.model.Order;

import java.util.ArrayList;
import java.util.List;

public class OrderListFragment extends Fragment {

    private String statusType;
    private RecyclerView rvOrders;

    // Hàm tạo Fragment kèm tham số Status
    public static OrderListFragment newInstance(String status) {
        OrderListFragment fragment = new OrderListFragment();
        Bundle args = new Bundle();
        args.putString("status", status);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            statusType = getArguments().getString("status");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_list, container, false);

        rvOrders = view.findViewById(R.id.rvOrders); // Bạn cần tạo file xml cho fragment này chứa 1 RecyclerView
        rvOrders.setLayoutManager(new LinearLayoutManager(getContext()));

        // Fake dữ liệu theo status
        List<Order> list = getFakeData(statusType);

        OrderAdapter adapter = new OrderAdapter(getContext(), list);
        rvOrders.setAdapter(adapter);

        return view;
    }

    // Hàm fake data
    private List<Order> getFakeData(String status) {
        List<Order> list = new ArrayList<>();
        // Ảnh placeholder
        int img = R.drawable.ic_launcher_background;

        if (status.equals("Active")) {
            list.add(new Order("1", "Brown Jacket", "XL", 1, 83.97, "Active", img));
            list.add(new Order("2", "Brown Suite", "M", 2, 120.0, "Active", img));
        } else if (status.equals("Completed")) {
            list.add(new Order("3", "Black Shirt", "L", 1, 50.0, "Completed", img));
        } else {
            list.add(new Order("4", "Red Dress", "S", 1, 90.0, "Cancelled", img));
        }
        return list;
    }
}
