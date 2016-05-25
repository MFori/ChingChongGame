package cz.martinforejt.chingchong;

/**
 * Created by Martin Forejt on 16.05.2016.
 * forejt.martin97@gmail.com
 */
public abstract class Player implements PlayerInterface {

    protected String name;

    protected int thumbs = 2;
    protected int showsThumbs = 0;
    protected int chongs;
    protected boolean isHisTurn;

    protected Rival rival;

    public Player(String name, String rivalName) {
        this.name = name;
        rival = new Rival(rivalName);
    }

    public Rival getRival() {
        return rival;
    }

    public Player setThumbs(int thumbs) {
        this.thumbs = thumbs;
        return this;
    }

    public int getThumbs() {
        return thumbs;
    }

    public Player setShowsThumbs(int showsThumbs) {
        this.showsThumbs = showsThumbs;
        return this;
    }

    public int getShowsThumbs() {
        return showsThumbs;
    }

    public boolean isHisTurn() {
        return isHisTurn;
    }

    public int getChongs() {
        return chongs;
    }

    public Player setChongs(int chongs) {
        this.chongs = chongs;
        return this;
    }

    public Player hisTurn(boolean turn) {
        isHisTurn = turn;
        return this;
    }

    public String getName() {
        return name;
    }

}
