package cz.martinforejt.chingchong;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

/**
 * Created by Martin Forejt on 15.05.2016.
 * forejt.martin97@gmail.com
 * class Menu
 */
public class Menu implements View.OnClickListener {

    protected ImageButton rate, exit, settings, about;
    protected View view;
    protected GameActivity activity;
    private SoundManager soundManager;
    private Button multi, single;

    /**
     * @param view     View
     * @param activity Activity
     */
    public Menu(View view, Activity activity) {
        this.view = view;
        this.activity = (GameActivity) activity;
        soundManager = SoundManager.getInstance(activity.getApplicationContext());
    }

    /**
     * Initialize menu layout elements
     */
    public void init() {
        rate = (ImageButton) view.findViewById(R.id.menu_rate);
        rate.setOnClickListener(Menu.this);

        exit = (ImageButton) view.findViewById(R.id.menu_exit);
        exit.setOnClickListener(Menu.this);

        settings = (ImageButton) view.findViewById(R.id.menu_settings);
        settings.setOnClickListener(Menu.this);

        about = (ImageButton) view.findViewById(R.id.menu_about);
        about.setOnClickListener(Menu.this);

        multi = (Button) view.findViewById(R.id.menu_button_multiplayer);
        single = (Button) view.findViewById(R.id.menu_button_singleplayer);
        setFont();
    }

    /**
     * Set chlorinr (assets/fonts/chlorinr.ttf) font to menu buttons
     */
    private void setFont() {
        Typeface typeface = Typeface.createFromAsset(activity.getAssets(), "fonts/chlorinr.ttf");
        multi.setTypeface(typeface);
        single.setTypeface(typeface);
    }

    @Override
    public void onClick(final View view) {

        Fragment fragment = null;
        String tag = "";

        try {
            soundManager.play(SoundManager.EFFECT_MENU);
        } catch (Exception e) {
            Log.d("EX", "MENU");
        }

        switch (view.getId()) {
            case R.id.menu_rate:
                showStore();
                break;
            case R.id.menu_exit:
                activity.exitDialog();
                break;
            case R.id.menu_settings:
                fragment = new SettingsFragment();
                tag = SettingsFragment.TAG;
                break;
            case R.id.menu_about:
                fragment = new AboutFragment();
                tag = AboutFragment.TAG;
                break;
        }

        if (fragment != null) {
            changeFragment(fragment, tag);
        }
    }

    /**
     * Change the fragment
     * Animate alpha of menu corner buttons (rate, exit, settings, about)
     *
     * @param fragment Fragment
     * @param tag      String
     */
    private void changeFragment(Fragment fragment, String tag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                float alpha = 1;
                try {
                    while (alpha > 0) {
                        Thread.sleep(30);
                        alpha -= 0.07;
                        final float _alpha = alpha;
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                rate.setAlpha(_alpha);
                                exit.setAlpha(_alpha);
                                settings.setAlpha(_alpha);
                                about.setAlpha(_alpha);
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        activity.changeFragment(fragment, tag, GameActivity.SLIDE_RIGHT, false);
    }

    /**
     * Show app in google play store for rating
     */
    private void showStore() {
        final String appPackageName = activity.getPackageName(); // getPackageName() from Context or Activity object
        try {
            activity.startActivity(
                    new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName))
            );
        } catch (android.content.ActivityNotFoundException anfe) {
            activity.startActivity(
                    new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName))
            );
        }
    }

}
