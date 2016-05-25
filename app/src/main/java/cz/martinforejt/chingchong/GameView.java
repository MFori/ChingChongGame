package cz.martinforejt.chingchong;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Martin Forejt on 15.05.2016.
 * forejt.martin97@gmail.com
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private Thread thread;
    private SurfaceHolder holder;
    Bitmap time;
    Paint paint;

    int gameWidth, gameHeight;
    int timeWidth, timeHeight;

    float Timeratio;
    int TimeWidth, TimeHeight;

    static GameView instance;
    Context context;

    private boolean countDownAnimating = false;

    public GameView(Context context) {
        super(context);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;
        instance = this;

        paint = new Paint();
        paint.setAntiAlias(true);

        holder = getHolder();
        holder.addCallback(this);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        gameWidth = width;
        gameHeight = height;

        Bitmap originalTime = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        int originalWidth = originalTime.getWidth();
        int originalHeight = originalTime.getHeight();
        float ratio = originalHeight / originalWidth;
        int newWidth = width / 6;

        Timeratio = ratio;
        TimeWidth = newWidth;
        TimeHeight = (int) (newWidth * ratio);

        time = Bitmap.createScaledBitmap(originalTime, newWidth, (int) (newWidth * ratio), false);
        timeWidth = time.getWidth();
        timeHeight = time.getHeight();

        time = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Canvas canvas = null;
        canvas = holder.lockCanvas();
        if (canvas != null) {
            canvas.drawColor(Color.BLUE);
            holder.unlockCanvasAndPost(canvas);
        }
    }

    public void animateCountDown() {
        countDownAnimating = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                int width;
                int alpha;
                Canvas canvas;
                try {
                    for (int i = 5; i > 0; i--) {
                        width = (TimeWidth * 6) / 7;
                        alpha = 255;
                        Time t = new Time(context, i);
                        long start = System.currentTimeMillis();
                        while ((System.currentTimeMillis() - start) < 1200) {
                            Log.d("TIME", String.valueOf(System.currentTimeMillis() - start));
                            canvas = holder.lockCanvas();
                            if (canvas != null) {
                                canvas.drawColor(Color.BLUE);
                                t.setOpacity(alpha);
                                t.setWidth(width);
                                t.setHeight((int) (width * Timeratio));
                                t.setX(gameWidth / 2 - width / 2);
                                t.setY(TimeHeight / 2 - ((int) (width * Timeratio)) / 2);
                                t.draw(canvas);
                                holder.unlockCanvasAndPost(canvas);
                                width += 1;
                                if (width > (TimeWidth * 6) / 8) alpha -= 1;
                            }
                        }
                    }
                    clear();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                countDownAnimating = false;
            }
        }).start();
    }

    public boolean isCountDownAnimating() {
        return countDownAnimating;
    }

    public GameView setCountDownAnimationg() {
        countDownAnimating = true;
        return this;
    }

    private void clear() {
        Canvas canvas = holder.lockCanvas();
        if (canvas != null) {
            canvas.drawColor(Color.BLUE);
            holder.unlockCanvasAndPost(canvas);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    private class TimeThread extends Thread {
        int width;
        int alpha = 200;

        public void stopThread() {
            width = Integer.MAX_VALUE;
            alpha = -width;
        }

        @Override
        public void run() {
            Canvas canvas = null;
            try {
                width = (TimeWidth * 5) / 6;
                int number = 1;
                Time t = new Time(context, 1);
                while (width < TimeWidth) {
                    canvas = holder.lockCanvas();
                    if (canvas != null) {
                        canvas.drawColor(Color.BLUE);
                        t.setOpacity(alpha);
                        t.setWidth(width);
                        t.setHeight((int) (width * Timeratio));
                        t.setX(gameWidth / 2 - width / 2);
                        t.setY(TimeHeight / 2 - ((int) (width * Timeratio)) / 2);
                        t.draw(canvas);
                        //drawCountDown(canvas, width, alpha, number);
                        holder.unlockCanvasAndPost(canvas);
                        width += 2;
                    }
                }
                while (alpha > 0) {
                    canvas = holder.lockCanvas();
                    if (canvas != null) {
                        canvas.drawColor(Color.BLUE);
                        //drawCountDown(canvas, width, alpha, number);
                        holder.unlockCanvasAndPost(canvas);
                        alpha -= 3;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public static GameView getInstance() {
        return instance;
    }

}
