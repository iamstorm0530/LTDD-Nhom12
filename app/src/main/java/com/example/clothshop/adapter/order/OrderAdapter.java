package com.example.clothshop.adapter.order;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clothshop.R;
import com.example.clothshop.activity.user.CartActivity;
import com.example.clothshop.activity.user.LeaveReviewActivity;
import com.example.clothshop.activity.user.OrderDetailActivity; // Sẽ tạo sau
import com.example.clothshop.activity.user.ViewReviewActivity;
import com.example.clothshop.model.Order;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Order> list;
    private Context context;

    public OrderAdapter(Context context, List<Order> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order item = list.get(position);

        holder.tvName.setText(item.getProductName());
        holder.tvDetail.setText("Size: " + item.getProductSize() + " | Qty: " + item.getQuantity());
        holder.tvPrice.setText("$" + item.getTotalPrice());
        holder.imgProduct.setImageResource(item.getImageResId());

        // Xử lý nút bấm theo Status
        switch (item.getStatus()) {
            case "Active":
                holder.btnAction.setText("Track Order");
                holder.btnAction.setOnClickListener(v -> {
                    // Mở màn hình Track Order (OrderDetail)
                    Intent intent = new Intent(context, OrderDetailActivity.class);
                    // Truyền ID đơn hàng để bên kia biết load đơn nào
                    intent.putExtra("order_id", item.getId());
                    context.startActivity(intent);
                });
                break;

            case "Completed":
                // Logic giả lập: Nếu ID chứa số 3 thì coi như ĐÃ ĐÁNH GIÁ (demo View Review)
                // Còn lại thì coi như CHƯA ĐÁNH GIÁ (demo Leave Review)

                if (item.getId().contains("3")) {
                    // TRƯỜNG HỢP: ĐÃ ĐÁNH GIÁ
                    holder.btnAction.setText("View Review");
                    holder.btnAction.setOnClickListener(v -> {
                        Intent intent = new Intent(context, ViewReviewActivity.class);
                        intent.putExtra("product_name", item.getProductName());
                        intent.putExtra("product_price", item.getTotalPrice());

                        // THÊM DÒNG NÀY: Truyền ngày đánh giá (Giả lập)
                        intent.putExtra("review_date", "12/10/2023");

                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    });

                } else {
                    // TRƯỜNG HỢP: CHƯA ĐÁNH GIÁ
                    holder.btnAction.setText("Leave Review");
                    holder.btnAction.setOnClickListener(v -> {
                        Intent intent = new Intent(context, LeaveReviewActivity.class);
                        intent.putExtra("product_name", item.getProductName());
                        intent.putExtra("product_price", item.getTotalPrice());
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    });
                }
                break;

            case "Cancelled":
                holder.btnAction.setText("Re-Order");
                holder.btnAction.setOnClickListener(v -> {
                    // Logic Re-Order: Thêm vào giỏ và nhảy về Cart
                    Toast.makeText(context, "Added to Cart!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, CartActivity.class);
                    context.startActivity(intent);
                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvName, tvDetail, tvPrice;
        Button btnAction;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvName = itemView.findViewById(R.id.tvName);
            tvDetail = itemView.findViewById(R.id.tvDetail);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            btnAction = itemView.findViewById(R.id.btnAction);
        }
    }
}