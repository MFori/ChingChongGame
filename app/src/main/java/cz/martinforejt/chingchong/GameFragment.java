package cz.martinforejt.chingchong;

import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Martin Forejt on 16.05.2016.
 * forejt.martin97@gmail.com
 * class GameFragment
 */
public class GameFragment extends Fragment {

    public static final String TAG = "game";

    private static GameFragment instance;

    private ChinChong game;
    private Player player;

    private boolean leftActive = true,
            rightActive = true;
    private int leftThumb = 0, rightThumb = 0;
    private PauseDialog pauseDialog = null;

    protected GameView gameView;

    protected Button num0, num1, num2, num3, num4;
    protected ImageButton left, right;
    protected TextView animate;
    protected TextView playerName, rivalName;
    protected ImageView playerThumb1, playerThumb2, rivalThumb1, rivalThumb2;

    /**
     * @param player Player
     * @return GameFragment
     */
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

        final View view = inflater.inflate(R.layout.fragment_game, container, false);

        gameView = (GameView) view.findViewById(R.id.gameView);

        GameActivity.setVisibleFragment(TAG);

        game = new ChinChong(player, (GameActivity) getActivity());
        game.start();

        initElements(view);

        playerName.setText(player.getName());
        rivalName.setText(player.getRival().getName());

