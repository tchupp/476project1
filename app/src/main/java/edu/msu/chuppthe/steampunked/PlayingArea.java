package edu.msu.chuppthe.steampunked;

import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A representation of the playing area
 */
public class PlayingArea {

    private class Parameters implements Serializable {
        /**
         * X location for the left side of the board
         */
        private float x = 0f;

        /**
         * Y location for the top side of the board
         */
        private float y = 0f;

        /**
         * Max X location for the right side of the board
         */
        private float maxSmall = -1f;

        /**
         * Max Y location for the bottom side of the board
         */
        private float maxLarge = -1f;

        /**
         * Scale of the overall board
         */
        private float scaleFac = -1f;

        /**
         * Do the params need initializing?
         */
        private boolean start = true;
    }

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
     * Width of the playing area (integer number of cells)
     */
    private int width;

    /**
     * Height of the playing area (integer number of cells)
     */
    private int height;

    /**
     * Storage for the pipes
     * First level: X, second level Y
     */
    private Pipe[][] pipes;

    /**
     * This variable is set to a piece we are dragging. If
     * we are not dragging, the variable is null.
     */
    private Pipe dragging = null;

    /**
     * This variable is set to a piece we most recently selected.
     * Lets us keep track of which piece we want to discard or install.
     */
    private Pipe selected = null;

    /**
     * The current parameters
     */
    private Parameters params = new Parameters();

    /**
     * First touch status
     */
    private Touch touch1 = new Touch();

    /**
     * Second touch status
     */
    private Touch touch2 = new Touch();

    /**
     * Construct a playing area
     *
     * @param width  Width (integer number of cells)
     * @param height Height (integer number of cells)
     */
    public PlayingArea(int width, int height) {
        this.width = width;
        this.height = height;

        this.pipes = new Pipe[width][height];
    }

    /**
     * Get the playing area height
     *
     * @return Height
     */
    public int getHeight() {
        return this.height;
    }

    /**
     * Get the playing area width
     *
     * @return Width
     */
    public int getWidth() {
        return this.width;
    }

    /**
     * Get the pipe at a given location.
     * This will return null if outside the playing area.
     *
     * @param x X location
     * @param y Y location
     * @return Reference to Pipe object or null if none exists
     */
    public Pipe getPipe(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return null;
        }

