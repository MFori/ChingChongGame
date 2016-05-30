package cz.martinforejt.chingchong;

import android.app.Activity;
import android.media.Image;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class GameFragment extends Fragment {

    public static final String TYPE_MULTI_PLAYER = "multiplayer";
    public static final String TYPE_SINGLE_PLAYER = "singleplayer";
    public static final String TAG = "game";

    private static GameFragment instance;

    private ChinChong game;
    private Player player;

    private boolean leftActive = true, rightActive = true;
    private int leftThumb = 0, rightThumb = 0;

    GameView gameView;

    /* Testovaci */
    Button num0, num1, num2, num3, num4;
    ImageButton left, right;
    TextView animate;
    TextView playerName, rivalName;
    ImageView playerThumb1, playerThumb2, rivalThumb1, rivalThumb2;
    /* END */

    public static GameFragment newInstance(Player player) {
        instance = new GameFragment();
        Bundle args = new Bundle();
        args.putSerializable("player", player);
        instance.setArguments(args);
        return instance;
    }

    public static GameFragment getInstance() {
        return instance;
    }

    public GameFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            player = (Player) getArguments().getSerializable("player");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_game, container, false);

        gameView = (GameView) view.findViewById(R.id.gameView);

        GameActivity.setVisibleFragment(TAG);
        game = new ChinChong(player, (GameActivity) getActivity());
        game.start();

        left = (ImageButton) view.findViewById(R.id.left);
        right = (ImageButton) view.findViewById(R.id.right);
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

        playerName = (TextView) view.findViewById(R.id.player_name);
        rivalName = (TextView) view.findViewById(R.id.rival_name);
        animate = (TextView) view.findViewById(R.id.animate);

        playerThumb1 = (ImageView) view.findViewById(R.id.player_thumb_1);
        playerThumb2 = (ImageView) view.findViewById(R.id.player_thumb_2);
        rivalThumb1 = (ImageView) view.findViewById(R.id.rival_thumb_1);
        rivalThumb2 = (ImageView) view.findViewById(R.id.rival_thumb_2);

        playerName.setText(player.getName());
        rivalName.setText(player.getRival().getName());

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
                        left.setImageResource(leftThumb == 1 ? R.drawable.thumb100 : R.drawable.fist);
                    }
                    break;
                case R.id.right:
                    if (rightActive) {
                        rightThumb = Math.abs(rightThumb - 1);
                        game.thumbsChange(rightThumb + leftThumb);
                        right.setImageResource(rightThumb == 1 ? R.drawable.thumb100 : R.drawable.fist);
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
        switch (thumbs) {
            case 2:
                playerThumb1.setVisibility(View.VISIBLE);
                playerThumb2.setVisibility(View.VISIBLE);
                break;
            case 1:
                playerThumb1.setVisibility(View.GONE);
                playerThumb2.setVisibility(View.VISIBLE);
                break;
            case 0:
                playerThumb1.setVisibility(View.GONE);
                playerThumb2.setVisibility(View.GONE);
                break;
        }
    }

    public void setRivalThumbs(int thumbs) {
        switch (thumbs) {
            case 2:
                rivalThumb1.setVisibility(View.VISIBLE);
                rivalThumb2.setVisibility(View.VISIBLE);
                break;
            case 1:
                rivalThumb1.setVisibility(View.GONE);
                rivalThumb2.setVisibility(View.VISIBLE);
                break;
            case 0:
                rivalThumb1.setVisibility(View.GONE);
                rivalThumb2.setVisibility(View.GONE);
                break;
        }
    }

    public void endGame() {
        game.end();
        GameActivity activity = (GameActivity) getActivity();
        activity.changeFragment(ResultFragment.newInstance(), ResultFragment.TAG, true);
        game = null;
    }

    public void animate(int chongs) {
        this.animate.setText("CHING - CHONG " + String.valueOf(chongs));
    }

    public void setActiveChongs(int chongs) {
        int background = R.drawable.chong_button_active;
        int choosen = R.drawable.chong_button_active_choosen;
        num0.setBackgroundResource(background);
        num1.setBackgroundResource(background);
        num2.setBackgroundResource(background);
        num3.setBackgroundResource(background);
        num4.setBackgroundResource(background);
        switch (chongs) {
            case 0:
                num0.setBackgroundResource(choosen);
                break;
            case 1:
                num1.setBackgroundResource(choosen);
                break;
            case 2:
                num2.setBackgroundResource(choosen);
                break;
            case 3:
                num3.setBackgroundResource(choosen);
                break;
            case 4:
                num4.setBackgroundResource(choosen);
                break;
        }
    }

    public void isHisTurn(boolean hisTurn) {
        int background = 0;
        if (hisTurn) {
            background = R.drawable.chong_button_active;
        } else {
            background = R.drawable.chong_button_unactive;
        }
        num0.setBackgroundResource(background);
        num1.setBackgroundResource(background);
        num2.setBackgroundResource(background);
        num3.setBackgroundResource(background);
        num4.setBackgroundResource(background);
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
        if (game != null) game.pause();
        super.onDestroy();
    }

}
