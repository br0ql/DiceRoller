package com.example.diceroller.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class HistogramView extends View {

    private int[] counts;

    private Paint barPaint;
    private Paint axisPaint;
    private Paint textPaint;

    public HistogramView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        axisPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        barPaint.setColor(Color.parseColor("#4F46E5")); // indigo
        axisPaint.setColor(Color.LTGRAY);
        axisPaint.setStrokeWidth(2f);

        textPaint.setColor(Color.DKGRAY);
        textPaint.setTextSize(28f);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void setData(int[] counts) {
        this.counts = counts;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
