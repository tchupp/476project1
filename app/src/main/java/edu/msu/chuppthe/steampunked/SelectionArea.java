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
        int cWidth = canvas.getWidth();
        int cHeight = canvas.getHeight();
        float top = cWidth < cHeight ? cWidth : 0;
        float left = cWidth < cHeight ? 0 : cHeight;

        // Draw the selection area
        canvas.drawRect(left, top, cWidth, cHeight, this.selectionAreaPaint);

        for (int i = 0; i < this.pipes.size(); i++) {
            Pipe pipe = this.pipes.get(i);

            canvas.save();
            pipe.draw(canvas);
            canvas.restore();
        }
    }
}
