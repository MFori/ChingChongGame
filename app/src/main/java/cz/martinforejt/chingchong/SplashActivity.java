package cz.martinforejt.chingchong;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.LinearLayout;

/**
 * Created by Martin Forejt on 14.05.2016.
 * forejt.martin97@gmail.com
 * class SplashActivity
 * Launch Activity
 */
public class SplashActivity extends AppCompatActivity {

    // Loading
    private LinearLayout container, loader;
    protected Thread t = null;
    protected boolean canStart = true;
    protected boolean isStartingGame = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // FullScreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        container = (LinearLayout) findViewById(R.id.loading_container);
        loader = (LinearLayout) findViewById(R.id.loading_bar);

        Config.init(getApplicationContext());

    }

    /**
     * Start game activity, stop(finish) splash(this) activity
     */
    protected void startGame() {
        if (canStart) {
            isStartingGame = true;
            Intent game = new Intent(SplashActivity.this, GameActivity.class);
            startActivity(game);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
            t.interrupt();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        canStart = true;

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) loader.getLayoutParams();
        params.width = 0;
        loader.setLayoutParams(params);

        container.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (t != null) {
                    t.interrupt();
                    t = null;
                }
                t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int c_width = container.getWidth();
                        try {

                            Thread.sleep(85);
                            while (loader.getWidth() < (c_width - 5)) {
                                Thread.sleep(6);

                                final LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) loader.getLayoutParams();
                                params.width += 1;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        loader.setLayoutParams(params);
                                    }
                                });
                            }

                            Thread.sleep(100);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                startGame();
                            }
                        });
                    }
                });
                t.start();

                if (Build.VERSION.SDK_INT < 16) {
                    container.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    container.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });
    }

    @Override
    public void onPause() {
        canStart = false;

        if(!isStartingGame) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) loader.getLayoutParams();
            params.width = Integer.MAX_VALUE;
            loader.setLayoutParams(params);
        }

        super.onPause();
    }


}
