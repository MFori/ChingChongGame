package cz.martinforejt.chingchong;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Martin Forejt on 16.05.2016.
 * forejt.martin97@gmail.com
 * class CreateGameFragment
 * Uses for creating game - creating server player
 */
public class CreateGameFragment extends Fragment {

    public static final String TAG = "create";

    private ServerPlayer player;

    private static CreateGameFragment instance;

    private TextView ipAddress, waiting;
    private Thread waitingThread;
    private boolean isWaiting = false;

    public static CreateGameFragment newInstance() {
        instance = new CreateGameFragment();
        return instance;
    }

    /**
     * @return CreateGameFragment
     */
    public static CreateGameFragment getInstance() {
        return instance;
    }

    public CreateGameFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GameActivity.setVisibleFragment(TAG);

        player = new ServerPlayer(Config.getName(), "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_game, container, false);

        ipAddress = (TextView) view.findViewById(R.id.ip_address);
        ipAddress.setText(player.getIpAddress());

        waiting = (TextView) view.findViewById(R.id.waitingText);
        startWaiting();

        return view;
    }

    private void startWaiting() {
        waitingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                isWaiting = true;
                while (isWaiting) {
                    try {
                        Thread.sleep(600);
                        setWaitingText("...");
                        Thread.sleep(600);
                        setWaitingText("..");
                        Thread.sleep(600);
                        setWaitingText(".");
                        Thread.sleep(600);
                        setWaitingText("");
                        Thread.sleep(600);
                        setWaitingText(".");
                        Thread.sleep(600);
                        setWaitingText("..");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        waitingThread.start();
    }

    private void setWaitingText(final String text) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                waiting.setText("Waiting for oponent" + text);
            }
        });
    }

    /**
     * Starts the game ( add gameFragment )
     */
    public void startGame() {
        isWaiting = false;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                GameActivity activity = (GameActivity) getActivity();
                activity.changeFragment(GameFragment.newInstance(player), GameFragment.TAG, true);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        player.onDestroy();
        waitingThread.interrupt();
        waitingThread = null;
    }

}
