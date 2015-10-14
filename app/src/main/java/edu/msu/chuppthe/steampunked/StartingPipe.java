package edu.msu.chuppthe.steampunked;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class StartingPipe extends Pipe {

    /**
     * Name of the player
     */
    private String playerName;

    /**
     * Image for the pipe
     */
    private Bitmap handleImage;

    /**
     * Paint to draw the player name
     */
    private Paint namePaint;

    /**
     * Constructor
     */
    public StartingPipe(Context context, String playerName) {
        super(false, true, false, false);
        this.playerName = playerName;
        this.handleImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.handle);
        this.setId(context, R.drawable.straight);

        this.namePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.namePaint.setColor(Color.BLACK);
        this.namePaint.setTextSize(40);
        this.namePaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    public void draw(Canvas canvas, float gridSize) {
        int cWidth = canvas.getWidth();
        int cHeight = canvas.getHeight();
        float cSize = cWidth < cHeight ? cWidth : cHeight;

        int pWidth = pipeImage.getWidth();
        int pHeight = pipeImage.getHeight();
        float pSize = pWidth < pHeight ? pWidth : pHeight;
        float scale = cSize / (gridSize * pSize);

        float facX = (float) this.x / gridSize;
        float facY = (this.y + 1.f) / gridSize;

        canvas.save();

        canvas.translate(facX * cSize, facY * cSize + 55.f);
        canvas.scale(scale, scale);

        canvas.save();
        canvas.rotate(-90);

        canvas.drawBitmap(pipeImage, 0, 0, null);
        canvas.restore();

        canvas.save();
        canvas.translate(0, -pHeight);

        canvas.drawBitmap(handleImage, 0, 0, null);

        canvas.restore();

        canvas.translate(pWidth / 2.f, pHeight / 8.f);
        canvas.drawText(this.playerName, 0, 0, this.namePaint);

        canvas.restore();
    }
}
