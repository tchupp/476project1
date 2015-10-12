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

        Pipe player1StartPipe = new Pipe(false, true, false, false);
        Pipe player2StartPipe = new Pipe(false, true, false, false);
        playingArea.add(player1StartPipe, 1, 2);
        playingArea.add(player2StartPipe, 1, 4);

        Pipe player1EndPipe = new Pipe(false, false, false, true);
        Pipe player2EndPipe = new Pipe(false, false, false, true);
        playingArea.add(player1EndPipe, 5, 2);
        playingArea.add(player2EndPipe, 5, 4);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        playingArea.draw(canvas);
    }
}
