package cz.martinforejt.chingchong;

import android.content.Context;
import android.content.Intent;

/**
 * Created by Martin Forejt on 05.06.2016.
 * forejt.martin97@gmail.com
 */
public class BackgroundMusic {

    Context context;
    MusicThread musicThread;

    protected static final String TRACK = "track";
    public static final int TRACK_01 = R.raw.back;
    public static final int TRACK_02 = R.raw.cdk_sunday;

    private static BackgroundMusic instance = null;

    protected int track;

    public static BackgroundMusic getInstance(Context context) {
        if (instance == null) {
            instance = new BackgroundMusic(context);
        }
        return instance;
    }

    /**
     * Construct
     *
     * @param context Context
     */
    private BackgroundMusic(Context context) {
        this.context = context;
    }

    public void setTrack(int track) {
        this.track = track;
    }

    /**
     * Start playing music
     */
    public void start() {
        if (Config.isIsSoundOn()) {
            musicThread = new MusicThread();
            musicThread.start();
        }
    }

    /**
     * Stop playing music
     */
    public void stop() {
        if (musicThread != null && !musicThread.isInterrupted()) {
            musicThread.end();
        }
    }

    /**
     * Music Thread
     */
    private class MusicThread extends Thread {

        @Override
        public void run() {
            Intent music = new Intent(context.getApplicationContext(), Music.class);
            music.putExtra(TRACK, track);
            context.startService(music);
        }

        /**
         * Stop service
         * interrupt self
         */
        public void end() {
            context.stopService(new Intent(context.getApplicationContext(), Music.class));
            this.interrupt();
        }

    }
}
