package edu.msu.chuppthe.steampunked;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SelectionArea {
    /**
     * Paint for the selection area
     */
    private Paint selectionAreaPaint;

    /**
     * This variable is set to a piece we are dragging. If
     * we are not dragging, the variable is null.
     */
    private Pipe dragging = null;

    /**
     * Most recent relative X touch when dragging
     */
    private float lastRelX;

    /**
     * Most recent relative Y touch when dragging
     */
    private float lastRelY;

    private List<Pipe> pipes;

    private Random random = new Random();

    /**
     * Context of the Selection Area
     */
    private Context context;

    public SelectionArea(Context context) {
        this.context = context;
        this.selectionAreaPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.selectionAreaPaint.setColor(0xffadf99d);

        this.pipes = new ArrayList<>();

        generatePipes(context);
    }

    private void generatePipes(Context context) {
        while (this.pipes.size() < 5) {
            switch (random.nextInt(5)) {
                case 0:
                    pipes.add(Pipe.createCapPipe(context));
                    break;
                case 1:
                    pipes.add(Pipe.createTeePipe(context));
                    break;
                case 2:
                    pipes.add(Pipe.createStraightPipe(context));
                    break;
                case 3:
                    pipes.add(Pipe.createNinetyPipe(context));
                    break;
                case 4:
                    pipes.add(Pipe.createStraightPipe(context));
                    break;
                default:
                    break;
            }
        }
    }

    public boolean onTouchEvent(View view, MotionEvent event) {
        float relX = event.getX();
        float relY = event.getY();

        switch (event.getActionMasked()) {

            case MotionEvent.ACTION_DOWN:
                return onTouched(relX, relY);

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                return onReleased(view);

            case MotionEvent.ACTION_MOVE:
                // If we are dragging, move the piece and force a redraw
                if (dragging != null) {
                    dragging.move(relX - lastRelX, relY - lastRelY);
                    lastRelX = relX;
                    lastRelY = relY;
                    view.invalidate();
                    return true;
                }
                break;
        }
        return false;
    }

    /**
     * Handle a touch message. This is when we get an initial touch
     *
     * @param x x location for the touch, relative to the puzzle - 0 to 1 over the puzzle
     * @param y y location for the touch, relative to the puzzle - 0 to 1 over the puzzle
     * @return true if the touch is handled
     */
    private boolean onTouched(float x, float y) {

        // Check each piece to see if it has been hit
        // We do this in reverse order so we find the pieces in front
        for (int p = pipes.size() - 1; p >= 0; p--) {
            if (pipes.get(p).hit(x, y)) {
                // We hit a piece!
                dragging = pipes.get(p);
                lastRelX = x;
                lastRelY = y;

                return true;
            }
        }
        return false;
    }

    private boolean onReleased(View view) {
        if (dragging != null) {
            dragging = null;
            return true;
        }
        return false;
    }

    public void draw(Canvas canvas) {
        float gridSize = 6;

        int cWidth = canvas.getWidth();
        int cHeight = canvas.getHeight();
        float cSize = cWidth > cHeight ? cWidth : cHeight;

        float horizontal = cWidth > cHeight ? cWidth : 0;
        float vertical = cWidth > cHeight ? 0 : cHeight;

        canvas.drawRect(0, 0, cWidth, cHeight, this.selectionAreaPaint);

        for (int i = 0; i < this.pipes.size(); i++) {
            Pipe pipe = this.pipes.get(i);

            float pSize = pipe.getImageSize();
            float scale = cSize / (gridSize * pSize);
            float fac = (float) i / (gridSize - 1f);
            float dx = horizontal * fac;
            float dy = vertical * fac;

            if (cWidth > cHeight) {
                dx += (pSize * 0.1f);
                dy += (pSize * 1.1f);
            } else {
                dx += (pSize * 0.25f);
                dy += (pSize * 0.8f);
            }

            pipe.setBasePosition(dx, dy, scale);
            pipe.draw(canvas);
        }
    }
}
