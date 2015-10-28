package edu.msu.chuppthe.steampunked;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class StartingPipe extends Pipe {
    /**
     * Image for the pipe
     */
    private Bitmap handleImage;

    /**
     * Paint to draw the player name
     */
    private Paint namePaint;
    private Boolean active;

    /**
     * Constructor
     */
    public StartingPipe(Context context) {
        super(false, false, true, false);
        this.handleImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.straight);
        this.setId(context, R.drawable.handle);
        this.setMovable(false);

        this.namePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.namePaint.setColor(Color.BLACK);
        this.namePaint.setTextSize(40);
        this.namePaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    public void draw(Canvas canvas) {
        int pWidth = this.pipeImage.getWidth();
        int pHeight = this.pipeImage.getHeight();

        canvas.save();
        canvas.translate(this.getPositionX(), this.getPositionY());
        canvas.scale(this.getScale(), this.getScale());

        canvas.save();
        canvas.rotate(this.getRotation() * 90);

        canvas.drawBitmap(this.handleImage, 0, 0, null);
        canvas.restore();

        canvas.save();
        canvas.translate(0, -pHeight);

        canvas.drawBitmap(this.pipeImage, 0, 0, null);
        canvas.restore();

        canvas.save();
        canvas.translate(pWidth / 2.f, pHeight / 8.f);

        canvas.drawText(this.getPlayer().getName(), 0, 0, this.namePaint);
        canvas.restore();

        canvas.restore();

        if (!this.active) return;

        canvas.save();
        canvas.translate(canvas.getHeight() / 2f, 80);

        canvas.drawText(this.getPlayer().getName(), 0, 0, this.namePaint);
        canvas.restore();
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
