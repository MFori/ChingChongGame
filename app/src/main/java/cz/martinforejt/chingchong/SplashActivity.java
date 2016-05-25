/**
 * Created on 14.5.2016
 *
 * @author Martin Forejt
 * @email me@martinforejt.cz
 */
package cz.martinforejt.chingchong;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

/**
 * class SplashActivity
 * Launch Activity
 */
public class SplashActivity extends AppCompatActivity {

    // Loading
    LinearLayout container, loader;
    Thread t = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // FullScreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);

        container = (LinearLayout) findViewById(R.id.loading_container);
        loader = (LinearLayout) findViewById(R.id.loading_bar);

        container.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (t == null) {
                    t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            int c_width = container.getWidth();
                            try {

                                Thread.sleep(100);
                                while (loader.getWidth() < (c_width - 1)) {
                                    Thread.sleep(7);

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
                }
            }
        });

    }

    /**
     * Start game activity, stop(finish) splash(this) activity
     */
    protected void startGame() {
        Intent game = new Intent(SplashActivity.this, GameActivity.class);
        startActivity(game);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
        t.interrupt();
    }

    @Override
    public void onPause(){
        super.onPause();
        t.interrupt();
        t = null;
        finish();
    }

}
