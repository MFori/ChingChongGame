package cz.martinforejt.chingchong;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateGameFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateGameFragment extends Fragment {

    public static final String TAG = "create";

    private ServerPlayer player;
    private String playerName = "";

    private TextView ipAddress;

    public static CreateGameFragment newInstance() {
        CreateGameFragment fragment = new CreateGameFragment();
        /*Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);*/
        return fragment;
    }

    public CreateGameFragment() {
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

        player = new ServerPlayer("pepa", "rival");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_game, container, false);

        ipAddress = (TextView) view.findViewById(R.id.ip_address);
        ipAddress.setText(player.getIpAddress());

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        player.onDestroy();
    }

}
