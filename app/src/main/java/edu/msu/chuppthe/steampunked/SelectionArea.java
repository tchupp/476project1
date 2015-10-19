package edu.msu.chuppthe.steampunked;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.List;

public class SelectionArea {

    /**
     * Paint for the selection area
     */
    private Paint selectionAreaPaint;

    /**
     * Paint for the outline
     */
    private Paint outlinePaint;

    private List<Pipe> pipes;

    public SelectionArea(Context context) {
        this.selectionAreaPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.selectionAreaPaint.setColor(0xffadf99d);

        this.outlinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.outlinePaint.setColor(Color.BLACK);
        this.outlinePaint.setStyle(Paint.Style.STROKE);

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
        float cSize = cWidth > cHeight ? cWidth : cHeight;

        float top = cWidth > cHeight ? cWidth : 0;
        float left = cWidth > cHeight ? 0 : cHeight;

        canvas.drawRect(0, 0, cWidth, cHeight, this.selectionAreaPaint);

        for (int i = 0; i < this.pipes.size(); i++) {
            Pipe pipe = this.pipes.get(i);

            float pSize = pipe.getImageSize();
            float scale = cSize / (gridSize * pSize);
            float fac = (float) i / (gridSize - 1f);
            float dx = top * fac;
            float dy = left * fac;

            if (cWidth > cHeight) {
                dx += (pSize * 0.1f);
                dy += (pSize * 1.1f);
            } else {
                dx += (pSize * 0.25f);
                dy += (pSize * 0.8f);
            }

            canvas.save();
            canvas.translate(dx, dy);
            canvas.scale(scale, scale);

            pipe.draw(canvas);
            canvas.drawRect(0, -pSize, pSize, 0, this.outlinePaint);

            canvas.restore();
        }
    }
}
