package ru.projects.german.vkplaylister.fragment.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import ru.projects.german.vkplaylister.R;
import ru.projects.german.vkplaylister.activity.MainActivity;
import ru.projects.german.vkplaylister.fragment.CreateAlbumFragment;

/**
 * Created on 24.10.15.
 *
 * @author German Berezhko, gerralizza@gmail.com
 */
public class AlbumTitleDialogFragment extends DialogFragment {
    public static final String TAG = AlbumTitleDialogFragment.class.getSimpleName();
    private static final String EDIT_TEXT_KEY = "EDIT_TEXT_KEY";

    private EditText editText;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        editText = new EditText(getActivity());
        final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.create_album_dialog_title)
                .setMessage(R.string.create_album_dialog_message)
                .setView(editText)
                .setPositiveButton(R.string.dialog_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String title = editText.getText().toString();
                        ((MainActivity) getActivity()).openFragment(CreateAlbumFragment.newInstance(title), true);
//                        final ProgressDialogFragment progressDialog = ProgressDialogFragment.newInstance(
//                                getResources().getString(R.string.dialog_wait_title),
//                                getResources().getString(R.string.create_album_dialog_wait_message)
//                        );
//                        final FragmentManager fragmentManager = getFragmentManager();
//                        progressDialog.show(fragmentManager, ProgressDialogFragment.TAG);
//                        DataManager.createEmptyAlbumByTitleAndGetId(title, new VKRequest.VKRequestListener() {
//                            @Override
//                            public void onComplete(VKResponse response) {
//                                int albumId = -1;
//                                try {
//                                    albumId = response.json
//                                            .getJSONObject("response")
//                                            .getInt(Constants.VK_ALBUM_ID);
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                                Otto.post(new NeedCloseFragmentEvent(ProgressDialogFragment.TAG));
//                                Otto.post(new NeedOpenFragmentEvent(CreateAlbumFragment.newInstance(title, albumId), true));
//                            }
//
//                            @Override
//                            public void onError(VKError error) {
//                                Log.e(TAG, error.toString());
//                                Otto.post(new NeedCloseFragmentEvent(ProgressDialogFragment.TAG));
//                            }
//                        });
                    }
                })
                .setNegativeButton(R.string.dialog_negative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE)
                        .setEnabled(editText.getText().length() != 0);
            }
        });
        if (savedInstanceState != null) {
            editText.setText(savedInstanceState.getCharSequence(EDIT_TEXT_KEY));
            editText.setSelection(editText.getText().length());
        }
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(s.length() != 0);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(s.length() != 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(s.length() != 0);
            }
        });
        return dialog;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence(EDIT_TEXT_KEY, editText.getText());
    }
}
