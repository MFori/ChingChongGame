package cz.martinforejt.chingchong;

import android.content.Context;

/**
 * Created by Martin Forejt on 09.06.2016.
 * forejt.martin97@gmail.com
 */
public class Vibrator {

    protected Context context;
    protected android.os.Vibrator vibrator;
    protected boolean canVibrate = false;

    private static Vibrator instance = null;

    public static Vibrator getInstance(Context context) {
        if (instance == null) {
            instance = new Vibrator(context);
        }

        return instance;
    }

    public Vibrator(Context context) {
        this.context = context;
        vibrator = (android.os.Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        canVibrate = vibrator.hasVibrator();
    }

    public void vibrate(long milis) {
        if (canVibrate && Config.isVibratorOn()) vibrator.vibrate(milis);
    }

}
