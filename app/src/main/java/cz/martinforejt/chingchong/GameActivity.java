/**
 * Created on 14.5.2016
 *
 * @author Martin Forejt
 * @email me@martinforejt.cz
 */
package cz.martinforejt.chingchong;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

public class GameActivity extends AppCompatActivity {

    public static final boolean SLIDE_LEFT = false;
    public static final boolean SLIDE_RIGHT = true;

    static View black_scene;
    protected boolean isAnimatingScene = false;
    protected static String visibleFragment = "menu";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_game);

        black_scene = findViewById(R.id.black_scene);

        // Set default fragment
        MenuFragment fragment = MenuFragment.newInstance();
        changeFragment(fragment, MenuFragment.TAG, false);
    }

    /**
     * Change fragment with animation
     *
     * @param fragment Fragment instance of new fragment
     * @param tag      String
     * @param left     bool animation direction (left-true, right-false)
     */
    protected void changeFragment(Fragment fragment, String tag, boolean left, boolean withScene) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (left) ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        else ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
        ft.replace(R.id.container, fragment, tag);
        ft.commit();
    }

    /**
     * Change fragment without animation
     *
     * @param fragment Fragment instance of new fragment
     * @param tag      String
     */
    public void changeFragment(final Fragment fragment, final String tag, boolean withScene) {
        if (withScene) {
            new android.os.Handler().postDelayed(
                    new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    changeFragment(fragment, tag, false);
                                }
                            });
                        }
                    },
                    500);
            animateBlackScene(1000);
        } else {
            FragmentManager manager = getFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.container, fragment, tag);
            transaction.commit();
        }
    }

    @Override
    public void onBackPressed() {
        switch (visibleFragment) {
            case MenuFragment.TAG:
                super.onBackPressed();
                break;
            case GameFragment.TAG:
                FragmentManager manager = getFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.remove(GameFragment.newInstance(""));
                transaction.commit();
                changeFragment(MenuFragment.newInstance(), MenuFragment.TAG, true);
                break;
            case MultiPlayerFragment.TAG:
            case AboutFragment.TAG:
            case SettingsFragment.TAG:
                changeFragment(MenuFragment.newInstance(), MenuFragment.TAG, SLIDE_LEFT, false);
                break;
        }
    }

    public void playMultiPlayer(View v) {
        changeFragment(MultiPlayerFragment.newInstance(), MultiPlayerFragment.TAG, SLIDE_RIGHT, false);
    }

    public void playSinglePlayer(View v) {
        changeFragment(GameFragment.newInstance(GameFragment.TYPE_SINGLE_PLAYER), GameFragment.TAG, true);
    }

    public void backToMenu(View v) {
        onBackPressed();
    }

    /**
     * @param visibleFragment String - tag
     */
    public static void setVisibleFragment(String visibleFragment) {
        GameActivity.visibleFragment = visibleFragment;
    }

    /**
     * @return String - fragment tag
     */
    public static String getVisibleFragment() {
        return GameActivity.visibleFragment;
    }

    public void animateBlackScene(float duration) {
        if (!isAnimatingScene) {
            black_scene.setVisibility(View.VISIBLE);

            // sleep when alpha = 1 - 100 milis
            duration -= 100;
            // to 1 and back => 2 times
            duration = duration / 2;
            final long dur = (long) (duration * 0.01);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    isAnimatingScene = true;
                    float alpha = 0;
                    boolean toOne = true;
                    boolean isAnimating = true;
                    try {

                        while (isAnimating) {

                            Thread.sleep(dur);

                            if (toOne) {
                                if (alpha > 0.98) {
                                    Thread.sleep(100);
                                    toOne = false;
                                }
                                alpha += 0.01;
                            } else {
                                if (alpha < 0.02) isAnimating = false;
                                alpha -= 0.01;
                            }

                            final float _alpha = alpha;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    black_scene.setAlpha(_alpha);
                                }
                            });
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            black_scene.setVisibility(View.GONE);
                        }
                    });
                    isAnimatingScene = false;
                }
            }).start();
        }
    }

}
