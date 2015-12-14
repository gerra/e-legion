package ru.projects.german.vkplaylister.fragment.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

/**
 * Created on 24.10.15.
 *
 * @author German Berezhko, gerralizza@gmail.com
 */
public class ProgressDialogFragment extends DialogFragment{
    public static final String TAG = ProgressDialogFragment.class.getSimpleName();
    private static final String TITLE_KEY = "TITLE_KEY";
    private static final String MESSAGE_KEY = "MESSAGE_KEY";

    public static ProgressDialogFragment newInstance(String title, String message) {
        ProgressDialogFragment fragment = new ProgressDialogFragment();
        Bundle args = new Bundle();
        args.putString(TITLE_KEY, title);
        args.putString(MESSAGE_KEY, message);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = "";
        String message = "";
        if (getArguments() != null) {
            title = getArguments().getString(TITLE_KEY);
            message = getArguments().getString(MESSAGE_KEY);
        }
        ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        return dialog;
    }
}
