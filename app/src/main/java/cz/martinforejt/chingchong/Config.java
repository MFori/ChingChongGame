package cz.martinforejt.chingchong;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Martin Forejt on 04.06.2016.
 * forejt.martin97@gmail.com
 * class Config
 */
public class Config {

    private static Context context;
    private static SharedPreferences sharedPreferences;

    /**
     * Initialize config, call when app start
     *
     * @param context Context
     */
    public static void init(Context context) {
        Config.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        initSound();
        initVibrator();
        resetScore();
    }

    /*****************************************************
     * SOUND
     ****************************************************/
    private static boolean isSoundOn = true;
    private static final String SOUND = "sound";

    /**
     *
     */
    private static void initSound() {
        isSoundOn = sharedPreferences.getBoolean(SOUND, true);
    }

    /**
     * Check if sound is on
     *
     * @return bool
     */
    public static boolean isIsSoundOn() {
        return isSoundOn;
    }

    /**
     * @param isSoundOn bool
     */
    public static void setIsSoundOn(boolean isSoundOn) {
        Config.isSoundOn = isSoundOn;

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SOUND, isSoundOn);
        editor.apply();
    }

    /*****************************************************
     * Vibrations
     ****************************************************/
    private static boolean isVibratorOn = false;
    private static final String VIBRATOR = "vibrations";

    /**
     *
     */
    private static void initVibrator() {
        isVibratorOn = sharedPreferences.getBoolean(VIBRATOR, false);
    }

    /**
     * @return bool
     */
    public static boolean isVibratorOn() {
        return isVibratorOn;
    }

    /**
     * @param isVibratorOn bool
     */
    public static void setVibrator(boolean isVibratorOn) {
        Config.isVibratorOn = isVibratorOn;

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(VIBRATOR, Config.isVibratorOn);
        editor.apply();
    }

    /*****************************************************
     * Name
     ****************************************************/
    private static final String PREF_NAME = "config";
    private static final String NAME = "player_name";
    public static final String RIVAL_OFFLINE_NAME = "Android";
    private static String name = null;

    /**
     * Return player (user) name
     *
     * @return String
     */
    public static String getName() {
        if (name == null) {
            name = sharedPreferences.getString(NAME, "");
        }
        return name;
    }

    /**
     * Set player (user) name
     *
     * @param name String
     */
    public static void setName(String name) {
        Config.name = name.trim();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(NAME, Config.name);
        editor.apply();
    }

    /*****************************************************
     * Score
     ****************************************************/
    private static final String SCORE_MY = "score_m";
    private static final String SCORE_RIVAL = "score_r";

    /**
     * @return int
     */
    public static int getMyScore() {
        return sharedPreferences.getInt(SCORE_MY, 0);
    }

    /**
     * @return int
     */
    public static int getRivalScore() {
        return sharedPreferences.getInt(SCORE_RIVAL, 0);
    }

    /**
     * @param score int
     */
    public static void setMyScore(int score) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SCORE_MY, score);
        editor.apply();
    }

    /**
     * @param score int
     */
    public static void setScoreRival(int score) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SCORE_RIVAL, score);
        editor.apply();
    }

    /**
     * Reset score
     */
    public static void resetScore() {
        setMyScore(0);
        setScoreRival(0);
    }

}
