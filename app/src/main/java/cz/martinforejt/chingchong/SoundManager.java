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
    protected int soundIds[] = new int[7];
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    soundIds[EFFECT_MENU] = soundPool.load(context, RES_ID_MENU, 1);
                    soundIds[VOICE_0] = soundPool.load(context, RES_VOICE_0, 1);
                    soundIds[VOICE_1] = soundPool.load(context, RES_VOICE_1, 1);
                    soundIds[VOICE_2] = soundPool.load(context, RES_VOICE_2, 1);
                    soundIds[VOICE_3] = soundPool.load(context, RES_VOICE_3, 1);
                    soundIds[VOICE_4] = soundPool.load(context, RES_VOICE_4, 1);
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
        }).start();
    }

    /**
     * Effects constants
     */
    public static final int EFFECT_MENU = 0;
    public static final int EFFECT_START = 1;
    public static final int VOICE_0 = 2;
    public static final int VOICE_1 = 3;
    public static final int VOICE_2 = 4;
    public static final int VOICE_3 = 5;
    public static final int VOICE_4 = 6;

    /**
     * Resources
     */
    private static final int RES_ID_MENU = R.raw.beep;
    private static final int RES_VOICE_0 = R.raw.voice_zero;
    private static final int RES_VOICE_1 = R.raw.voice_one;
    private static final int RES_VOICE_2 = R.raw.voice_two;
    private static final int RES_VOICE_3 = R.raw.voice_tree;
    private static final int RES_VOICE_4 = R.raw.voice_four;

    /**
     * Play sound effect
     *
     * @param effect int
     */
    public void play(int effect) {
        Log.d("SoundManager:", "Play: " + String.valueOf(effect));
        if (Config.isIsSoundOn() && isLoaded) {
            try {
                soundPool.play(soundIds[effect], 1, 1, 1, 0, 1.0f);
                Log.d("SoundManager:", "Playing: " + String.valueOf(effect));
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("Cant play: ", "EFFECT: " + String.valueOf(effect));
            }
        }
    }
}
