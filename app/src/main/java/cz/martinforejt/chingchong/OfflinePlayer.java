package cz.martinforejt.chingchong;

import java.util.Random;

/**
 * Created by Martin Forejt on 16.05.2016.
 * forejt.martin97@gmail.com
 * class OfflinePlayer
 */
public class OfflinePlayer extends Player {

    public OfflinePlayer(String name, String rivalName) {
        super(name, rivalName);
    }

    /**
     *
     */
    public void sendData() {

        int allThumbs = rival.getThumbs() + this.getThumbs();

        // rival chongs choose
        if (rival.isHisTurn()) {
            Random random = new Random();
            int ch = random.nextInt(allThumbs + 1);
            rival.setChongs(ch);
        }

        // rival thumbs choose
        Random r = new Random();
        int th = r.nextInt(rival.getThumbs() + 1);
        rival.setShowsThumbs(th);

        int visibleThumbs = rival.getShowsThumbs() + this.getShowsThumbs();

        if (rival.isHisTurn()) {
            if (visibleThumbs == rival.getChongs()) rival.setThumbs(rival.getThumbs() - 1);
        } else if (this.isHisTurn) {
            if (visibleThumbs == this.getChongs()) this.setThumbs(this.getThumbs() - 1);
        }

        this.rival.hasData(true);
    }

    /**
     *
     */
    public void haveData() {

    }

    /**
     *
     */
    public void onDestroy() {

    }

}
