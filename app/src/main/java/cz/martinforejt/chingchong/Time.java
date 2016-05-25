package cz.martinforejt.chingchong;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by Martin Forejt on 24.05.2016.
 * forejt.martin97@gmail.com
 */
public class Time {

    private static Bitmap original = null;

    private Context context;
    protected int number;

    protected int width, height, opacity;
    protected int x, y;

    public Time(Context context, int number) {
        initBitmap(context, number);
    }

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

    public void draw(Canvas canvas) {
        Bitmap bitmap = Bitmap.createScaledBitmap(original, width, height, false);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setAlpha(opacity);

        canvas.drawBitmap(bitmap, x, y, paint);
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setOpacity(int opacity) {
        this.opacity = opacity;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

}
