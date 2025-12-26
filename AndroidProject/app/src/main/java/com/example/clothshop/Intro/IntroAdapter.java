package com.example.clothshop.Intro;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clothshop.R;

import java.util.List;

public class IntroAdapter extends RecyclerView.Adapter<IntroAdapter.IntroViewHolder> {

    private List<IntroItem> list;

    public IntroAdapter(List<IntroItem> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public IntroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_intro, parent, false);
        return new IntroViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IntroViewHolder holder, int position) {
        IntroItem item = list.get(position);
        holder.imgIntro.setImageResource(item.getImage());
        holder.tvTitle.setText(item.getTitle());
        holder.tvDesc.setText(item.getDesc());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class IntroViewHolder extends RecyclerView.ViewHolder {

        ImageView imgIntro;
        TextView tvTitle, tvDesc;

        public IntroViewHolder(@NonNull View itemView) {
            super(itemView);
            imgIntro = itemView.findViewById(R.id.imgIntro);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDesc = itemView.findViewById(R.id.tvDesc);
        }
    }
}
