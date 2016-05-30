package cz.martinforejt.chingchong;

import android.util.Log;
import android.widget.Toast;

/**
 * Created by Martin Forejt on 16.05.2016.
 * forejt.martin97@gmail.com
 */
public class ChinChong {

    private Player player;

    private Thread GameThread;
    private boolean isRunning = false;

    private GameActivity activity;

    private boolean gameTime = false;
    private boolean animateTime = false;
    private boolean getDataTime = false;
    private boolean endRoundTime = false;

    private GameView gameView;

    public ChinChong(Player player, GameActivity activity) {
        this.player = player;
        this.activity = activity;

        if(player instanceof ServerPlayer) {
            ((ServerPlayer) player).restartServer();
        }

        GameThread = new Thread(new GameRunnable());

        gameView = GameView.getInstance();
    }

    public void start() {
        isRunning = true;
        gameTime = true;
        //player.hisTurn(true);
        GameThread.start();
    }

    public void pause() {
        isRunning = false;
        GameThread.interrupt();
    }

    public void resume() {
        GameThread.start();
    }

    public void thumbsChange(int thumbs) {
        if (gameTime)
            this.player.setShowsThumbs(thumbs);
    }

    public void chongsChange(int chongs) {
        if (gameTime && this.player.isHisTurn()) {
            this.player.setChongs(chongs);
            GameFragment.getInstance().setActiveChongs(chongs);
        }
    }

    public void end() {
        isRunning = false;
        GameThread.interrupt();
        GameThread = null;
    }

    /**
     * GameRunnable
     * Main Game Thread
     */
    private class GameRunnable implements Runnable {

        @Override
        public void run() {
            setChongsKeyboard();
            while (isRunning) {
                try {
                    if (gameTime) {
                        gameTimePart();
                    } else if (getDataTime) {
                        gettingDataTimePart();
                    } else if (animateTime) {
                        animateResultTimePart();
                    } else if (endRoundTime) {
                        endRoundTimePart();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * Game time - player can play ( change thumbs and chongs )
         */
        private void gameTimePart() {
            gameView.setCountDownAnimationg();
            onUi(new Runnable() {
                @Override
                public void run() {
                    gameView.animateCountDown();
                }
            });
            try {
                while (gameView.isCountDownAnimating()) {
                    Thread.sleep(1);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            gameTime = false;
            getDataTime = true;
        }

        /**
         * Getting data from rival
         */
        private void gettingDataTimePart() {
            ChinChong.this.player.sendData();
            while (getDataTime) {
                if (ChinChong.this.player.getRival().hasData()) {
                    getDataTime = false;
                    animateTime = true;
                    break;
                }
            }
        }

        /**
         * Animating result and data from rival
         */
        private void animateResultTimePart() {
            try {
                onUi(new Runnable() {
                    @Override
                    public void run() {
                        //TODO animate
                        GameFragment.getInstance().animate(
                                ChinChong.this.player.getShowsThumbs() + ChinChong.this.player.getRival().getShowsThumbs()
                        );
                    }
                });
                Thread.sleep(1000);

                onUi(new Runnable() {
                    @Override
                    public void run() {
                        //TODO draw thumbs and set turn animate "Your turn"
                        GameFragment.getInstance().setPlayerThumbs(ChinChong.this.player.getThumbs());
                        GameFragment.getInstance().setRivalThumbs(ChinChong.this.player.getRival().getThumbs());
                    }
                });
                Thread.sleep(1000);
                endRoundTime = true;
                animateTime = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            ChinChong.this.player.rival.hasData(false);
        }

        /**
         * Prepare game for new round / or end game
         */
        private void endRoundTimePart() {
            endRoundTime = false;

            // End game - show result screen
            if(ChinChong.this.player.getThumbs() == 0 || ChinChong.this.player.getRival().getThumbs() == 0) {
                onUi(new Runnable() {
                    @Override
                    public void run() {
                        GameFragment.getInstance().endGame();
                    }
                });
            } else {
                gameTime = true;
                setChongsKeyboard();
            }
        }

        public void setChongsKeyboard() {
            if (ChinChong.this.player.isHisTurn()) {
                ChinChong.this.player.hisTurn(false);
                ChinChong.this.player.rival.hisTurn(true);
                onUi(new Runnable() {
                    @Override
                    public void run() {
                        GameFragment.getInstance().isHisTurn(false);
                    }
                });
            } else {
                ChinChong.this.player.hisTurn(true);
                ChinChong.this.player.rival.hisTurn(false);
                onUi(new Runnable() {
                    @Override
                    public void run() {
                        GameFragment.getInstance().isHisTurn(true);
                    }
                });
            }
        }

        /**
         * Run on ui thread
         *
         * @param runnable Runnable
         */
        public void onUi(Runnable runnable) {
            activity.runOnUiThread(runnable);
        }

    }

}
