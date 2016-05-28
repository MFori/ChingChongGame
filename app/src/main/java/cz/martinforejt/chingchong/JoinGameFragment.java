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
 * A simple {@link Fragment} subclass.
 * Use the {@link JoinGameFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class JoinGameFragment extends Fragment {

    public static final String TAG = "join";

    private ClientPlayer player;

    private EditText ipAddress;
    private Button joinBtn;

    public static JoinGameFragment newInstance() {
        JoinGameFragment fragment = new JoinGameFragment();
        /*Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);*/
        return fragment;
    }

    public JoinGameFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }*/
        GameActivity.setVisibleFragment(TAG);

        /*ClientPlayer clientPlayer = new ClientPlayer("pepa", "sfd", "192.168.0.100");
        clientPlayer.connect();
        clientPlayer.connect();*/
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
                player = new ClientPlayer("pepa", "rival", ipAddress.getText().toString());
                player.connect();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (player.isAsyncRunning()){
                            continue;
                        }
                        if(player.isConnect()){
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
