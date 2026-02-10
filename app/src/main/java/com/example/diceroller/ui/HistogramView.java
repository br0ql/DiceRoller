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
        textPaint.setTextAlign(Paint.Align.RIGHT);
    }

    public void setData(int[] counts) {
        this.counts = counts;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (counts == null || counts.length == 0) return;

        int width = getWidth();
        int height = getHeight();

        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();

        float availableWidth = width - paddingLeft - paddingRight;
        float availableHeight = height - paddingTop - paddingBottom;

        int barCount = counts.length;
        int maxCount = getMaxCount();
        if (maxCount == 0) return;

        float rowHeight = availableHeight / barCount;
        float startX = paddingLeft;

        for (int i = 1; i < barCount; i++) {
            float value = counts[i];
            float barLength = (value / maxCount) * (availableWidth * 0.9f);

            float top = paddingTop + i * rowHeight;
            float bottom = top + rowHeight * 0.7f;

            float left = startX;
            float right = left + barLength;

            // bar
            canvas.drawRect(left, top, right, bottom, barPaint);

            // label (1..N) po lewej
            canvas.drawText(
                    String.valueOf(i),
                    paddingLeft - 16,
                    bottom - 8,
                    textPaint
            );

            // wartosc kostki w srodku bara
            canvas.drawText(
                    String.valueOf(counts[i]) + " rolls",
                    paddingLeft + 120,
                    bottom - 8,
                    textPaint
            );
        }
    }


    private int getMaxCount() {
        int max = 0;
        for (int c : counts) {
            if (c > max) max = c;
        }
        return max;
    }


}
