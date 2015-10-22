package edu.msu.chuppthe.steampunked;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class PlayingAreaView extends View {

    /**
     * The play area
     */
    private PlayingArea playingArea = new PlayingArea(0, 0);

    /**
     * Paint for the outline
     */
    private Paint outlinePaint;

    public PlayingAreaView(Context context) {
        super(context);

        init(null, 0);
    }

    public PlayingAreaView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public PlayingAreaView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        this.outlinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.outlinePaint.setColor(Color.BLACK);
        this.outlinePaint.setStyle(Paint.Style.STROKE);
    }

    /**
     * Initialize the playing area
     *
     * @param gridSize  size of the playing grid. 5, 10, or 20 square
     * @param playerOne player one name
     * @param playerTwo player two name
     */
    public void setupPlayArea(int gridSize, Player playerOne, Player playerTwo) {
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

        Pipe player1EndPipe = Pipe.createEndingPipe(getContext(), playerOne);
        Pipe player2EndPipe = Pipe.createEndingPipe(getContext(), playerTwo);
        this.playingArea.add(player1EndPipe, endingX, player1Y + 1);
        this.playingArea.add(player2EndPipe, endingX, player2Y + 1);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        this.playingArea.draw(canvas);

        // Draw the outline
        int cWidth = canvas.getWidth();
        int cHeight = canvas.getHeight();
        canvas.drawRect(0, 0, cWidth, cHeight, this.outlinePaint);
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        return this.playingArea.onTouchEvent(this, event);
    }

    /**
     * Tell the playing area to install the selection and invalidate the view
     */
    public boolean installSelection() {
        boolean success = this.playingArea.installSelection();
        invalidate();

        return success;
    }

    /**
     * Tell the playing area to discard the selection and invalidate the view
     */
    public boolean discardSelection() {
        boolean success = this.playingArea.discardSelection();
        invalidate();

        return success;
    }

    /**
     * Tell the playing area to rotate the selection and invalidate the view
     */
    public boolean rotateSelected() {
        boolean success = this.playingArea.rotateSelection();
        invalidate();

        return success;
    }

    /**
     * checks for leaks in the playing area
     */
    public boolean checkLeaks(Pipe pipe) {
        boolean success = this.playingArea.search(pipe);
        invalidate();

        return success;
    }

    /**
     * Add the selected pipe from the SelectionArea to the PlayingArea
     *
     * @param pipe pipe that has been selected
     */
    public void notifyPieceSelected(Pipe pipe) {
        this.playingArea.setSelected(pipe);
        invalidate();
    }
}
