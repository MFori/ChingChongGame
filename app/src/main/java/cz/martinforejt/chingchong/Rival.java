package cz.martinforejt.chingchong;

import java.io.Serializable;

/**
 * Created by Martin Forejt on 16.05.2016.
 * forejt.martin97@gmail.com
 * class Rival
 */
public class Rival implements Serializable {

    private String name;
    private int thumbs = 2;
    private int chongs;
    protected int showsThumbs = 0;
    private boolean isHisTurn;

    private boolean hasData = false;

    public Rival(String name) {
        this.name = name;
    }

    /**
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * @param thumbs int
     * @return Rival
     */
    public Rival setThumbs(int thumbs) {
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
     * @return Rival
     */
    public Rival setShowsThumbs(int showsThumbs) {
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
    public boolean hasData() {
        return hasData;
    }

    /**
     * @param hasData bool
     * @return Rival
     */
    public Rival hasData(boolean hasData) {
        this.hasData = hasData;
        return this;
    }

    /**
     * @param chongs int
     * @return Rival
     */
    public Rival setChongs(int chongs) {
        this.chongs = chongs;
        return this;
    }

    /**
     * @return int
     */
    public int getChongs() {
        return chongs;
    }

    /**
     * @return bool
     */
    public boolean isHisTurn() {
        return isHisTurn;
    }

    /**
     * @param turn bool
     * @return Rival
     */
    public Rival hisTurn(boolean turn) {
        isHisTurn = turn;
        return this;
    }

}
