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
        playingArea = new PlayingArea(5, 5);

        Pipe player1StartPipe = Pipe.createStartingPipe(getContext());
        Pipe player2StartPipe = Pipe.createStartingPipe(getContext());
        playingArea.add(player1StartPipe, 0, 1);
        playingArea.add(player2StartPipe, 0, 3);

        Pipe player1EndPipe = Pipe.createEndingPipe(getContext());
        Pipe player2EndPipe = Pipe.createEndingPipe(getContext());
        playingArea.add(player1EndPipe, 4, 1);
        playingArea.add(player2EndPipe, 4, 3);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        playingArea.draw(canvas);
    }
}
