package edu.msu.chuppthe.steampunked;

import android.graphics.Canvas;

import java.io.Serializable;

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
     * Construct a playing area
     *
     * @param width  Width (integer number of cells)
     * @param height Height (integer number of cells)
     */
    public PlayingArea(int width, int height) {
        this.width = width;
        this.height = height;

        // Allocate the playing area
        // Java automatically initializes all of the locations to null
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
        canvas.restore();
    }

    public void translate(float x, float y) {
        params.x += x;
        params.y += y;

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

    public void scale(float ratio) {
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
