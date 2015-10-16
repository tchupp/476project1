package edu.msu.chuppthe.steampunked;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.List;

public class SelectionArea {

    /**
     * Paint for the selection area
     */
    private Paint selectionAreaPaint;

    private List<Pipe> pipes;

    public SelectionArea(Context context) {
        this.selectionAreaPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.selectionAreaPaint.setColor(0xffadf99d);

        this.pipes = new ArrayList<>();

        //TODO: Replace this with random generation
        pipes.add(Pipe.createCapPipe(context));
        pipes.add(Pipe.createStraightPipe(context));
        pipes.add(Pipe.createNinetyPipe(context));
        pipes.add(Pipe.createStraightPipe(context));
        pipes.add(Pipe.createTeePipe(context));
    }

    public void draw(Canvas canvas) {
        float gridSize = 6;

        int cWidth = canvas.getWidth();
        int cHeight = canvas.getHeight();
        float cSize = cWidth < cHeight ? cWidth : cHeight;

        float top = cWidth < cHeight ? cWidth : 0;
        float left = cWidth < cHeight ? 0 : cHeight;

        // Draw the selection area
        canvas.drawRect(left, top, cWidth, cHeight, this.selectionAreaPaint);

        for (int i = 0; i < this.pipes.size(); i++) {
            Pipe pipe = this.pipes.get(i);

            float pSize = pipe.getImageSize();
            float scale = cSize / (gridSize * pSize);
            float fac = (float) i / (gridSize - 1f);
            float dx = (left + top * fac);
            float dy = (top + left * fac);

            if (cWidth < cHeight) {
                dy += (pSize * 1.25f);
            } else {
                dy += (pSize * 0.8f);
                dx += (pSize * 1f);
            }

            canvas.save();
            canvas.translate(dx, dy);
            canvas.scale(scale, scale);

            pipe.draw(canvas);

            canvas.restore();
        }
    }
}
