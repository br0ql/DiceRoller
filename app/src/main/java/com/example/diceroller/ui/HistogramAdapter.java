package com.example.diceroller.ui;
import com.example.diceroller.R;

import com.google.android.material.color.MaterialColors;
import androidx.core.content.ContextCompat;


import androidx.recyclerview.widget.RecyclerView;
import android.animation.ValueAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;



import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;


public class HistogramAdapter extends RecyclerView.Adapter<HistogramAdapter.ViewHolder> {

    private List<HistogramItem> items = new ArrayList<>();
    private int maxCount = 0;

    public void setData(List<HistogramItem> newItems) {
        items = newItems;
        maxCount = 0;

        for (HistogramItem item : items) {
            if (item.count > maxCount) maxCount = item.count;
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_histogram, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(items.get(position), maxCount);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView valueText, countText;
        View barView;
        FrameLayout barContainer;

        ViewHolder(View itemView) {
            super(itemView);
            valueText = itemView.findViewById(R.id.valueText);
            countText = itemView.findViewById(R.id.countText);
            barView = itemView.findViewById(R.id.barView);
            barContainer = itemView.findViewById(R.id.barContainer);
        }

        void bind(HistogramItem item, int maxCount) {

            valueText.setText(String.valueOf(item.value));
            countText.setText(String.valueOf(item.count));

            int selectedColor = ContextCompat.getColor(
                    itemView.getContext(),
                    R.color.histogram_selected
            );

            int normalColor = MaterialColors.getColor(
                    itemView,
                    com.google.android.material.R.attr.colorPrimary
            );

            barView.setBackgroundColor(
                    item.isSelected ? selectedColor : normalColor
            );

            // Klik
            itemView.setOnClickListener(v -> {
                item.isSelected = !item.isSelected;
                barView.setBackgroundColor(
                        item.isSelected ? selectedColor : normalColor
                );
            });


            barContainer.post(() -> {

                int maxWidth = barContainer.getWidth();
                float percent = maxCount == 0 ? 0 :
                        (float) item.count / maxCount;

                int targetWidth = (int) (maxWidth * percent);

                ValueAnimator animator = ValueAnimator.ofInt(
                        barView.getLayoutParams().width,
                        targetWidth
                );

                animator.setDuration(400);
                animator.setInterpolator(new DecelerateInterpolator());

                animator.addUpdateListener(animation -> {
                    int value = (int) animation.getAnimatedValue();
                    ViewGroup.LayoutParams params = barView.getLayoutParams();
                    params.width = value;
                    barView.setLayoutParams(params);
                });

                animator.start();
            });
        }
    }

    public void clearSelection() {
        for (HistogramItem item : items) {
            item.isSelected = false;
        }
    }
}