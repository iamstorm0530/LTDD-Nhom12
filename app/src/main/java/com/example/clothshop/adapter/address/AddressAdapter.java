package com.example.clothshop.adapter.address;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clothshop.R;
import com.example.clothshop.model.Address;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {

    private List<Address> list;
    private int selectedPosition = 0; // Mặc định chọn cái đầu tiên

    public AddressAdapter(List<Address> list) {
        this.list = list;
    }

    // Hàm để lấy địa chỉ đang chọn (để Activity gọi khi bấm Apply)
    public Address getSelectedAddress() {
        if (selectedPosition >= 0 && selectedPosition < list.size()) {
            return list.get(selectedPosition);
        }
        return null;
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address, parent, false);
        return new AddressViewHolder(view);
    }

    // Thêm interface lắng nghe sự kiện xóa (để báo Activity biết)
    public interface OnAddressActionListener {
        void onAddressDeleted(int position);
    }
    private OnAddressActionListener actionListener;

    public AddressAdapter(List<Address> list, OnAddressActionListener listener) {
        this.list = list;
        this.actionListener = listener;
    }

    // Hàm xóa
    public void removeItem(int position) {
        list.remove(position);
        notifyItemRemoved(position);
        if (actionListener != null) {
            actionListener.onAddressDeleted(position);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        Address item = list.get(position);
        holder.tvTitle.setText(item.getTitle());
        holder.tvDetail.setText(item.getAddressDetails());
        holder.tvPhone.setText(item.getPhoneNumber());

        // Kiểm tra xem vị trí này có phải là vị trí đang chọn không
        holder.rbSelect.setChecked(position == selectedPosition);

        // Xử lý khi bấm vào cả dòng
        holder.itemView.setOnClickListener(v -> {
            selectedPosition = holder.getAdapterPosition();
            notifyDataSetChanged(); // Vẽ lại toàn bộ list để cập nhật dấu tick
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class AddressViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDetail, tvPhone; // Thêm tvPhone
        RadioButton rbSelect;
        public ConstraintLayout viewForeground; // Cho Swipe
        public LinearLayout viewBackground;     // Cho Swipe

        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDetail = itemView.findViewById(R.id.tvAddressDetail);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            rbSelect = itemView.findViewById(R.id.rbSelect);
            viewForeground = itemView.findViewById(R.id.viewForeground);
            viewBackground = itemView.findViewById(R.id.viewBackground);
        }
    }
}
