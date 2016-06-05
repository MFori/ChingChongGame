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

    protected int track;

    /**
     * Construct
     *
     * @param context Context
     */
    public BackgroundMusic(Context context, int track) {
        this.context = context;
        this.track = track;
    }

    /**
     * Start playing music
     */
    public void start() {
        musicThread = new MusicThread();
        musicThread.start();
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
