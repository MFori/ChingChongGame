package cz.martinforejt.chingchong;


import android.os.Bundle;
import android.app.Fragment;
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

    private Button rematch;

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
        return fragment;
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

        // TEST RESULT
        TextView tv = (TextView) view.findViewById(R.id.win);
        String result = "";
        if (winner)
            result += "YOU ARE WINNER, " + player.getName() + " AND " + player.getRival().getName() + " IS LOOSER";
        else
            result += "YOUR ARE LOOSER, " + player.getName() + "AND" + player.getRival().getName() + " IS WINNER";
        tv.setText(result);
        // END

        initElements(view);

        return view;
    }

    /**
     * @param v View
     */
    private void initElements(View v) {
        rematch = (Button) v.findViewById(R.id.btn_rematch);
        rematch.setOnClickListener(playRematch);
    }

    /**
     *
     */
    View.OnClickListener playRematch = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            restorePlayer();
            ((GameActivity) getActivity()).changeFragment(GameFragment.newInstance(player), GameFragment.TAG, true);
        }
    };

    /**
     * Restore player thumbs information
     */
    private void restorePlayer() {
        player.setThumbs(2);
        player.setShowsThumbs(0);
        player.setChongs(0);

        player.rival.setThumbs(2);
        player.rival.setShowsThumbs(0);
        player.rival.setChongs(0);

        player.hisTurn(player instanceof ServerPlayer || player instanceof OfflinePlayer);
    }

    public ResultFragment() {
        // Required empty public constructor
    }

}
