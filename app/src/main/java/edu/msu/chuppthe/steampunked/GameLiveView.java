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
        Pipe player3StartPipe = Pipe.createStartingPipe(getContext());
        Pipe player4StartPipe = Pipe.createStartingPipe(getContext());
        Pipe player5StartPipe = Pipe.createStartingPipe(getContext());
        playingArea.add(player1StartPipe, 0, 0);
        playingArea.add(player2StartPipe, 1, 1);
        playingArea.add(player3StartPipe, 2, 2);
        playingArea.add(player4StartPipe, 3, 3);
        playingArea.add(player5StartPipe, 0, 4);

        Pipe player1EndPipe = Pipe.createEndingPipe(getContext());
        Pipe player2EndPipe = Pipe.createEndingPipe(getContext());
        Pipe player3EndPipe = Pipe.createEndingPipe(getContext());
        Pipe player4EndPipe = Pipe.createEndingPipe(getContext());
        Pipe player5EndPipe = Pipe.createEndingPipe(getContext());
        playingArea.add(player1EndPipe, 4, 0);
        playingArea.add(player2EndPipe, 4, 1);
        playingArea.add(player3EndPipe, 4, 2);
        playingArea.add(player4EndPipe, 4, 3);
        playingArea.add(player5EndPipe, 4, 4);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        playingArea.draw(canvas);
    }
}
