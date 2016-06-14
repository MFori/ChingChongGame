package cz.martinforejt.chingchong;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class MultiPlayerFragment extends Fragment {

    public static final String TAG = "multi";

    private Button create, join;

    public MultiPlayerFragment() {
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
        View view = inflater.inflate(R.layout.fragment_multi_player, container, false);

        create = (Button) view.findViewById(R.id.multi_create);
        join = (Button) view.findViewById(R.id.multi_join);
        setFont();

        return view;
    }

    /**
     * Set chlorinr (assets/fonts/chlorinr.ttf) font to multiplayer-menu buttons
     */
    private void setFont() {
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/chlorinr.ttf");
        create.setTypeface(typeface);
        join.setTypeface(typeface);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
