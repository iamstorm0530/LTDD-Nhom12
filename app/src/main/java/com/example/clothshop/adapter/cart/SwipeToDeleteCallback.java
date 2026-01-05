package com.example.clothshop.adapter.cart;

import android.graphics.Canvas;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {

    private CartAdapter mAdapter;

    public SwipeToDeleteCallback(CartAdapter adapter) {
        // 0: Không hỗ trợ Drag (kéo thả đổi vị trí)
        // LEFT: Chỉ hỗ trợ vuốt sang TRÁI
        super(0, ItemTouchHelper.LEFT);
        this.mAdapter = adapter;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false; // Không dùng chức năng move
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        // Khi vuốt xong -> Gọi hàm xóa bên Adapter
        int position = viewHolder.getAdapterPosition();
        mAdapter.removeItem(position);
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                            @NonNull RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {

        // Lấy lớp Foreground (màu trắng) để di chuyển
        View foregroundView = ((CartAdapter.CartViewHolder) viewHolder).viewForeground;

        // Hàm này của Android hỗ trợ vẽ UI mặc định
        getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (viewHolder != null) {
            View foregroundView = ((CartAdapter.CartViewHolder) viewHolder).viewForeground;
            getDefaultUIUtil().onSelected(foregroundView);
        }
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        View foregroundView = ((CartAdapter.CartViewHolder) viewHolder).viewForeground;
        getDefaultUIUtil().clearView(foregroundView);
    }
}
