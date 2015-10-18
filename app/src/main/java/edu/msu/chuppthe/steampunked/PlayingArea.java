package edu.msu.chuppthe.steampunked;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

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
        private float maxX = -1f;

        /**
         * Max Y location for the bottom side of the board
         */
        private float maxY = -1f;

        /**
         * Scale of the overall board
         */
        private float scaleFac = 1f;
    }

    private class DebugInfo {
        private Paint linePaint;

        public DebugInfo() {
            this.linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            this.linePaint.setColor(Color.BLACK);
            this.linePaint.setTextSize(40);
        }

        public void draw(Canvas canvas, float x, float y, float maxX, float maxY, float scale) {
            canvas.drawText("X: " + String.valueOf(x), 100, 50, this.linePaint);
            canvas.drawText("Y: " + String.valueOf(y), 100, 100, this.linePaint);
            canvas.drawText("Max X: " + String.valueOf(maxX), 100, 150, this.linePaint);
            canvas.drawText("Max Y: " + String.valueOf(maxY), 100, 200, this.linePaint);
            canvas.drawText("Scale: " + String.valueOf(scale), 100, 250, this.linePaint);
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
        float cSize = cWidth < cHeight ? cWidth : cHeight;
        if (params.maxX < 0) params.maxX = cSize;
        if (params.maxY < 0) params.maxY = cSize;

//        new DebugInfo().draw(canvas, params.x, params.y, -params.maxX, -params.maxY, params.scaleFac);

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
                    float scale = cSize / (this.width * pSize);

                    float facX = (float) x / this.width;
                    float facY = (y + 1.f) / this.width;

                    canvas.save();

                    canvas.translate(facX * cSize, facY * cSize);
                    canvas.scale(scale, scale);

                    pipe.draw(canvas);

                    canvas.restore();
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
        if (params.x < (-params.maxX * params.scaleFac) + params.maxX) {
            params.x = (-params.maxX * params.scaleFac) + params.maxX;
        }
        if (params.y < (-params.maxY * params.scaleFac) + params.maxY) {
            params.y = (-params.maxY * params.scaleFac) + params.maxY;
        }
    }

    public void scale(float ratio) {
        params.scaleFac *= ratio;
        if (params.scaleFac < 1f) {
            params.scaleFac = 1f;
        } else {
            float xc = params.maxX / 2f;
            float yc = params.maxY / 2f;

            params.x = (params.x - xc) * ratio + xc;
            params.y = (params.y - yc) * ratio + yc;
        }
    }
}
