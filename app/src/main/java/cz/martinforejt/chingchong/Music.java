package cz.martinforejt.chingchong;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

/**
 * Created by Martin Forejt on 16.11.2015.(ShiftSquares) edit: 05.06.2016.
 * forejt.martin97@gmail.com
 * class Music
 */
public class Music extends Service {

    protected static final String TRACK = "track";

    public static boolean isRunning = false;
    private MediaPlayer player;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //setMusicOptions(this, true, 100, 100, R.raw.back);
    }

    /**
     * Set music options
     *
     * @param context   Context
     * @param isLooped  bool
     * @param rVolume   int
     * @param lVolume   int
     * @param soundFile int
     */
    private void setMusicOptions(Context context, boolean isLooped, int rVolume, int lVolume, int soundFile) {
        player = MediaPlayer.create(context, soundFile);
        player.setLooping(isLooped);
        player.setVolume(lVolume, rVolume);
    }

    /**
     * @param intent  Intent
     * @param flags   int
     * @param startId int
     * @return int
     */
    public int onStartCommand(Intent intent, int flags, int startId) {

        int track = R.raw.back;

        try{
            track = intent.getIntExtra(TRACK, R.raw.back);
        } catch (Exception e) {
            e.printStackTrace();
        }

        setMusicOptions(this, true, 100, 100, track);

        try {
            player.start();
            isRunning = true;
        } catch (Exception e) {
            isRunning = false;
            player.stop();
        }
        return 1;
    }

    public void onStart(Intent intent, int startId) {
    }


    private void onStop() {
        isRunning = false;
    }

    public IBinder onUnBind(Intent arg0) {
        return null;
    }

    @Override
    public void onDestroy() {
        player.stop();
        player.release();
        onStop();
    }

    @Override
    public void onLowMemory() {
        player.stop();
        onStop();
    }
}

