package cz.martinforejt.chingchong;


import android.app.AlertDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Martin Forejt on 15.05.2016.
 * forejt.martin97@gmail.com
 * Class SettingsFragment
 */
public class SettingsFragment extends Fragment {

    public static final String TAG = "settings";

    private Button changeName, sound, vibrate;
    private TextView nameText;

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    public SettingsFragment() {
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
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        initElements(view);

        return  view;
    }

    /**
     * @param v View
     */
    private void initElements(View v) {
        changeName = (Button) v.findViewById(R.id.settings_change_name);
        changeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeNameDialog();
            }
        });

        sound = (Button) v.findViewById(R.id.settings_sound);
        sound.setOnClickListener(soundOnClick);
        sound.setText(Config.isIsSoundOn() ? "ON" : "OFF");

        vibrate = (Button) v.findViewById(R.id.settings_vibrate);
        vibrate.setOnClickListener(vibrateOnClick);
        vibrate.setText(Config.isVibratorOn() ? "ON" : "OFF");

        nameText = (TextView) v.findViewById(R.id.settings_name_text);
        setNameTextView();

        setFont();
    }

    /**
     *
     */
    private void setFont() {
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/chlorinr.ttf");
        sound.setTypeface(typeface);
        vibrate.setTypeface(typeface);
        changeName.setTypeface(typeface);
    }

    private void setNameTextView() {
        nameText.setText("Name: " + Config.getName());
    }

    /**
     *
     */
    public void changeNameDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View view = layoutInflater.inflate(R.layout.dialog_change_name, null);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);

        final EditText name = (EditText) view.findViewById(R.id.newNameEditText);
        final Button save = (Button) view.findViewById(R.id.saveNewName);

        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);

        // set current name
        name.setText(Config.getName());
        // cursor at end
        name.setSelection(name.getText().length());

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String n = name.getText().toString().trim();
                if (!n.equals("")) {
                    Config.setName(name.getText().toString());
                    Toast.makeText(getActivity(), "Name saved", Toast.LENGTH_SHORT).show();
                    nameText.setText("Name: " + Config.getName());
                } else {
                    Toast.makeText(getActivity(), "Nothing to save", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    View.OnClickListener vibrateOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(Config.isVibratorOn()) {
                Config.setVibrator(false);
                Toast.makeText(getActivity(), "Vibrations off", Toast.LENGTH_SHORT).show();
                vibrate.setText("Off");
            } else {
                Config.setVibrator(true);
                Toast.makeText(getActivity(), "Vibrations on", Toast.LENGTH_SHORT).show();
                vibrate.setText("On");
            }
        }
    };

    View.OnClickListener soundOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(Config.isIsSoundOn()) {
                Config.setIsSoundOn(false);
                Toast.makeText(getActivity(), "Sound off", Toast.LENGTH_SHORT).show();
                sound.setText("Off");
                BackgroundMusic.getInstance(getActivity()).stop();
            } else {
                Config.setIsSoundOn(true);
                Toast.makeText(getActivity(), "Sound on", Toast.LENGTH_SHORT).show();
                sound.setText("On");
                BackgroundMusic.getInstance(getActivity()).start();
            }
        }
    };

}
