package edu.msu.chuppthe.steampunked;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Pipe {

    public static Pipe createStartingPipe(Context context, String playerName) {
        return new StartingPipe(context, playerName);
    }

    public static Pipe createEndingPipe(Context context) {
        Pipe startingPipe = new Pipe(false, false, false, true);
        startingPipe.setId(context, R.drawable.gauge);
        return startingPipe;
    }

    public static Pipe createCapPipe(Context context) {
        Pipe capPipe = new Pipe(false, false, true, false);
        capPipe.setId(context, R.drawable.cap);
        return capPipe;
    }

    public static Pipe createTeePipe(Context context) {
        Pipe capPipe = new Pipe(true, true, true, false);
        capPipe.setId(context, R.drawable.tee);
        return capPipe;
    }

    public static Pipe createNinetyPipe(Context context) {
        Pipe capPipe = new Pipe(false, true, true, false);
        capPipe.setId(context, R.drawable.ninety);
        return capPipe;
    }

    public static Pipe createStraightPipe(Context context) {
        Pipe capPipe = new Pipe(true, false, true, false);
        capPipe.setId(context, R.drawable.straight);
        return capPipe;
    }

    /**
     * Playing area this pipe is a member of
     */
    private PlayingArea playingArea = null;

    /**
     * Array that indicates which sides of this pipe
     * has flanges. The order is north, east, south, west.
     * <p>
     * As an example, a T that has a horizontal pipe
     * with the T open to the bottom would be:
     * <p>
     * false, true, true, true
     */
    private boolean[] connect = {false, false, false, false};

    /**
     * X location in the playing area (index into array)
     */
    protected int x = 0;

    /**
     * Y location in the playing area (index into array)
     */
    protected int y = 0;

    /**
     * X position
     */
    protected float xPos = 0;

    /**
     * Y position
     */
    protected float yPos = 0;

    /**
     * Base X position
     */
    protected float xBase = 0;

    /**
     * Base Y position
     */
    protected float yBase = 0;

    /**
     * Base scale
     */
    protected float scaleBase;

    /**
     * Can the piece be moved
     */
    protected boolean isMovable = true;

    /**
     * Depth-first visited visited
     */
    private boolean visited = false;

    /**
     * Image for the pipe
     */
    protected Bitmap pipeImage = null;

    /**
     * ID for the pipe image
     */
    private int id;

    /**
     * Paint for the outline
     */
    protected Paint outlinePaint;

    /**
     * Constructor
     *
     * @param north True if can connect north
     * @param east  True if can connect east
     * @param south True if can connect south
     * @param west  True if can connect west
     */
    public Pipe(boolean north, boolean east, boolean south, boolean west) {
        connect[0] = north;
        connect[1] = east;
        connect[2] = south;
        connect[3] = west;

        this.outlinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.outlinePaint.setColor(Color.BLACK);
        this.outlinePaint.setStyle(Paint.Style.STROKE);
    }

    /**
     * Search to see if there are any downstream of this pipe
     * <p>
     * This does a simple depth-first search to find any connections
     * that are not, in turn, connected to another pipe. It also
     * set the visited flag in all pipes it does visit, so you can
     * tell if a pipe is reachable from this pipe by checking that flag.
     *
     * @return True if no leaks in the pipe
     */
    public boolean search() {
        visited = true;

        for (int d = 0; d < 4; d++) {
            /*
             * If no connection this direction, ignore
             */
            if (!connect[d]) {
                continue;
            }

            Pipe n = neighbor(d);
            if (n == null) {
                // We leak
                // We have a connection with nothing on the other side
                return false;
            }

            // What is the matching location on
            // the other pipe. For example, if
            // we are looking in direction 1 (east),
            // the other pipe must have a connection
            // in direction 3 (west)
            int dp = (d + 2) % 4;
            if (!n.connect[dp]) {
                // We have a bad connection, the other side is not
                // a flange to connect to
                return false;
            }

            if (!n.visited) {
                // Is there a leak in that direction
                if (!n.search()) {
                    // We found a leak downstream of this pipe
                    return false;
                }
            }
        }

        // Yah, no leaks
        return true;
    }

    /**
     * Find the neighbor of this pipe
     *
     * @param d Index (north=0, east=1, south=2, west=3)
     * @return Pipe object or null if no neighbor
     */
    private Pipe neighbor(int d) {
        switch (d) {
            case 0:
                return playingArea.getPipe(x, y - 1);

            case 1:
                return playingArea.getPipe(x + 1, y);

            case 2:
                return playingArea.getPipe(x, y + 1);

            case 3:
                return playingArea.getPipe(x - 1, y);
        }

        return null;
    }

    /**
     * Get the playing area
     *
     * @return Playing area object
     */
    public PlayingArea getPlayingArea() {
        return playingArea;
    }

    /**
     * Set the playing area and location for this pipe
     *
     * @param playingArea Playing area we are a member of
     * @param x           X index
     * @param y           Y index
     */
    public void setPosition(PlayingArea playingArea, int x, int y) {
        this.playingArea = playingArea;
        this.x = x;
        this.y = y;
    }

    /**
     * Set the playing area and location for this pipe
     *
     * @param context view context
     * @param id      id of the image
     */
    protected void setId(Context context, int id) {
        this.id = id;
        this.pipeImage = BitmapFactory.decodeResource(context.getResources(), id);
    }

    /**
     * Has this pipe been visited by a search?
     *
     * @return True if yes
     */
    public boolean beenVisited() {
        return this.visited;
    }

    /**
     * Set the visited flag for this pipe
     *
     * @param visited Value to set
     */
    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    /**
     * Get the smaller side of the pipe image
     *
     * @return smaller side of pipe image
     */
    public float getImageSize() {
        int pWidth = pipeImage.getWidth();
        int pHeight = pipeImage.getHeight();
        return (float) (pWidth < pHeight ? pWidth : pHeight);
    }

    /**
     * Draw the piece to the canvas
     *
     * @param canvas canvas to draw to
     */
    public void draw(Canvas canvas) {
        canvas.save();
        canvas.translate(this.xBase + this.xPos, this.yBase + this.yPos);
        canvas.scale(this.scaleBase, this.scaleBase);
        canvas.rotate(-90);

        canvas.drawBitmap(pipeImage, 0, 0, null);
        canvas.drawRect(0, 0, this.getImageSize(), this.getImageSize(), this.outlinePaint);
        canvas.restore();
    }

    public void move(float dx, float dy) {
        if (this.isMovable) {
            this.xPos += dx;
            this.yPos += dy;
        }
    }

    public void setBasePosition(float x, float y, float scale) {
        this.xBase = x;
        this.yBase = y;
        this.scaleBase = scale;
    }

    public boolean hit(float testX, float testY) {
        float pX = this.xBase + this.xPos;
        float pY = this.yBase + this.yPos;

        return (pX < testX && testX < pX + this.getImageSize())
                && (pY - this.getImageSize() < testY && testY < pY);
    }
}
