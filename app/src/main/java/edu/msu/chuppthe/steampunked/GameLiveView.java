package edu.msu.chuppthe.steampunked;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

public class GameLiveView extends View {

    /**
     * The actual puzzle
     */
    private Pipe pipe;

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


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


    }
}
