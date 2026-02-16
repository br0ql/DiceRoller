package com.example.diceroller.ui;

public class HistogramInfo implements HistogramEntry {
    public boolean isInfoRow;
    public String infoText;

    public HistogramInfo(String infoText) {
        this.isInfoRow = true;
        this.infoText = infoText;
    }
}
