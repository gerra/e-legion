package ru.projects.german.vkplaylister.fragment.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import ru.projects.german.vkplaylister.R;
import ru.projects.german.vkplaylister.otto.Otto;
import ru.projects.german.vkplaylister.otto.SyncWithVkEvent;

/**
 * Created on 27.10.15.
 *
 * @author German Berezhko, gerralizza@gmail.com
 */
public class SyncWithVkDialogFragment extends DialogFragment {
    public static final String TAG = SyncWithVkDialogFragment.class.getSimpleName();

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity(), R.style.AppTheme_Dialog)
                .setCancelable(false)
                .setTitle(R.string.sync_with_vk_dialog_title)
                .setMessage(R.string.sync_with_vk_dialog_message)
                .setPositiveButton(R.string.dialog_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Otto.post(new SyncWithVkEvent());
                    }
                })
                .setNegativeButton(R.string.dialog_negative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        return alertDialog;
    }
}
