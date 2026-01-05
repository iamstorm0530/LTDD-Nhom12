package com.example.clothshop.adapter.cart;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clothshop.R;
import com.example.clothshop.model.CartItem;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> cartList;
    private OnCartChangeListener listener;

    // Interface để giao tiếp với Activity (cập nhật tổng tiền)
    public interface OnCartChangeListener {
        void onCartChanged(); // Gọi khi số lượng thay đổi hoặc xóa item
    }

    public CartAdapter(List<CartItem> cartList, OnCartChangeListener listener) {
        this.cartList = cartList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartList.get(position);

        holder.tvName.setText(item.getName());
        holder.tvPrice.setText("$" + item.getPrice());
        holder.tvSize.setText("Size: " + item.getSize());
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));
        holder.imgProduct.setImageResource(item.getImageResId());

        // Xử lý nút Cộng (+)
        holder.btnPlus.setOnClickListener(v -> {
            item.setQuantity(item.getQuantity() + 1);
            holder.tvQuantity.setText(String.valueOf(item.getQuantity()));
            listener.onCartChanged(); // Báo Activity tính lại tiền
        });

        // Xử lý nút Trừ (-)
        holder.btnMinus.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
                holder.tvQuantity.setText(String.valueOf(item.getQuantity()));
                listener.onCartChanged(); // Báo Activity tính lại tiền
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    // Hàm xóa item (Được gọi từ SwipeToDeleteCallback)
    public void removeItem(int position) {
        cartList.remove(position);
        notifyItemRemoved(position);
        listener.onCartChanged(); // Báo Activity tính lại tiền sau khi xóa
    }

    // Hàm hoàn tác (nếu bạn muốn làm chức năng Undo)
    public void restoreItem(CartItem item, int position) {
        cartList.add(position, item);
        notifyItemInserted(position);
        listener.onCartChanged();
    }

    public List<CartItem> getListData() {
        return cartList;
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvName, tvPrice, tvSize, tvQuantity, btnMinus, btnPlus;
        public ConstraintLayout viewForeground; // Để class Swipe xử lý
        public LinearLayout viewBackground;     // Để class Swipe xử lý

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvName = itemView.findViewById(R.id.tvProductName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvSize = itemView.findViewById(R.id.tvSize);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            btnPlus = itemView.findViewById(R.id.btnPlus);

            // 2 View quan trọng cho hiệu ứng vuốt
            viewForeground = itemView.findViewById(R.id.viewForeground);
            viewBackground = itemView.findViewById(R.id.viewBackground);
        }
    }
}