        pauseDialog = new PauseDialog(getActivity());
        pauseDialog.setEndListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseDialog.close();
                endGame();
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    /**
     * Listener for thumbs and chongs
     */
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.left:
                    if (leftActive) {
                        // 0 - 1 => 1 , 1 - 1 => 0
                        leftThumb = Math.abs(leftThumb - 1);
                        //game.thumbsChange(leftThumb + rightThumb);
                        if (game.leftThumbChange(leftThumb == 1, leftThumb + rightThumb)) {
                            left.setImageResource(leftThumb == 1 ? R.drawable.thumb_left : R.drawable.left_fist);
                        } else {
                            leftThumb = Math.abs(leftThumb - 1);
                        }
                    }
                    break;
                case R.id.right:
                    if (rightActive) {
                        rightThumb = Math.abs(rightThumb - 1);
                        //game.thumbsChange(rightThumb + leftThumb);
                        //right.setImageResource(rightThumb == 1 ? R.drawable.thumb_right : R.drawable.right_fist);
                        if (game.rightThumbChange(rightThumb == 1, leftThumb + rightThumb)) {
                            right.setImageResource(rightThumb == 1 ? R.drawable.thumb_right : R.drawable.right_fist);
                        } else {
                            leftThumb = Math.abs(leftThumb - 1);
                        }
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

    /**
     * Set players available thumbs
     *
     * @param thumbs int
     */
    public void setPlayerThumbs(int thumbs) {
        if (game != null)
            switch (thumbs) {
                case 2:
                    playerThumb1.setVisibility(View.VISIBLE);
                    playerThumb2.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    if (playerThumb1.getVisibility() == View.VISIBLE) {
                        scaleThumb(playerThumb1);
                    }
                    playerThumb2.setVisibility(View.VISIBLE);
                    break;
                case 0:
                    playerThumb1.setVisibility(View.GONE);
                    if (playerThumb2.getVisibility() == View.VISIBLE) {
                        scaleThumb(playerThumb2);
                    }
                    break;
            }
    }

    /**
     * Set rival available thumbs
     *
     * @param thumbs int
     */
    public void setRivalThumbs(int thumbs) {
        if (game != null)
            switch (thumbs) {
                case 2:
                    rivalThumb1.setVisibility(View.VISIBLE);
                    rivalThumb2.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    if (rivalThumb1.getVisibility() == View.VISIBLE) {
                        scaleThumb(rivalThumb1);
                    }
                    rivalThumb2.setVisibility(View.VISIBLE);
                    break;
                case 0:
                    rivalThumb1.setVisibility(View.GONE);
                    if (rivalThumb2.getVisibility() == View.VISIBLE) {
                        scaleThumb(rivalThumb2);
                    }
                    break;
            }
    }

    /**
     * Run scale animation to view (thumb)
     * First animation scale up (1.1x) than scale to 0 in second step
     *
     * @param thumb View (ImageView)
     */
    protected void scaleThumb(final View thumb) {
        final Animation scale2 = new ScaleAnimation(1.2f, 0, 1.2f, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scale2.setDuration(1000);
        scale2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                thumb.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        Animation scale1 = new ScaleAnimation(1, 1.2f, 1, 1.2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scale1.setDuration(500);
        scale1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                thumb.startAnimation(scale2);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        thumb.startAnimation(scale1);
    }

    /**
     * Show result fragment
     *
     * @param win bool - is player winner ( has 0 thumbs )
     */
    public void endGame(boolean win) {
        game.end();
        GameActivity activity = (GameActivity) getActivity();
        activity.changeFragment(ResultFragment.newInstance(win, player), ResultFragment.TAG, true);
        game = null;
    }

    /**
     * End game without result and rematch variant
     */
    public void endGame() {
        game.end();
        game = null;
        ((GameActivity) getActivity()).changeFragment(new MenuFragment(), MenuFragment.TAG, true);
    }

    /**
     * Animate result
     *
     * @param chongs int
     */
    public void animate(int chongs) {
        if (game != null) {
            this.animate.setText("CHING CHONG " + String.valueOf(chongs));
            Animation finalAnim = new ScaleAnimation(0.8f, 1.3f, 0.8f, 1.3f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            finalAnim.setDuration(2500);
            Animation alpha = new AlphaAnimation(1, 0);
            alpha.setDuration(2500);
            AnimationSet set = new AnimationSet(true);
            set.addAnimation(finalAnim);
            set.addAnimation(alpha);
            set.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    GameFragment.this.animate.setVisibility(View.VISIBLE);
                }
                @Override
                public void onAnimationEnd(Animation animation) {
                    GameFragment.this.animate.setVisibility(View.INVISIBLE);
                }
                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            this.animate.startAnimation(set);
        }
    }

    /**
     * Set comic (assets/fonts/comic.ttf) font to animate text
     */
    private void setFontAnimate() {
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/comic.ttf");
        animate.setTypeface(typeface);
    }

    /**
     * Set chlorinr (assets/fonts/chlorinr.ttf) font to chongs buttons
     */
    private void setFontChongs() {
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/comic.ttf");
        num0.setTypeface(typeface);
        num1.setTypeface(typeface);
        num2.setTypeface(typeface);
        num3.setTypeface(typeface);
        num4.setTypeface(typeface);
    }

    /**
     * Set active chong background2
     *
     * @param chongs int
     */
    public void setActiveChongs(int chongs) {
        if (game != null) {
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
    }

    /**
     * Slide right thumb out
     * Make right thumb invisible
     */
    public void hideRightThumb() {
        if (game != null && right.getVisibility() == View.VISIBLE) {
            animateHideThumb(right, 300);
        }
    }

    /**
     * Slide right thumb out
     * Make right thumb invisible
     */
    public void hideLeftThumb() {
        if (game != null && left.getVisibility() == View.VISIBLE) {
            animateHideThumb(left, -300);
        }
    }

    /**
     * Slide out thumb
     * @param thumb View (ImageView)
     */
    private void animateHideThumb(final View thumb, int x) {
        TranslateAnimation animation = new TranslateAnimation(0, x, 0, 300);
        animation.setDuration(1200);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                thumb.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        thumb.startAnimation(animation);
    }

    /**
     * Enable/Disable chongs 'keyboard'
     *
     * @param hisTurn bool
     */
    public void isHisTurn(boolean hisTurn) {
        if (game != null) {
            int background;
            int color;
            if (hisTurn) {
                background = R.drawable.chong_button_active;
                color = R.color.chongActive;
            } else {
                background = R.drawable.chong_button_unactive;
                color = R.color.chongUnActive;
            }
            setChongBackgroundAndTextColor(background, color, num0);
            setChongBackgroundAndTextColor(background, color, num1);
            setChongBackgroundAndTextColor(background, color, num2);
            setChongBackgroundAndTextColor(background, color, num3);
            setChongBackgroundAndTextColor(background, color, num4);
        }
    }

    private void setChongBackgroundAndTextColor(int background, int color, Button btn) {
        btn.setBackgroundResource(background);
        btn.setTextColor(color);
    }

    /**
     * Init views from layout
     *
     * @param view View - fragment
     */
    private void initElements(View view) {
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

        isHisTurn(player instanceof ClientPlayer || player instanceof OfflinePlayer);

        playerName = (TextView) view.findViewById(R.id.player_name);
        rivalName = (TextView) view.findViewById(R.id.rival_name);
        animate = (TextView) view.findViewById(R.id.animate);
        setFontAnimate();
        setFontChongs();

        playerThumb1 = (ImageView) view.findViewById(R.id.player_thumb_1);
        playerThumb2 = (ImageView) view.findViewById(R.id.player_thumb_2);
        rivalThumb1 = (ImageView) view.findViewById(R.id.rival_thumb_1);
        rivalThumb2 = (ImageView) view.findViewById(R.id.rival_thumb_2);
    }

    /**
     * Show pause dialog
     */
    public void showPauseRivalDialog() {
        if (game != null) pauseDialog.open();
    }

    /**
     * Hide pause dialog if is visible
     */
    public void hidePauseRivalDialog() {
        if (game != null) pauseDialog.hide();
    }

    @Override
    public void onPause() {
        if (game != null) game.pause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (game != null) game.end();
        game = null;
        super.onDestroy();
    }

    @Override
    public void onResume() {
        if (game != null) game.resume();
        super.onResume();
    }

}
