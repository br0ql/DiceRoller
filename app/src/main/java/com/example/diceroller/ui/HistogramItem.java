package com.example.diceroller.ui;

public class HistogramItem {

    public final int value;
    public final int count;
    public boolean isSelected;

    public HistogramItem(int value, int count) {
        this.value = value;
        this.count = count;
        this.isSelected = false;
    }
}