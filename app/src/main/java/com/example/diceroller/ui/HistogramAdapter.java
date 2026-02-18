package com.example.diceroller.ui;

import com.example.diceroller.R;

import com.google.android.material.color.MaterialColors;
import com.google.android.material.card.MaterialCardView;
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

public class HistogramAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_BAR = 0;
    private static final int TYPE_INFO = 1;

    public interface OnSelectionChangedListener {
        void onSelectionChanged(int count);
    }

    private OnSelectionChangedListener selectionListener;
    private java.util.Map<Integer, Integer> increments = new java.util.HashMap<>();


    public void setOnSelectionChangedListener(OnSelectionChangedListener listener) {
        this.selectionListener = listener;
    }

    private final List<HistogramItem> bars = new ArrayList<>();
    private final List<HistogramInfo> history = new ArrayList<>();


    private int maxCount = 0;
    private boolean animateBars = true;

    // =========================
    // DATA
    // =========================

    public void setData(List<HistogramItem> newItems) {

        bars.clear();
        bars.addAll(newItems);

        maxCount = 0;
        for (HistogramItem item : bars) {
            if (item.getCount() > maxCount) maxCount = item.getCount();
        }

        animateBars = true;
        notifyDataSetChanged();

        new android.os.Handler(android.os.Looper.getMainLooper())
                .postDelayed(() -> animateBars = false, 350);

    }

    public void clearHistory() {
        int historySize = history.size();
        if (historySize == 0) return;

        history.clear();
        notifyItemRangeRemoved(bars.size(), historySize);
    }


    public void addInfoMessage(String message) {
        history.add(new HistogramInfo(message));
        notifyItemInserted(bars.size() + history.size() - 1);
    }

    public void setIncrements(java.util.Map<Integer, Integer> inc) {
        this.increments = inc;
        notifyItemRangeChanged(0, bars.size());
    }


    public void clearIncrements() {
        if (increments.isEmpty()) return;

        increments.clear();
        notifyItemRangeChanged(0, bars.size());
    }



    // =========================
    // RECYCLER CORE
    // =========================

    @Override
    public int getItemCount() {
        return bars.size() + history.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position < bars.size()) {
            return TYPE_BAR;
        }
        return TYPE_INFO;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == TYPE_INFO) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_histogram_info, parent, false);
            return new InfoViewHolder(view);
        }

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_histogram, parent, false);
        return new BarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (position < bars.size()) {
            ((BarViewHolder) holder).bind(bars.get(position));
        } else {
            ((InfoViewHolder) holder)
                    .bind(history.get(position - bars.size()));
        }
    }

    // =========================
    // BAR VIEW HOLDER
    // =========================

    class BarViewHolder extends RecyclerView.ViewHolder {

        TextView valueText, countText;
        View barView;
        FrameLayout barContainer;
        MaterialCardView valueCard;


        BarViewHolder(View itemView) {
            super(itemView);
            valueText = itemView.findViewById(R.id.valueText);
            countText = itemView.findViewById(R.id.countText);
            barView = itemView.findViewById(R.id.barView);
            barContainer = itemView.findViewById(R.id.barContainer);
            valueCard = itemView.findViewById(R.id.valueCard);
        }

        void bind(HistogramItem item) {

            valueText.setText(String.valueOf(item.getValue()));
            Integer incObj = increments.get(item.getValue());
            int inc = incObj != null ? incObj : 0;


            if (inc > 0) {
                countText.setText("(+" + inc + ") " + item.getCount());
            } else {
                countText.setText(String.valueOf(item.getCount()));
            }


            int selectedColor = ContextCompat.getColor(
                    itemView.getContext(),
                    R.color.histogram_selected
            );

            int normalColor = MaterialColors.getColor(
                    itemView,
                    com.google.android.material.R.attr.colorPrimary
            );

            barView.setBackgroundColor(
                    item.isSelected() ? selectedColor : normalColor
            );
            valueCard.setCardBackgroundColor(
                    item.isSelected() ? selectedColor : normalColor
            );

            itemView.setOnClickListener(v -> {

                if (item.isSelected()) {
                    item.setUnselected();
                } else {
                    item.setSelected();
                }

                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    notifyItemChanged(pos);
                }

                if (selectionListener != null) {
                    selectionListener.onSelectionChanged(getSelectedCount());
                }
            });


            barContainer.post(() -> {

                int maxWidth = barContainer.getWidth();
                float percent = maxCount == 0 ? 0 :
                        (float) item.getCount() / maxCount;

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

    // =========================
    // INFO VIEW HOLDER
    // =========================

    class InfoViewHolder extends RecyclerView.ViewHolder {

        TextView infoText;

        InfoViewHolder(View itemView) {
            super(itemView);
            infoText = itemView.findViewById(R.id.infoText);
        }

        void bind(HistogramInfo item) {
            infoText.setText(item.infoText);
        }
    }

    // =========================
    // SELECTION HELPERS
    // =========================

    private int getSelectedCount() {
        int count = 0;
        for (HistogramItem item : bars) {
            if (item.isSelected()) count++;
        }
        return count;
    }

    public void clearSelection() {
        for (HistogramItem item : bars) {
            item.setUnselected();
        }
        notifyDataSetChanged();
    }

    public List<Integer> getSelectedValues() {
        List<Integer> selected = new ArrayList<>();
        for (HistogramItem item : bars) {
            if (item.isSelected()) {
                selected.add(item.getValue());
            }
        }
        return selected;
    }
}
