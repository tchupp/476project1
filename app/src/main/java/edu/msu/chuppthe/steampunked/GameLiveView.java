package edu.msu.chuppthe.steampunked;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class GameLiveView extends View {

    /**
     * Local class to handle the touch status for one touch.
     * We will have one object of this type for each of the
     * two possible touches.
     */
    private class Touch {
        /**
         * Touch id
         */
        public int id = -1;

        /**
         * Current x location
         */
        public float x = 0;

        /**
         * Current y location
         */
        public float y = 0;

        /**
         * Previous x location
         */
        public float lastX = 0;

        /**
         * Previous y location
         */
        public float lastY = 0;

        /**
         * Change in x value from previous
         */
        public float dX = 0;

        /**
         * Change in y value from previous
         */
        public float dY = 0;

        /**
         * Copy the current values to the previous values
         */
        public void copyToLast() {
            lastX = x;
            lastY = y;
        }

        /**
         * Compute the values of dX and dY
         */
        public void computeDeltas() {
            dX = x - lastX;
            dY = y - lastY;
        }
    }

    /**
     * First touch status
     */
    private Touch touch1 = new Touch();

    /**
     * Second touch status
     */
    private Touch touch2 = new Touch();

    /**
     * The play area
     */
    private PlayingArea playingArea;

    /**
     * The selection area
     */
    private SelectionArea selectionArea;

    /**
     * Paint for the outline
     */
    private Paint outlinePaint;

    public GameLiveView(Context context) {
        super(context);
        init(null, 0);
    }

    public GameLiveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public GameLiveView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        this.selectionArea = new SelectionArea(getContext());

        this.outlinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.outlinePaint.setColor(Color.BLACK);
        this.outlinePaint.setStyle(Paint.Style.STROKE);
    }

    public void setupPlayArea(int gridSize, String playerOne, String playerTwo) {
        int totalGridSize = 5 * gridSize;
        int startingX = 0;
        int endingX = totalGridSize - 1;
        int player1Y = gridSize;
        int player2Y = 3 * gridSize;

        this.playingArea = new PlayingArea(totalGridSize, totalGridSize);

        Pipe player1StartPipe = Pipe.createStartingPipe(getContext(), playerOne);
        Pipe player2StartPipe = Pipe.createStartingPipe(getContext(), playerTwo);
        this.playingArea.add(player1StartPipe, startingX, player1Y);
        this.playingArea.add(player2StartPipe, startingX, player2Y);

        Pipe player1EndPipe = Pipe.createEndingPipe(getContext());
        Pipe player2EndPipe = Pipe.createEndingPipe(getContext());
        this.playingArea.add(player1EndPipe, endingX, player1Y + 1);
        this.playingArea.add(player2EndPipe, endingX, player2Y + 1);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        this.playingArea.draw(canvas);
        this.selectionArea.draw(canvas);

        // Draw the outline
        int cWidth = canvas.getWidth();
        int cHeight = canvas.getHeight();
        canvas.drawRect(0, 0, cWidth, cHeight, this.outlinePaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int id = event.getPointerId(event.getActionIndex());

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                touch1.id = id;
                touch2.id = -1;
                getPositions(event);
                touch1.copyToLast();
                return true;

            case MotionEvent.ACTION_POINTER_DOWN:
                if (touch1.id >= 0 && touch2.id < 0) {
                    touch2.id = id;
                    getPositions(event);
                    touch2.copyToLast();
                    return true;
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                touch1.id = -1;
                touch2.id = -1;
                invalidate();
                return true;

            case MotionEvent.ACTION_POINTER_UP:
                if (id == touch2.id) {
                    touch2.id = -1;
                } else if (id == touch1.id) {
                    // Make what was touch2 now be touch1 by
                    // swapping the objects.
                    Touch t = touch1;
                    touch1 = touch2;
                    touch2 = t;
                    touch2.id = -1;
                }
                invalidate();
                return true;

            case MotionEvent.ACTION_MOVE:
                getPositions(event);
                move();
                return true;
        }

        return super.onTouchEvent(event);
    }

    /**
     * Determine distance between the two touches
     *
     * @param x1 Touch 1 x
     * @param y1 Touch 1 y
     * @param x2 Touch 2 x
     * @param y2 Touch 2 y
     * @return computed distance
     */
    private static float distance(float x1, float y1, float x2, float y2) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        return (float) Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
    }

    /**
     * Get the positions for the two touches and put them
     * into the appropriate touch objects.
     *
     * @param event the motion event
     */
    private void getPositions(MotionEvent event) {
        for (int i = 0; i < event.getPointerCount(); i++) {
            // Get the pointer id
            int id = event.getPointerId(i);

            // Convert to image coordinates
            float x = (event.getX(i));
            float y = (event.getY(i));

            if (id == touch1.id) {
                touch1.copyToLast();
                touch1.x = x;
                touch1.y = y;
            } else if (id == touch2.id) {
                touch2.copyToLast();
                touch2.x = x;
                touch2.y = y;
            }
        }

        invalidate();
    }

    /**
     * Handle movement of the touches
     */
    private void move() {
        // If no touch1, we have nothing to do
        // This should not happen, but it never hurts
        // to check.
        if (touch1.id < 0) {
            return;
        }

        if (touch1.id >= 0) {
            // At least one touch
            // We are moving
            touch1.computeDeltas();
            this.playingArea.translate(touch1.dX, touch1.dY);
        }

        if (touch2.id >= 0) {
            // Two touches

            /*
             * Scaling
             */
            float distance1 = distance(touch1.lastX, touch1.lastY, touch2.lastX, touch2.lastY);
            float distance2 = distance(touch1.x, touch1.y, touch2.x, touch2.y);
            float ratio = distance2 / distance1;
            this.playingArea.scale(ratio);
        }
    }
}
