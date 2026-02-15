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

    public interface OnSelectionChangedListener {
        void onSelectionChanged(int count);
    }

    private OnSelectionChangedListener selectionListener;

    public void setOnSelectionChangedListener(OnSelectionChangedListener listener) {
        this.selectionListener = listener;
    }

    private List<HistogramItem> items = new ArrayList<>();
    private int maxCount = 0;
    private boolean animateBars = true;

    public void setData(List<HistogramItem> newItems) {
        items = newItems;
        maxCount = 0;

        for (HistogramItem item : items) {
            if (item.count > maxCount) maxCount = item.count;
        }

        animateBars = true; // animuj tylko przy nowym histogramie
        notifyDataSetChanged();

        new android.os.Handler().postDelayed(() -> animateBars = false, 350);
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

    private int getSelectedCount() {
        int count = 0;
        for (HistogramItem item : items) {
            if (item.isSelected) count++;
        }
        return count;
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

            itemView.setOnClickListener(v -> {
                item.isSelected = !item.isSelected;

                barView.setBackgroundColor(
                        item.isSelected ? selectedColor : normalColor
                );

                if (selectionListener != null) {
                    selectionListener.onSelectionChanged(getSelectedCount());
                }
            });



            barContainer.post(() -> {

                int maxWidth = barContainer.getWidth();
                float percent = maxCount == 0 ? 0 :
                        (float) item.count / maxCount;

                int targetWidth = (int) (maxWidth * percent);

                ViewGroup.LayoutParams params = barView.getLayoutParams();

                if (animateBars) {

                    params.width = 0;
                    barView.setLayoutParams(params);

                    ValueAnimator animator = ValueAnimator.ofInt(0, targetWidth);
                    animator.setDuration(300);
                    animator.setInterpolator(new DecelerateInterpolator());

                    animator.addUpdateListener(animation -> {
                        params.width = (int) animation.getAnimatedValue();
                        barView.setLayoutParams(params);
                    });

                    animator.start();

                } else {
                    params.width = targetWidth;
                    barView.setLayoutParams(params);
                }
            });

        }
    }

    public void clearSelection() {
        for (HistogramItem item : items) {
            item.isSelected = false;
        }
    }

    public List<Integer> getSelectedValues() {

        List<Integer> selected = new ArrayList<>();

        for (HistogramItem item : items) {
            if (item.isSelected) {
                selected.add(item.value);
            }
        }

        return selected;
    }

}