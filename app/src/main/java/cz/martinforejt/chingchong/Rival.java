package cz.martinforejt.chingchong;

/**
 * Created by Martin Forejt on 16.05.2016.
 * forejt.martin97@gmail.com
 */
public class Rival {

    private String name;
    private int thumbs = 2;
    private int chongs;
    protected int showsThumbs = 0;
    private boolean isHisTurn;

    private boolean hasData = false;

    public Rival(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Rival setThumbs(int thumbs) {
        this.thumbs = thumbs;
        return this;
    }

    public int getThumbs() {
        return thumbs;
    }

    public Rival setShowsThumbs(int showsThumbs) {
        this.showsThumbs = showsThumbs;
        return this;
    }

    public int getShowsThumbs() {
        return showsThumbs;
    }

    public boolean hasData() {
        return hasData;
    }

    public Rival hasData(boolean hasData) {
        this.hasData = hasData;
        return this;
    }

    public Rival setChongs(int chongs) {
        this.chongs = chongs;
        return this;
    }

    public int getChongs() {
        return chongs;
    }

    public boolean isHisTurn() {
        return isHisTurn;
    }

    public Rival hisTurn(boolean turn) {
        isHisTurn = turn;
        return this;
    }

}
