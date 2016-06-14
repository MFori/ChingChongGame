package cz.martinforejt.chingchong;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by Martin Forejt on 24.05.2016.
 * forejt.martin97@gmail.com
 * class CountDown
 * Draws countDown img ( 5 - 4,..)
 */
public class CountDown {

    private static Bitmap original = null;

    // countdown position 5-4-3-2-1
    protected int number;

    protected int width, height, opacity;
    protected int x, y;

    /**
     * Construct
     *
     * @param context Context
     * @param number  int
     */
    public CountDown(Context context, int number) {
        initBitmap(context, number);
        this.number = number;
    }

    /**
     * Get (create from resources) the original bitmap for current number
     *
     * @param context Context
     * @param number  int
     */
    private static void initBitmap(Context context, int number) {
        int id = R.drawable.countdown_1;
        switch (number) {
            case 5:
                id = R.drawable.countdown_5;
                break;
            case 4:
                id = R.drawable.countdown_4;
                break;
            case 3:
                id = R.drawable.countdown_3;
                break;
            case 2:
                id = R.drawable.countdown_2;
                break;
            case 1:
                id = R.drawable.countdown_1;
                break;
        }
        original = BitmapFactory.decodeResource(context.getResources(), id);
    }

    /**
     * Draw bitmap on canvas
     *
     * @param canvas Canvas
     */
    public void draw(Canvas canvas) {
        Bitmap bitmap = Bitmap.createScaledBitmap(original, width, height, false);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setAlpha(opacity);

        canvas.drawBitmap(bitmap, x, y, paint);
    }

    /**
     * Set bitmap width
     *
     * @param width int
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * Return bitmap width
     *
     * @return int
     */
    public int getWidth() {
        return width;
    }

    /**
     * Set bitmap height
     *
     * @param height int
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * Return bitmap height
     *
     * @return int
     */
    public int getHeight() {
        return height;
    }

    /**
     * Set bitmap opacity
     *
     * @param opacity int
     */
    public void setOpacity(int opacity) {
        this.opacity = opacity;
    }

    /**
     * Return bitmap opacity
     *
     * @return int
     */
    public int getOpacity() {
        return opacity;
    }

    /**
     * Set x position of bitmap
     *
     * @param x int
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Return x position of bitmap
     *
     * @return int
     */
    public int getX() {
        return x;
    }

    /**
     * Set y position of bitmap
     *
     * @param y int
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Return y position of bitmap
     *
     * @return int
     */
    public int getY() {
        return y;
    }

}
