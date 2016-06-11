package cz.martinforejt.chingchong;


import android.util.Log;

/**
 * Created by Martin Forejt on 16.05.2016.
 * forejt.martin97@gmail.com
 * class ChingChong
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

    /**
     * Game construct
     *
     * @param player   Player ( offline/client/server )
     * @param activity GameActivity
     */
    public ChinChong(Player player, GameActivity activity) {
        this.player = player;
        this.activity = activity;

        if (player instanceof ServerPlayer) {
            ((ServerPlayer) player).restartServer();
        }

        GameThread = new Thread(new GameRunnable());

        // get surfaceView
        gameView = GameView.getInstance();
    }

    /**
     * Start the game, run the main game Thread
     */
    public void start() {
        isRunning = true;
        gameTime = true;

        // Display/Hide chongs choose 'keyboard'
        //GameFragment.getInstance().isHisTurn(player.isHisTurn());
        player.rival.hisTurn(!player.isHisTurn());

        GameThread.start();
    }

    /**
     * Pause the game, end main Thread
     */
    public void pause() {
        isRunning = false;
        player.onPause();
        gameView.stopCountDown();
    }

    /**
     * Resume paused game if is paused
     * Restore game logic
     */
    public void resume() {
        if (!isRunning) {
            player.onResume();
            GameThread.interrupt();
            GameThread = null;

            gameTime = true;
            animateTime = false;
            getDataTime = false;
            endRoundTime = false;

            isRunning = true;
            GameThread = new Thread(new GameRunnable());
            GameThread.start();
        }
    }

    /**
     * Trigger for thumbs change in game fragment layout
     *
     * @param thumbs int
     */
    public void thumbsChange(int thumbs) {
        if (gameTime)
            this.player.setShowsThumbs(thumbs);
    }

    /**
     * Trigger for chongs change in game fragment layout
     * Only if is player turn
     *
     * @param chongs int
     */
    public void chongsChange(int chongs) {
        if (gameTime && this.player.isHisTurn()) {
            this.player.setChongs(chongs);
            GameFragment.getInstance().setActiveChongs(chongs);
        }
    }

    /**
     * End the game and stop main game Thread
     */
    public void end() {
        isRunning = false;
        GameThread.interrupt();
        GameThread = null;
        player.onDestroy();
    }

    /**
     * Check if game is in pause mode
     *
     * @return bool
     */
    public boolean isPaused() {
        return !isRunning;
    }

    /**
     * GameRunnable
     * For main Game Thread
     */
    private class GameRunnable implements Runnable {

        /**
         * Main game loop
         */
        @Override
        public void run() {
            while (isRunning) {
                try {
                    if (gameTime) {
                        // player can play
                        gameTimePart();
                    } else if (getDataTime) {
                        // players changing data
                        gettingDataTimePart();
                    } else if (animateTime) {
                        // animation result, when has all data
                        animateResultTimePart();
                    } else if (endRoundTime) {
                        // set up for new round
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
            gameView.setCountDownAnimation();
            onUi(new Runnable() {
                @Override
                public void run() {
                    gameView.animateCountDown();
                }
            });

            while (gameView.isCountDownAnimating()) {
                try {
                    Thread.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // TODO ZKUSIT misto sleep countinue;
            }
            gameTime = false;
            getDataTime = true;
        }

        /**
         * Getting data from rival
         */
        private void gettingDataTimePart() {
            // send data to rival
            player.rival.hasData(false);
            ChinChong.this.player.sendData();
            while (getDataTime) {

                // player has data from rival
                if (ChinChong.this.player.getRival().hasData()) {
                    getDataTime = false;
                    animateTime = true;
                    noPaused();
                    break;
                }

                if(player instanceof ClientPlayer) {
                    if(((ClientPlayer) player).isError()) {
                        Log.d("Error: ", "Client");
                        paused();
                        try{
                            Thread.sleep(200);
                            ChinChong.this.player.sendData();
                            continue;
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }

                if(player instanceof ServerPlayer) {
                    if(((ServerPlayer) player).isClientPaused()) paused();
                    try{
                        Thread.sleep(100);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
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
        }

        /**
         * Prepare game for new round / or end game
         */
        private void endRoundTimePart() {
            endRoundTime = false;
            ChinChong.this.player.rival.hasData(false);

            // End game - show result screen
            if (ChinChong.this.player.getThumbs() == 0 || ChinChong.this.player.getRival().getThumbs() == 0) {
                Vibrator.getInstance(activity).vibrate(100);
                player.onDestroy();
                onUi(new Runnable() {
                    @Override
                    public void run() {
                        GameFragment.getInstance().endGame(ChinChong.this.player.getThumbs() == 0);
                    }
                });
            } else {
                gameTime = true;
                player.setChongs(-1);
                player.rival.setChongs(-1);
                setChongsKeyboard();
            }
        }

        /**
         * Enable/Disable chongs choosing 'keyboard'
         */
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
            try{
                activity.runOnUiThread(runnable);
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        public void paused() {
            onUi(new Runnable() {
                @Override
                public void run() {
                    GameFragment.getInstance().showPauseRivalDialog();
                }
            });
        }

        public void noPaused() {
            onUi(new Runnable() {
                @Override
                public void run() {
                    GameFragment.getInstance().hidePauseRivalDialog();
                }
            });
        }

    }

}
