package edu.msu.chuppthe.steampunked;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

public class GameLiveView extends View {
    /**
     * The play area
     */
    private PlayingArea playingArea;

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
        int gridSize = 1;
        setPlayAreaSize(gridSize);
    }

    public void setPlayAreaSize(int gridSize) {
        int totalGridSize = 5 * gridSize;
        int startingX = 0;
        int endingX = totalGridSize - 1;
        int player1Y = gridSize;
        int player2Y = 3 * gridSize;

        playingArea = new PlayingArea(totalGridSize, totalGridSize);

        Pipe player1StartPipe = Pipe.createStartingPipe(getContext());
        Pipe player2StartPipe = Pipe.createStartingPipe(getContext());
        playingArea.add(player1StartPipe, startingX, player1Y);
        playingArea.add(player2StartPipe, startingX, player2Y);

        Pipe player1EndPipe = Pipe.createEndingPipe(getContext());
        Pipe player2EndPipe = Pipe.createEndingPipe(getContext());
        playingArea.add(player1EndPipe, endingX, player1Y);
        playingArea.add(player2EndPipe, endingX, player2Y);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        playingArea.draw(canvas);
    }
}
