package cz.martinforejt.chingchong;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class GameFragment extends Fragment {

    public static final String TYPE_MULTI_PLAYER = "multiplayer";
    public static final String TYPE_SINGLE_PLAYER = "singleplayer";
    public static final String TAG = "game";

    private static GameFragment instance;

    private ChinChong game;

    private boolean leftActive = true, rightActive = true;
    private int leftThumb = 0, rightThumb = 0;

    GameView gameView;

    /* Testovaci */
    Button left, right, num0, num1, num2, num3, num4;
    TextView lefttext, righttext, countdown, animate;
    /* END */

    public static GameFragment newInstance(String type) {
        instance = new GameFragment();
        /*Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);*/
        return instance;
    }

    public static GameFragment getInstance(){
        return instance;
    }

    public GameFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }*/
        /*GameActivity.setVisibleFragment(TAG);
        game = new ChinChong(new OfflinePlayer("pepa", "rival"), (GameActivity) getActivity());
        game.start();*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_game, container, false);

        gameView = (GameView) view.findViewById(R.id.gameView);

        GameActivity.setVisibleFragment(TAG);
        game = new ChinChong(new OfflinePlayer("pepa", "rival"), (GameActivity) getActivity());
        game.start();

        left = (Button) view.findViewById(R.id.left);
        right = (Button) view.findViewById(R.id.right);
        num0 = (Button) view.findViewById(R.id.click0);
        num1 = (Button) view.findViewById(R.id.click1);
        num2 = (Button) view.findViewById(R.id.click2);
        num3 = (Button) view.findViewById(R.id.click3);
        num4 = (Button) view.findViewById(R.id.click4);

        left.setOnClickListener(onClickListener);
        right.setOnClickListener(onClickListener);
        num0.setOnClickListener(onClickListener);
        num1.setOnClickListener(onClickListener);
        num2.setOnClickListener(onClickListener);
        num3.setOnClickListener(onClickListener);
        num4.setOnClickListener(onClickListener);

        lefttext = (TextView) view.findViewById(R.id.textleft);
        righttext = (TextView) view.findViewById(R.id.textright);
        countdown = (TextView) view.findViewById(R.id.countdown);
        animate = (TextView) view.findViewById(R.id.animate);

        return view;
    }

    public GameView getGameView() {
        return gameView;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.left:
                    if (leftActive) {
                        // 0 - 1 => 1 , 1 - 1 => 0
                        leftThumb = Math.abs(leftThumb - 1);
                        game.thumbsChange(leftThumb + rightThumb);
                        left.setText("LEFT " + String.valueOf(leftThumb));
                    }
                    break;
                case R.id.right:
                    if(rightActive) {
                        rightThumb = Math.abs(rightThumb - 1);
                        game.thumbsChange(rightThumb + leftThumb);
                        right.setText("RIGHT " + String.valueOf(rightThumb));
                    }
                    break;
                case R.id.click0:
                    game.chongsChange(0);
                    break;
                case R.id.click1:
                    game.chongsChange(1);
                    break;
                case R.id.click2:
                    game.chongsChange(2);
                    break;
                case R.id.click3:
                    game.chongsChange(3);
                    break;
                case R.id.click4:
                    game.chongsChange(4);
                    break;
            }
        }
    };

    public void setPlayerThumbs(int thumbs) {
        lefttext.setText("Máš " + String.valueOf(thumbs) + " palce");
    }

    public void setRivalThumbs(int thumbs) {
        righttext.setText("Má " + String.valueOf(thumbs) + " palce");
    }

    public void animate(int chongs) {
        this.animate.setText("CHING - CHONG " + String.valueOf(chongs));
    }

    public void setActiveChongs(int chongs) {
        num0.setText("0");
        num1.setText("1");
        num2.setText("2");
        num3.setText("3");
        num4.setText("4");
        switch (chongs){
            case 0:
                num0.setText("-0-");
                break;
            case 1:
                num1.setText("-1-");
                break;
            case 2:
                num2.setText("-2-");
                break;
            case 3:
                num3.setText("-3-");
                break;
            case 4:
                num4.setText("-4-");
                break;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onPause() {
        game.end();
        super.onDestroy();
    }

}
