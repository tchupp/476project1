package edu.msu.chuppthe.steampunked;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class GameLiveView extends View {
    /**
     * The play area
     */
    private PlayingArea playingArea;

    /**
     * The selection area
     */
    private SelectionArea selectionArea;

    /**
     * Paint for the grid area
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
        this.playingArea.add(player1EndPipe, endingX, player1Y);
        this.playingArea.add(player2EndPipe, endingX, player2Y);
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
}
