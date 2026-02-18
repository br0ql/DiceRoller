package com.example.diceroller.ui;

public class HistogramItem implements HistogramEntry {

    private final int value;
    private final int count;
    private boolean isSelected;

    public HistogramItem(int value, int count) {
        this.value = value;
        this.count = count;
        this.isSelected = false;
    }

    public int getValue() {
        return value;
    }

    public int getCount() {
        return count;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected() {
        isSelected = true;
    }

    public void setUnselected() {
        isSelected = false;
    }
}