package cz.martinforejt.chingchong;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by Martin Forejt on 14.05.2016.
 * forejt.martin97@gmail.com
 * class GameActivity
 * Main game activity started from splash screen (activity)
 */
public class GameActivity extends AppCompatActivity {

    public static final boolean SLIDE_LEFT = false;
    public static final boolean SLIDE_RIGHT = true;

    static View black_scene;
    protected boolean isAnimatingScene = false;
    protected static String visibleFragment = "menu";

    SoundManager soundManager;
    BackgroundMusic backgroundMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_game);

        //Config.init(getApplicationContext()); edit: initialize config in splash activity

        if (Config.isIsSoundOn()) setVolumeControlStream(AudioManager.STREAM_MUSIC);

        black_scene = findViewById(R.id.black_scene);

        /*ImageView a = (ImageView) findViewById(R.id.backgroundImg);
        a.setImageResource(R.drawable.background);*/

        soundManager = SoundManager.getInstance(getApplicationContext());
        backgroundMusic = BackgroundMusic.getInstance(getApplicationContext());
        backgroundMusic.setTrack(BackgroundMusic.TRACK_02);

        // Set default fragment
        Fragment fragment;
        String tag;
        if (Config.getName().equals("")) {
            fragment = new NewPlayerFragment();
            tag = NewPlayerFragment.TAG;
        } else {
            fragment = new MenuFragment();
            tag = MenuFragment.TAG;
        }
        changeFragment(fragment, tag, false);
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
        soundManager.play(SoundManager.EFFECT_MENU);
        switch (visibleFragment) {
            case MenuFragment.TAG:
            case NewPlayerFragment.TAG:
                // open exit dialog
                exitDialog();
                break;
            case GameFragment.TAG:
                FragmentManager manager = getFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.remove(GameFragment.newInstance(new OfflinePlayer("Martin", "Pepa")));
                transaction.commit();
                changeFragment(new MenuFragment(), MenuFragment.TAG, true);
                break;
            case CreateGameFragment.TAG:
            case JoinGameFragment.TAG:
                changeFragment(new MultiPlayerFragment(), MultiPlayerFragment.TAG, SLIDE_LEFT, false);
                break;
            case MultiPlayerFragment.TAG:
            case AboutFragment.TAG:
            case SettingsFragment.TAG:
            case ResultFragment.TAG:
                changeFragment(new MenuFragment(), MenuFragment.TAG, SLIDE_LEFT, false);
                break;
        }
    }

    /**
     * Show exit dialog
     */
    public void exitDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(GameActivity.this);
        View view = layoutInflater.inflate(R.layout.exit_dialog, null);

        final AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
        builder.setView(view);

        final Button btnYes = (Button) view.findViewById(R.id.dialogBtnYes);
        final Button btnNo = (Button) view.findViewById(R.id.dialogBtnNo);

        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);

        // Close dialog
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // Close app
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GameActivity.super.onBackPressed();
            }
        });

        dialog.show();
    }

    /**
     * Click on MultiPlayer button
     *
     * @param v Button
     */
    public void playMultiPlayer(View v) {
        soundManager.play(SoundManager.EFFECT_MENU);
        changeFragment(new MultiPlayerFragment(), MultiPlayerFragment.TAG, SLIDE_RIGHT, false);
    }

    /**
     * Click on SinglePlayer button
     *
     * @param v Button
     */
    public void playSinglePlayer(View v) {
        soundManager.play(SoundManager.EFFECT_MENU);
        changeFragment(GameFragment.newInstance(new OfflinePlayer(Config.getName(), Config.RIVAL_OFFLINE_NAME)), GameFragment.TAG, true);
    }

    /**
     * Click on Create MultiPlayer game (create server)
     *
     * @param v Button
     */
    public void createGame(View v) {
        if (isInternetConnection()) {
            soundManager.play(SoundManager.EFFECT_MENU);
            changeFragment(CreateGameFragment.newInstance(), CreateGameFragment.TAG, SLIDE_RIGHT, false);
        } else Toast.makeText(this, "You must be connected to WiFi.", Toast.LENGTH_SHORT).show();
    }

    /**
     * Click on Join MultiPlayer game (create client)
     *
     * @param v Button
     */
    public void joinGame(View v) {
        if (isInternetConnection()) {
            soundManager.play(SoundManager.EFFECT_MENU);
            changeFragment(JoinGameFragment.newInstance(), JoinGameFragment.TAG, SLIDE_RIGHT, false);
        } else
            Toast.makeText(this, "You must be connected to WiFi.", Toast.LENGTH_SHORT).show();
    }

    /**
     * Click on back arrow
     *
     * @param v Button
     */
    public void backToMenu(View v) {
        onBackPressed();
    }

    /**
     * Click on credits text ( 2016 MfStudio )
     *
     * @param v TextView
     */
    public void creditsClick(View v) {
        try {
            startActivity(
                    new Intent(Intent.ACTION_VIEW, Uri.parse("market://dev?id=6858337862983523992"))
            );
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(
                    new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/dev?id=6858337862983523992"))
            );
        }
    }

    /**
     * Save tag of visible fragment
     *
     * @param visibleFragment String - tag
     */
    public static void setVisibleFragment(String visibleFragment) {
        GameActivity.visibleFragment = visibleFragment;
    }

    /**
     * Return tag of visible fragment
     *
     * @return String - fragment tag
     */
    public static String getVisibleFragment() {
        return GameActivity.visibleFragment;
    }

    /**
     * Animate black screen with alpha
     *
     * @param duration float - permeation time
     */
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

    /**
     * Check if device is connected to wifi
     *
     * @return bool
     */
    public boolean isInternetConnection() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return wifi.isConnected();
    }

    @Override
    public void onResume() {
        backgroundMusic.start();
        super.onResume();
    }

    @Override
    public void onPause() {
        backgroundMusic.stop();
        super.onPause();
    }

}
