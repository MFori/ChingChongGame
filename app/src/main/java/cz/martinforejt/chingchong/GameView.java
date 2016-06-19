package cz.martinforejt.chingchong;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Martin Forejt on 16.05.2016.
 * forejt.martin97@gmail.com
 * class GameView
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder holder;
    Bitmap time;
    Paint paint;

    int gameWidth, gameHeight;
    int timeWidth, timeHeight;

    float Timeratio;
    int TimeWidth, TimeHeight;

    static GameView instance;
    Context context;

    Bitmap background;
    Paint backPaint;

    private boolean countDownAnimating = false;

    /**
     * Construct
     *
     * @param context Context
     */
    public GameView(Context context) {
        super(context);
    }

    /**
     * Construct
     *
     * @param context Context
     * @param attrs   AttributeSet
     */
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

        // Background:
        BitmapFactory.Options BackOptions = new BitmapFactory.Options();
        BackOptions.inScaled = false;
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.background, BackOptions);
        //float backRatio = (float) bitmap.getHeight() / (float) bitmap.getWidth();
        //background = Bitmap.createScaledBitmap(bitmap, gameWidth, (int) (gameWidth * backRatio), true);
        background = Bitmap.createScaledBitmap(bitmap, gameWidth, gameHeight, true);

        backPaint = new Paint();
        backPaint.setAntiAlias(true);
        backPaint.setFilterBitmap(true);
        backPaint.setDither(true);

        Canvas c = holder.lockCanvas();
        c.drawBitmap(background, 0, 0, backPaint);
        holder.unlockCanvasAndPost(c);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Canvas canvas = null;
        canvas = holder.lockCanvas();
        if (canvas != null) {
            //canvas.drawColor(getResources().getColor(R.color.backgroundbrown));
            //canvas.drawBitmap(background, 0, 0, null);
            holder.unlockCanvasAndPost(canvas);
        }
    }

    /**
     * Animate countdown from 5 to 0
     */
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

                        // create number object
                        CountDown number = new CountDown(context, i);
                        long start = System.currentTimeMillis();
                        // 1200 milis for number
                        while ((System.currentTimeMillis() - start) < 1200 && countDownAnimating) {
                            canvas = holder.lockCanvas();
                            if (canvas != null) {
                                //canvas.drawColor(getResources().getColor(R.color.backgroundbrown));
                                canvas.drawBitmap(background, 0, 0, backPaint);
                                int x = gameWidth / 2 - width / 2;
                                int y = TimeHeight / 2 - ((int) (width * Timeratio) / 2);
                                int height = (int) (width * Timeratio);
                                drawCountDown(canvas, number, width, height, x, y, alpha);
                                holder.unlockCanvasAndPost(canvas);
                                width += 1;
                                if (width > (TimeWidth * 6) / 8) alpha -= 1;
                            }
                        }
                        if (!countDownAnimating) break;
                    }
                    clear();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                countDownAnimating = false;
            }
        }).start();
    }

    /**
     * Draw number bitmap on canvas
     *
     * @param canvas Canvas
     * @param number CountDown
     * @param width  int
     * @param height int
     * @param x      int
     * @param y      int
     * @param alpha  int
     */
    private void drawCountDown(Canvas canvas, CountDown number, int width, int height, int x, int y, int alpha) {
        number.setOpacity(alpha);
        number.setWidth(width);
        number.setHeight((int) (width * Timeratio));
        number.setX(gameWidth / 2 - width / 2);
        number.setY(TimeHeight / 2 - ((int) (width * Timeratio)) / 2);
        number.draw(canvas);
    }

    /**
     * Check if still countdown animation
     *
     * @return bool
     */
    public boolean isCountDownAnimating() {
        return countDownAnimating;
    }

    /**
     *
     */
    public void stopCountDown() {
        countDownAnimating = false;
    }

    /**
     * @return GameView
     */
    public GameView setCountDownAnimation() {
        countDownAnimating = true;
        return this;
    }

    /**
     * Draw only background2
     */
    private void clear() {
        Canvas canvas = holder.lockCanvas();
        if (canvas != null) {
            //canvas.drawColor(getResources().getColor(R.color.backgroundbrown));
            canvas.drawBitmap(background, 0, 0, backPaint);
            holder.unlockCanvasAndPost(canvas);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    /**
     * Get instance of GameView
     *
     * @return GameView
     */
    public static GameView getInstance() {
        return instance;
    }

}
