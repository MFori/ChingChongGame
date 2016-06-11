package cz.martinforejt.chingchong;


import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Martin Forejt on 02.06.2016.
 * forejt.martin97@gmail.com
 * class ResultFragment
 * Shows game result
 */
public class ResultFragment extends Fragment {

    public static final String TAG = "result";
    private static final String ARG_WINNER = "winner";
    private static final String ARG_PLAYER = "player";

    private boolean winner;
    private Player player;

    private static ResultFragment instance;

    private Button rematch;
    private TextView win, myScore, rivalScore, score, waiting;
    private static final String TEXT_WIN = "YOU ARE WINNER";
    private static final String TEXT_LOSS = "YOU ARE LOSER";

    private Thread rematchT = null;
    protected boolean waitForRematch = false;
    protected boolean isRematch = false;

    /**
     * @param win    bool
     * @param player Player
     * @return ResultFragment
     */
    public static ResultFragment newInstance(boolean win, Player player) {
        ResultFragment fragment = new ResultFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_WINNER, win);
        args.putSerializable(ARG_PLAYER, player);
        fragment.setArguments(args);
        instance = fragment;
        return fragment;
    }

    /**
     * @return ResultFragment
     */
    public static ResultFragment getInstance() {
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            winner = getArguments().getBoolean(ARG_WINNER);
            player = (Player) getArguments().getSerializable(ARG_PLAYER);
        }
        GameActivity.setVisibleFragment(TAG);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_result, container, false);

        initElements(view);

        if (winner) {
            Config.setMyScore(Config.getMyScore() + 1);
            win.setText(TEXT_WIN);
        } else {
            Config.setScoreRival(Config.getRivalScore() + 1);
            win.setText(TEXT_LOSS);
        }

        myScore.setText(player.getName());
        rivalScore.setText(player.getRival().getName());
        score.setText(String.valueOf(Config.getMyScore()) + " : " + String.valueOf(Config.getRivalScore()));

        return view;
    }

    /**
     * Initialize layout elements
     *
     * @param v View
     */
    private void initElements(View v) {
        rematch = (Button) v.findViewById(R.id.btn_rematch);
        rematch.setOnClickListener(playRematch);
        win = (TextView) v.findViewById(R.id.win);
        myScore = (TextView) v.findViewById(R.id.myScore);
        rivalScore = (TextView) v.findViewById(R.id.rivalScore);
        score = (TextView) v.findViewById(R.id.result_score);
        waiting = (TextView) v.findViewById(R.id.rematch_text_waiting);
    }

    /**
     * On rematch button click listener
     */
    View.OnClickListener playRematch = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            restorePlayer();
            // Client
            if (player instanceof ClientPlayer) {
                rematchT = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        waitForRematch = true;
                        boolean isConnect = false;
                        while (!isConnect) {

                            Log.d("Rematch: ", "Call client");
                            ((ClientPlayer) player).rematch();

                            try {
                                Thread.sleep(200);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            if (!waitForRematch) return;
                            while (true) {
                                if (!((ClientPlayer) player).isAsyncRunning()) break;
                                if (!waitForRematch) return;
                            }
                            isConnect = ((ClientPlayer) player).isConnect();
                            if (isConnect) {
                                rematch();
                                return;
                            }

                            //isConnect = ((ClientPlayer) player).isConnect();
                        }
                    }
                });
                rematchT.start();
            }
            // Server
            else if (player instanceof ServerPlayer) {
                ((ServerPlayer) player).wantRematch(true);
            }
            // Offline
            else if (player instanceof OfflinePlayer) {
                rematch();
            }

            v.setVisibility(View.GONE);
            waiting.setVisibility(View.VISIBLE);
        }
    };

    /**
     * Start game with same rival
     */
    public void rematch() {
        isRematch = true;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((GameActivity) getActivity()).changeFragment(GameFragment.newInstance(player), GameFragment.TAG, true);
            }
        });
    }

    /**
     * Restore player thumbs information
     */
    private void restorePlayer() {
        if (player instanceof ServerPlayer) ((ServerPlayer) player).restartServer();

        player.setThumbs(Player.DEFAULT_THUMBS);
        player.setShowsThumbs(Player.DEFAULT_SHOWS_THUMBS);
        player.setChongs(Player.DEFAULT_CHONGS);

        player.rival.setThumbs(Player.DEFAULT_THUMBS);
        player.rival.setShowsThumbs(Player.DEFAULT_SHOWS_THUMBS);
        player.rival.setChongs(Player.DEFAULT_CHONGS);

        player.hisTurn(player instanceof ClientPlayer || player instanceof OfflinePlayer);
        player.rival.hisTurn(player instanceof ServerPlayer);
    }

    public ResultFragment() {
        // Required empty public constructor
    }

    @Override
    public void onDestroy() {
        waitForRematch = false;
        if (rematchT != null) {
            rematchT.interrupt();
            rematchT = null;
        }
        player.onDestroy();
        if (!isRematch) Config.resetScore();
        super.onDestroy();
    }

}