        return pipes[x][y];
    }

    /**
     * Add a pipe to the playing area
     *
     * @param pipe Pipe to add
     * @param x    X location
     * @param y    Y location
     */
    public void add(Pipe pipe, int x, int y) {
        pipes[x][y] = pipe;
        pipe.setPosition(this, x, y);
    }

    /**
     * Search to determine if this pipe has no leaks
     *
     * @param start Starting pipe to search from
     * @return true if no leaks
     */
    public boolean search(Pipe start) {
        /*
         * Set the visited flags to false
         */
        for (Pipe[] row : pipes) {
            for (Pipe pipe : row) {
                if (pipe != null) {
                    pipe.setVisited(false);
                }
            }
        }

        /*
         * The pipe itself does the actual search
         */
        return start.search();
    }

    /**
     * On touch event for the playing area
     *
     * @param view  View context of the touch
     * @param event touch event
     * @return if the touch was successful
     */
    public boolean onTouchEvent(View view, MotionEvent event) {
        int id = event.getPointerId(event.getActionIndex());

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                touch1.id = id;
                touch2.id = -1;
                getPositions(view, event);
                touch1.copyToLast();
                onTouched();
                return true;

            case MotionEvent.ACTION_POINTER_DOWN:
                if (touch1.id >= 0 && touch2.id < 0) {
                    touch2.id = id;
                    getPositions(view, event);
                    touch2.copyToLast();
                    return true;
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                touch1.id = -1;
                touch2.id = -1;
                onRelease();
                view.invalidate();
                return true;

            case MotionEvent.ACTION_POINTER_UP:
                if (id == touch2.id) {
                    touch2.id = -1;
                } else if (id == touch1.id) {
                    // Make what was touch2 now be touch1
                    Touch t = touch1;
                    touch1 = touch2;
                    touch2 = t;
                    touch2.id = -1;
                }
                view.invalidate();
                return true;

            case MotionEvent.ACTION_MOVE:
                getPositions(view, event);
                move();
                return true;
        }
        return false;
    }

    /**
     * Draw all the pieces to the canvas
     *
     * @param canvas canvas to draw to
     */
    public void draw(Canvas canvas) {
        int cWidth = canvas.getWidth();
        int cHeight = canvas.getHeight();
        float cSmall = cWidth < cHeight ? cWidth : cHeight;
        float cBig = cWidth > cHeight ? cWidth : cHeight;

        if (params.start) {
            params.maxSmall = cSmall;
            params.maxLarge = cBig;
            params.scaleFac = cBig / cSmall;
            params.start = false;
        }

        canvas.save();
        canvas.translate(params.x, params.y);
        canvas.scale(params.scaleFac, params.scaleFac);

        // Draw the pipes
        for (int x = 0; x < pipes.length; x++) {
            Pipe[] row = pipes[x];
            for (int y = 0; y < row.length; y++) {
                Pipe pipe = row[y];
                if (pipe != null) {
                    float pSize = pipe.getImageSize();
                    float scale = cSmall / (this.width * pSize);

                    float facX = (float) x / this.width;
                    float facY = (y + 1.f) / this.width;

                    pipe.setBasePosition(facX * cSmall, facY * cSmall, scale);
                    pipe.draw(canvas);
                }
            }
        }

        if (this.selected != null) this.selected.draw(canvas);

        canvas.restore();
    }

    /**
     * Find the neighbor of this pipe
     *
     * @param d Index (north=0, east=1, south=2, west=3)
     * @return Pipe object or null if no neighbor
     */
    public Pipe neighbor(int d, int x, int y) {
        switch (d) {
            case 0:
                return this.getPipe(x, y - 1);

            case 1:
                return this.getPipe(x + 1, y);

            case 2:
                return this.getPipe(x, y + 1);

            case 3:
                return this.getPipe(x - 1, y);
        }

        return null;
    }

    /**
     * Install the selected pipe
     */
    public boolean installSelection() {
        if (this.selected == null) {
            return false;
        }

        int gridX = Math.round(selected.getPositionX() * this.width / params.maxSmall);
        int gridY = Math.round(selected.getPositionY() * this.width / params.maxSmall - 1);

        if (this.pipes[gridX][gridY] == null) {
            if (!checkConnected(gridX, gridY)) {
                return false;
            }

            // If the piece is good to install
            this.selected.resetMovement();
            this.selected.setMovable(false);
            add(this.selected, gridX, gridY);
            this.selected = null;

            return true;
        }
        return false;
    }

    private boolean checkConnected(int gridX, int gridY) {
        boolean hasNeighbor = false;

        for (int d = 0; d < 4; d++) {
            Pipe n = neighbor(d, gridX, gridY);
            if (n == null) {
                continue;
            }
            hasNeighbor = true;

            int dp = (d + 2) % 4;
            if (!(n.canConnect(dp) && selected.canConnect(d))) {
                // Either cannot connect
                return false;
            }
            if ((selected.getPlayer() != n.getPlayer())) {
                // Pieces have different player
                return false;
            }
        }
        return hasNeighbor;
    }

    /**
     * Discard the selected pipe
     */
    public boolean discardSelection() {
        if (this.selected == null) {
            return false;
        }

        this.selected = null;
        return true;
    }

    /**
     * Rotate the selected pipe
     */
    public boolean rotateSelection() {
        if (this.selected == null) {
            return false;
        }

        this.selected.rotate();
        return true;
    }

    /**
     * Set the selected piece from the selection area
     *
     * @param selected pipe from the selection area
     */
    public void setSelected(Pipe selected) {
        this.selected = selected;
        this.dragging = selected;

        float pSize = this.selected.getImageSize();
        float scale = params.maxSmall / (this.width * pSize);

        float x = this.selected.getPositionX();
        float y = params.maxSmall / params.scaleFac;

        this.selected.setBasePosition(x, y, scale);
        this.selected.resetMovement();
    }

    /**
     * Handle a touch message. This is when we get an initial touch
     */
    private void onTouched() {
        float testX = (touch1.x - params.x) / params.scaleFac;
        float testY = (touch1.y - params.y) / params.scaleFac;

        if (this.selected != null) {
            if (this.selected.hit(testX, testY)) {
                this.dragging = this.selected;
                return;
            }
        }

        for (Pipe[] row : pipes) {
            for (Pipe pipe : row) {
                if (pipe != null) {
                    if (pipe.hit(testX, testY)) {
                        this.dragging = pipe;
                        this.selected = pipe;
                        return;
                    }
                }
            }
        }
    }

    /**
     * Handle a release message.
     */
    private void onRelease() {
        if (this.dragging != null) {
            this.dragging = null;
        }
    }

    /**
     * Get the positions for the two touches and put them
     * into the appropriate touch objects.
     *
     * @param view  View context
     * @param event the motion event
     */
    private void getPositions(View view, MotionEvent event) {
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

        view.invalidate();
    }

    /**
     * Handle movement of the touches
     */
    private void move() {
        // If no touch1, we have nothing to do
        if (touch1.id < 0) {
            return;
        }

        if (touch1.id >= 0) {
            // At least one touch! We are moving
            touch1.computeDeltas();
            if (this.dragging != null) {
                float dx = touch1.dX / params.scaleFac;
                float dy = touch1.dY / params.scaleFac;

                translatePipe(dx, dy, this.dragging);
                return;
            } else {
                translate(touch1.dX, touch1.dY);
            }
        }
        if (touch2.id >= 0) {
            // Two touches

            /*
             * Scaling
             */
            float distance1 = distance(touch1.lastX, touch1.lastY, touch2.lastX, touch2.lastY);
            float distance2 = distance(touch1.x, touch1.y, touch2.x, touch2.y);
            float ratio = distance2 / distance1;
            this.scale(ratio);
        }
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
     * Translate the view, while keeping it contained in the allowed area
     *
     * @param dx delta x to move by
     * @param dy delta y to move by
     */
    private void translate(float dx, float dy) {
        params.x += dx;
        params.y += dy;

        if (params.x > 0) {
            params.x = 0;
        }
        if (params.y > 0) {
            params.y = 0;
        }
        if (params.x < (-params.maxSmall * params.scaleFac) + params.maxLarge) {
            params.x = (-params.maxSmall * params.scaleFac) + params.maxLarge;
        }
        if (params.y < (-params.maxSmall * params.scaleFac) + params.maxSmall) {
            params.y = (-params.maxSmall * params.scaleFac) + params.maxSmall;
        }
    }

    /**
     * Translate the pipe, while keeping it contained in the playing area
     *
     * @param dx   delta x to move by
     * @param dy   delta y to move by
     * @param pipe pipe to translate
     */
    private void translatePipe(float dx, float dy, Pipe pipe) {
        float x = pipe.getPositionX() + dx;
        float y = pipe.getPositionY() + dy;
        float pSize = pipe.getImageSize() * pipe.getScale();

        if (x > 0 && (y - pSize) > 0
                && (x + pSize) < params.maxLarge && y < params.maxSmall) {
            pipe.move(dx, dy);
        }
    }

    /**
     * Scale the view by the given ratio
     *
     * @param ratio amount to scale the view by
     */
    private void scale(float ratio) {
        params.scaleFac *= ratio;
        if (params.scaleFac < params.maxLarge / params.maxSmall) {
            params.scaleFac = params.maxLarge / params.maxSmall;
        } else {
            float xc = params.maxSmall / 2f;
            float yc = params.maxSmall / 2f;

            params.x = (params.x - xc) * ratio + xc;
            params.y = (params.y - yc) * ratio + yc;
        }
    }

}
