package cz.martinforejt.chingchong;

import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class NewPlayerFragment extends Fragment {

    public static final String TAG = "newPlayer";

    private EditText editName;
    private Button submit;

    public NewPlayerFragment() {
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
        View view = inflater.inflate(R.layout.fragment_new_player, container, false);

        editName = (EditText) view.findViewById(R.id.edit_name_first);
        submit = (Button) view.findViewById(R.id.btn_name_first);
        setFont();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editName.getText().toString().trim();

                if (!name.equals("")) {
                    Config.setName(name);
                    ((GameActivity) getActivity()).changeFragment(MenuFragment.newInstance(), MenuFragment.TAG, true);
                }
            }
        });

        return view;
    }

    /**
     * Set chlorinr font to submit button
     */
    private void setFont() {
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/chlorinr.ttf");
        submit.setTypeface(typeface);
    }

}
