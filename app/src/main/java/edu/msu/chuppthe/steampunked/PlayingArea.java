package edu.msu.chuppthe.steampunked;

import android.graphics.Canvas;

/**
 * A representation of the playing area
 */
public class PlayingArea {
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
        pipes = new Pipe[width][height];
    }

    /**
     * Get the playing area height
     *
     * @return Height
     */
    public int getHeight() {
        return height;
    }

    /**
     * Get the playing area width
     *
     * @return Width
     */
    public int getWidth() {
        return width;
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
        int wid = canvas.getWidth();
        int hgt = canvas.getHeight();
        int sqSize = wid < hgt ? wid : hgt;

        for (int i = 0; i < pipes.length; i++) {
            Pipe[] row = pipes[i];
            for (int j = 0; j < row.length; j++) {
                Pipe pipe = row[j];
                if (pipe != null) {
                    canvas.save();
                    float xScale = (float) i / (float) this.width;
                    float yScale = (float) j / (float) this.width;
                    canvas.translate(xScale * sqSize, yScale * sqSize);
                    pipe.draw(canvas);
                    canvas.restore();
                }
            }
        }
    }
}
