package cz.martinforejt.chingchong;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

/**
 * Created by Martin Forejt on 07.06.2016.
 * forejt.martin97@gmail.com
 */
public class PauseDialog extends AlertDialog.Builder {

    protected static final int LAYOUT = R.layout.pause_dialog;
    protected Button end;

    private boolean isActive = false;

    protected AlertDialog dialog = null;

    /**
     * Construct
     *
     * @param context Context
     */
    public PauseDialog(Context context) {
        super(context);

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(LAYOUT, null);
        this.setView(view);

        end = (Button) view.findViewById(R.id.pauseDialogBtnEnd);
    }

    /**
     * Add on end button click listener
     *
     * @param listener View.OnclickListener
     */
    public void setEndListener(View.OnClickListener listener) {
        end.setOnClickListener(listener);
    }

    /**
     * Close dialog - destroy
     */
    public void close() {
        if (dialog != null && isActive) {
            dialog.dismiss();
        }
    }

    /**
     * Hide dialog - do not destroy
     */
    public void hide() {
        if (dialog != null && isActive) {
            dialog.dismiss();
            isActive = false;
        }
    }

    /**
     * Show/create dialog
     */
    public void open() {
        if (!isActive) {
            if (dialog == null) {
                dialog = super.create();
                dialog.setCanceledOnTouchOutside(false);
            }
            dialog.show();
            isActive = true;
        }
    }
}
