package edu.msu.chuppthe.steampunked;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    /**
     * List of pipes in the selection view
     */
    private Map<Player, List<Pipe>> pipeMap;

    /**
     * Random generator
     */
    private Random random = new Random();

    /**
     * Width of the view
     */
    private float cWidth;

    /**
     * Height of the view
     */
    private float cHeight;

    public SelectionArea() {
        this.selectionAreaPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.selectionAreaPaint.setColor(0xffadf99d);

        this.pipeMap = new HashMap<>();
    }

    /**
     * Handle touch events in the selection area
     *
     * @param view   view context of the touch
     * @param event  the touch event
     * @param player player that made the touch
     * @return if the touch was successful
     */
    public boolean onTouchEvent(SelectionAreaView view, MotionEvent event, Player player) {
        float relX = event.getX();
        float relY = event.getY();

        switch (event.getActionMasked()) {

            case MotionEvent.ACTION_DOWN:
                return onTouched(relX, relY, player);

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                return onReleased();

            case MotionEvent.ACTION_MOVE:
                return translatePipe(view, relX, relY, player);

        }
        return false;
    }


    /**
     * Handle drawing the selection area
     *
     * @param canvas context to draw to
     * @param player player to draw for
     */
    public void draw(Canvas canvas, Player player) {
        float gridSize = 6;

        this.cWidth = canvas.getWidth();
        this.cHeight = canvas.getHeight();
        float cSize = cWidth > cHeight ? cWidth : cHeight;

        float horizontal = cWidth > cHeight ? cWidth : 0;
        float vertical = cWidth > cHeight ? 0 : cHeight;

        canvas.drawRect(0, 0, cWidth, cHeight, this.selectionAreaPaint);

        List<Pipe> pipes = this.pipeMap.get(player);
        if (pipes == null) {
            return;
        }

        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);

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


    /**
     * Generate new random pipes
     */
    public void generatePipes(Context context, Player player) {
        if (this.pipeMap.get(player) == null) this.pipeMap.put(player, new ArrayList<Pipe>());

        List<Pipe> pipes = this.pipeMap.get(player);
        while (pipes.size() < 5) {
            switch (random.nextInt(5)) {
                case 0:
                    pipes.add(Pipe.createCapPipe(context, player));
                    break;
                case 1:
                    pipes.add(Pipe.createTeePipe(context, player));
                    break;
                case 2:
                    pipes.add(Pipe.createStraightPipe(context, player));
                    break;
                case 3:
                    pipes.add(Pipe.createNinetyPipe(context, player));
                    break;
                case 4:
                    pipes.add(Pipe.createStraightPipe(context, player));
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Handle a touch message. This is when we get an initial touch
     *
     * @param x      x location for the touch, relative to the puzzle - 0 to 1 over the puzzle
     * @param y      y location for the touch, relative to the puzzle - 0 to 1 over the puzzle
     * @param player player that made the touch
     * @return true if the touch is handled
     */
    private boolean onTouched(float x, float y, Player player) {
        List<Pipe> pipes = this.pipeMap.get(player);
        if (pipes == null) {
            return false;
        }

        // Check each piece to see if it has been hit
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

    /**
     * @return if the release was successful
     */
    private boolean onReleased() {
        if (dragging != null) {
            dragging = null;
            return true;
        }
        return false;
    }

    /**
     * Move the pipe, check to see if its out of the view
     *
     * @param view   view context
     * @param relX   x position relative to the touch
     * @param relY   y position relative to the touch
     * @param player player who is dragging the piece
     * @return if the translation was successful
     */
    private boolean translatePipe(SelectionAreaView view, float relX, float relY, Player player) {
        if (dragging == null) {
            return false;
        }

        float dx = relX - lastRelX;
        float dy = relY - lastRelY;

        float x = dragging.getPositionX() + dx;
        float y = dragging.getPositionY() + dy;
        float pSize = dragging.getImageSize() * dragging.getScale();

        if ((x > 0) && (y - pSize / 2 > 0)
                && (x + pSize < cWidth) && (y < cHeight)) {
            this.dragging.move(dx, dy);
        }

        if (y - pSize / 2 <= 0) {
            view.notifyPieceSelected(this.dragging);
            this.pipeMap.get(player).remove(this.dragging);
        }

        lastRelX = relX;
        lastRelY = relY;

        view.invalidate();
        return true;
    }
}
