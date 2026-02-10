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
    private Paint labelPaint;
    private Paint valuePaint;

    public HistogramView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        axisPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        barPaint.setColor(Color.parseColor("#4F46E5")); // indigo
        axisPaint.setColor(Color.LTGRAY);
        axisPaint.setStrokeWidth(2f);

        labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        labelPaint.setColor(Color.DKGRAY);
        labelPaint.setTextSize(28f);
        labelPaint.setTextAlign(Paint.Align.RIGHT);

        valuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        valuePaint.setColor(Color.WHITE);
        valuePaint.setTextSize(26f);
        valuePaint.setTextAlign(Paint.Align.CENTER);

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
            Paint.FontMetrics fm = valuePaint.getFontMetrics();
            float textY = (top + bottom) / 2 - (fm.ascent + fm.descent) / 2;

            float left = startX;
            float right = left + barLength;

            // bar
            canvas.drawRect(left, top, right, bottom, barPaint);

            // label (1..N) po lewej
            canvas.drawText(
                    String.valueOf(i),
                    paddingLeft - 16,
                    textY,
                    labelPaint
            );

            // wartosc kostki w srodku bara
            if (counts[i] != 0) {
                String label;
                int rolls = counts[i];
                if (rolls == 1) {
                    label = "1 roll";
                } else {
                    label = rolls + " rolls";
                }
                canvas.drawText(
                        label,
                        paddingLeft + 60,
                        textY,
                        valuePaint
                );
            }
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
