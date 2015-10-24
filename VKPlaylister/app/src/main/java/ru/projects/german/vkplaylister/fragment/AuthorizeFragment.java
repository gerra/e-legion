package ru.projects.german.vkplaylister.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vk.sdk.VKSdk;
import com.vk.sdk.api.model.VKScopes;

import ru.projects.german.vkplaylister.R;
import ru.projects.german.vkplaylister.activity.MainActivity;

/**
 * Created by root on 14.10.15.
 */
public class AuthorizeFragment extends Fragment {
    public static final String TAG = AuthorizeFragment.class.getSimpleName();

    public static AuthorizeFragment newInstance() {
        AuthorizeFragment fragment = new AuthorizeFragment();

        return fragment;
    }

    private MainActivity activity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (MainActivity) activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        view.findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VKSdk.login(activity, VKScopes.AUDIO);
            }
        });
        return view;
    }
}
