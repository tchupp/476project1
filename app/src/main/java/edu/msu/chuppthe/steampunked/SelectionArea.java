package edu.msu.chuppthe.steampunked;

import android.graphics.Canvas;
import android.graphics.Paint;

public class SelectionArea {

    /**
     * Paint for the selection area
     */
    private Paint selectionAreaPaint;

    public SelectionArea() {
        this.selectionAreaPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.selectionAreaPaint.setColor(0xffadf99d);
    }

    public void draw(Canvas canvas) {
        int cWidth = canvas.getWidth();
        int cHeight = canvas.getHeight();
        float top = cWidth < cHeight ? cWidth : 0;
        float left = cWidth < cHeight ? 0 : cHeight;

        // Draw the selection area
        canvas.drawRect(left, top, cWidth, cHeight, this.selectionAreaPaint);
    }
}
