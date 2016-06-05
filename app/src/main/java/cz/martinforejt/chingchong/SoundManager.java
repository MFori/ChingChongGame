package cz.martinforejt.chingchong;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;

/**
 * Created by Martin Forejt on 05.06.2016.
 * forejt.martin97@gmail.com
 * class SoundManager
 */
public class SoundManager {

    private static SoundManager instance = null;
    private Context context;

    SoundPool soundPool;
    protected int soundIds[] = new int[2];
    protected boolean isLoaded = false;

    /**
     * @param context Context
     * @return SoundManager
     */
    public static SoundManager getInstance(Context context) {
        if (instance == null) {
            instance = new SoundManager(context);
        }

        return instance;
    }

    /**
     * Private construct
     *
     * @param context Context
     */
    private SoundManager(Context context) {
        this.context = context;
        initSoundPool();
        loadEffects();
    }

    /**
     * Creates soundPool
     */
    private void initSoundPool() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            soundPool = createNewSoundPool();
        } else {
            soundPool = createOldSoundPool();
        }
    }

    /**
     * Creates soundPoll for lollipop and higher
     *
     * @return SoundPoll
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected SoundPool createNewSoundPool() {

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        return new SoundPool.Builder()
                .setAudioAttributes(audioAttributes)
                .build();

    }

    /**
     * Creates soundPool for api low than lollipop
     *
     * @return SoundPoll
     */
    @SuppressWarnings("deprecation")
    protected SoundPool createOldSoundPool() {

        return new SoundPool(5, AudioManager.STREAM_MUSIC, 0);

    }

    /**
     * Load effects to soundPool
     */
    protected void loadEffects() {
        try {
            soundIds[EFFECT_MENU] = soundPool.load(context, RES_ID_MENU, 1);
            soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                    isLoaded = true;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Effects constants
     */
    public static final int EFFECT_MENU = 0;
    public static final int EFFECT_START = 1;

    /**
     * Resources
     */
    private static final int RES_ID_MENU = R.raw.beep;

    /**
     * Play sound effect
     *
     * @param effect int
     */
    public void play(int effect) {
        if (Config.isIsSoundOn() && isLoaded) {
            try {
                soundPool.play(soundIds[effect], 1, 1, 1, 0, 1.0f);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("Cant play: ", "EFFECT: " + String.valueOf(effect));
            }
        }
    }

    public void startBackground() {

    }

    public void stopBackground() {

    }

}
