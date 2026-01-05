package com.example.clothshop.adapter.checkout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clothshop.R;
import com.example.clothshop.model.CartItem;

import java.util.List;

public class CheckoutAdapter extends RecyclerView.Adapter<CheckoutAdapter.CheckoutViewHolder> {

    private List<CartItem> list;

    public CheckoutAdapter(List<CartItem> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public CheckoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_checkout, parent, false);
        return new CheckoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckoutViewHolder holder, int position) {
        CartItem item = list.get(position);
        holder.tvName.setText(item.getName());
        holder.tvPrice.setText("$" + item.getPrice());
        // Hiển thị gộp Size và Qty
        holder.tvSizeQty.setText("Size: " + item.getSize() + "  |  Qty: " + item.getQuantity());
        holder.imgProduct.setImageResource(item.getImageResId());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class CheckoutViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvName, tvSizeQty, tvPrice;

        public CheckoutViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvName = itemView.findViewById(R.id.tvName);
            tvSizeQty = itemView.findViewById(R.id.tvSizeQty);
            tvPrice = itemView.findViewById(R.id.tvPrice);
        }
    }
}
