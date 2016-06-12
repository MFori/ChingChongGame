package cz.martinforejt.chingchong;

import android.animation.ObjectAnimator;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;

/**
 * Created by Martin Forejt on 16.05.2016.
 * forejt.martin97@gmail.com
 * class AboutFragment
 */
public class AboutFragment extends Fragment {

    public static final String TAG = "about";

    private ScrollView scroll;
    private ObjectAnimator scrollAnimator = null;
    private boolean endScrolling = false;

    public AboutFragment() {
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
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        scroll = (ScrollView) view.findViewById(R.id.about_scroll);

        scroll.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {


                if(!endScrolling) {
                    //ObjectAnimator.ofInt(scroll, "scrollY", scroll.getHeight()).setDuration(10000).start();
                    scrollAnimator = ObjectAnimator.ofInt(scroll, "scrollY", scroll.getHeight());
                    scrollAnimator.setDuration(10000).start();
                }

                if (Build.VERSION.SDK_INT < 16) {
                    scroll.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    scroll.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });

        scroll.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(scrollAnimator!= null && !endScrolling) scrollAnimator.cancel();
                endScrolling = true;
                return false;
            }
        });

        return view;
    }

}
