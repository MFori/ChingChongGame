package cz.martinforejt.chingchong;

import java.io.Serializable;

/**
 * Created by Martin Forejt on 16.05.2016.
 * forejt.martin97@gmail.com
 * abstract class Player
 */
public abstract class Player implements PlayerInterface, Serializable {

    protected String name;

    public static final int DEFAULT_THUMBS = 2;
    public static final int DEFAULT_SHOWS_THUMBS = 0;
    public static final int DEFAULT_CHONGS = -1;

    protected int thumbs = DEFAULT_THUMBS;
    protected int showsThumbs = DEFAULT_SHOWS_THUMBS;
    protected int chongs = DEFAULT_CHONGS;
    protected boolean isHisTurn = false;

    public Rival rival;

    public Player(String name, String rivalName) {
        this.name = name;
        rival = new Rival(rivalName);
    }

    /**
     * @return Rival
     */
    public Rival getRival() {
        return rival;
    }

    /**
     * @param thumbs int
     * @return Player
     */
    public Player setThumbs(int thumbs) {
        this.thumbs = thumbs;
        return this;
    }

    /**
     * @return int
     */
    public int getThumbs() {
        return thumbs;
    }

    /**
     * @param showsThumbs int
     * @return Player
     */
    public Player setShowsThumbs(int showsThumbs) {
        this.showsThumbs = showsThumbs;
        return this;
    }

    /**
     * @return int
     */
    public int getShowsThumbs() {
        return showsThumbs;
    }

    /**
     * @return bool
     */
    public boolean isHisTurn() {
        return isHisTurn;
    }

    /**
     * @return int
     */
    public int getChongs() {
        return chongs;
    }

    /**
     * @param chongs int
     * @return Player
     */
    public Player setChongs(int chongs) {
        this.chongs = chongs;
        return this;
    }

    /**
     * @param turn bool
     * @return Player
     */
    public Player hisTurn(boolean turn) {
        isHisTurn = turn;
        return this;
    }

    /**
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * On destroy
     */
    abstract void onDestroy();

    /**
     * On pause
     */
    abstract void onPause();

    /**
     * On resume
     */
    abstract void onResume();
}
