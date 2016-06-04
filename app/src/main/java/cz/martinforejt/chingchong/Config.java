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

    private static final String PREF_NAME = "config";
    private static final String NAME = "player_name";

    public static final String RIVAL_OFFLINE_NAME = "Android";

    private static String name = null;

    /**
     * Initialize config, call when app start
     *
     * @param context Context
     */
    public static void init(Context context) {
        Config.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

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

}
