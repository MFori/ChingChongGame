package cz.martinforejt.chingchong;

import java.io.Serializable;

/**
 * Created by Martin Forejt on 16.05.2016.
 * forejt.martin97@gmail.com
 * abstract class Player
 */
public abstract class Player implements PlayerInterface, Serializable {

    protected String name;

    protected int thumbs = 2;
    protected int showsThumbs = 0;
    protected int chongs = -1;
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
     *
     */
    abstract void onDestroy();

    /**
     *
     */
    abstract void onPause();

    /**
     *
     */
    abstract void onResume();
}
