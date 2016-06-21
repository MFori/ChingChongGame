package cz.martinforejt.chingchong;

import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Martin Forejt on 17.05.2016.
 * forejt.martin97@gmail.com
 * class JoinGameFragment
 * For join the game - create client player
 */
public class JoinGameFragment extends Fragment {

    public static final String TAG = "join";

    private ClientPlayer player = null;

    private String ipAddress = "";

    boolean isConnecting = false;
    boolean canConnect = true;

    Thread connThread = null;

    private TextView tvIp;
    private Button keyOk;
    private ImageButton keyBack, keyCancel, keyDot;
    // number buttons
    private Button[] btns = new Button[10];


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
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_join_game, container, false);

        tvIp = (TextView) view.findViewById(R.id.ip_address_text);
        keyDot = (ImageButton) view.findViewById(R.id.ip_key_dot);
        keyBack = (ImageButton) view.findViewById(R.id.ip_key_back);
        keyCancel = (ImageButton) view.findViewById(R.id.ip_key_cancel);
        keyOk = (Button) view.findViewById(R.id.ip_key_ok);
        initNumKeyboard(view);

        setFontConnect();
        setFontNumbers();

        // Dot key - add dot
        keyDot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberClick(".");
            }
        });

        // Back key - delete last character
        keyBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ipAddress.length() > 0) {
                    ipAddress = ipAddress.substring(0, ipAddress.length() - 1);
                    tvIp.setText(ipAddress);
                }
            }
        });

        // Cancel key - empty ip
        keyCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ipAddress = "";
                tvIp.setText(ipAddress);
            }
        });

        // Ok key - start game (send request to server player)
        keyOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if (isConnecting) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (isConnecting && canConnect) {
                                Log.d("wait", "w");
                                continue;
                            }
                            if(canConnect) {
                                connThread.interrupt();
                                connThread = null;
                                player.onDestroy();
                                player = null;
                                start();
                            }
                        }
                    }).start();
                } else
                    start();*/
                //if(!isConnecting) start();
                start();
            }
        });

        return view;
    }

    /**
     * Start game
     * Send request to server
     */
    private void start() {
        if (player != null) player.stopConnect();
        player = null;
        player = new ClientPlayer(Config.getName(), "", ipAddress);
        player.connect();
        isConnecting = true;
        connThread = new Thread(new Runnable() {
            @Override
            public void run() {
                /*if(!player.isAsyncRunning()){
                    start();
                    return;
                }*/
                while (player.isAsyncRunning()) {
                    Log.d("IS", "ASYNC");
                    try {
                        Thread.sleep(200);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (player.isConnect()) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            GameActivity activity = (GameActivity) getActivity();
                            activity.changeFragment(GameFragment.newInstance(player), GameFragment.TAG, true);
                        }
                    });
                    canConnect = false;
                    return;
                } else {
                    Log.d("CONNECTED", "NO");
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "Can not connect...", Toast.LENGTH_LONG).show();
                        }
                    });
                }
                isConnecting = false;
            }
        });
        connThread.start();
    }

    /**
     * Number, dot button click
     *
     * @param number String
     */
    private void numberClick(String number) {
        if (ipAddress.length() < 15) {
            ipAddress += number;
            tvIp.setText(ipAddress);
        }
    }

    /**
     * Initialize layout elements
     *
     * @param view View
     */
    private void initNumKeyboard(View view) {
        btns[0] = (Button) view.findViewById(R.id.ip_key_0);
        btns[1] = (Button) view.findViewById(R.id.ip_key_1);
        btns[2] = (Button) view.findViewById(R.id.ip_key_2);
        btns[3] = (Button) view.findViewById(R.id.ip_key_3);
        btns[4] = (Button) view.findViewById(R.id.ip_key_4);
        btns[5] = (Button) view.findViewById(R.id.ip_key_5);
        btns[6] = (Button) view.findViewById(R.id.ip_key_6);
        btns[7] = (Button) view.findViewById(R.id.ip_key_7);
        btns[8] = (Button) view.findViewById(R.id.ip_key_8);
        btns[9] = (Button) view.findViewById(R.id.ip_key_9);

        for (int i = 0; i < 10; i++) {
            btns[i].setOnClickListener(numsListener);
        }
    }

    /**
     * On number button (key) click
     */
    View.OnClickListener numsListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            numberClick(((Button) v).getText().toString());
        }
    };

    /**
     * Set chlorinr (assets/fonts/chlorinr.ttf) font to settings buttons
     */
    private void setFontConnect() {
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/chlorinr.ttf");
        keyOk.setTypeface(typeface);
    }

    /**
     * Set chlorinr (assets/fonts/chlorinr.ttf) font to settings buttons
     */
    private void setFontNumbers() {
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/comic.ttf");
        for (int i = 0; i < 10; i++) {
            btns[i].setTypeface(typeface);
        }
    }

}
