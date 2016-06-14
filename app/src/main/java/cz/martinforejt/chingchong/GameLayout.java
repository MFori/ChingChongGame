package cz.martinforejt.chingchong;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by Martin Forejt on 14.05.2016.
 * forejt.martin97@gmail.com
 * <p/>
 * Manipulating with xFraction: Sliding animation when changing fragments - full width X
 */
public class GameLayout extends FrameLayout {

    public GameLayout(Context context) {
        super(context);
    }

    public GameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * @return float
     */
    public float getXFraction() {
        if (getWidth() == 0) {
            return 0;
        }
        return getX() / getWidth();
    }

    /**
     * @param xFraction float
     */
    public void setXFraction(float xFraction) {
        final int width = getWidth();
        setX((width > 0) ? (xFraction * width) : -9999);
    }

}
