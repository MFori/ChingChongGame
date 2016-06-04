package cz.martinforejt.chingchong;

import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Martin Forejt on 17.05.2016.
 * forejt.martin97@gmail.com
 * class JoinGameFragment
 * For join the game - create client player
 */
public class JoinGameFragment extends Fragment {

    public static final String TAG = "join";

    private ClientPlayer player;

    private EditText ipAddress;
    private Button joinBtn;

    public static JoinGameFragment newInstance() {
        return new JoinGameFragment();
    }

    public JoinGameFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GameActivity.setVisibleFragment(TAG);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_join_game, container, false);

        ipAddress = (EditText) view.findViewById(R.id.ip_address_edit);
        joinBtn = (Button) view.findViewById(R.id.joinBtn);
        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player = new ClientPlayer(Config.getName(), "", ipAddress.getText().toString());
                player.connect();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (player.isAsyncRunning()) {
                            continue;
                        }
                        if (player.isConnect()) {
                            Log.d("CONNECTED", "YES");
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    GameActivity activity = (GameActivity) getActivity();
                                    activity.changeFragment(GameFragment.newInstance(player), GameFragment.TAG, true);
                                }
                            });
                        } else {
                            Log.d("CONNECTED", "NO");
                            // TODO: show error message
                        }
                    }
                }).start();
            }
        });

        return view;
    }

}
