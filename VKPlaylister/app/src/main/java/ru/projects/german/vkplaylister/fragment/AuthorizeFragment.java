package ru.projects.german.vkplaylister.fragment;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
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

    private void animateLogo(View vkLogo) {
        vkLogo.setScaleX(0f);
        vkLogo.setScaleY(0f);
        vkLogo.setAlpha(0f);

        ObjectAnimator animator1 = ObjectAnimator.ofFloat(vkLogo, "rotation", 720f);
        animator1.setDuration(1000);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(vkLogo, "alpha", 1f);
        animator2.setDuration(1000);
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(vkLogo, "scaleX", 1f);
        animator3.setDuration(1000);
        ObjectAnimator animator4 = ObjectAnimator.ofFloat(vkLogo, "scaleY", 1f);
        animator4.setDuration(1000);

        AnimatorSet set = new AnimatorSet();
        set.play(animator1)
                .with(animator2)
                .with(animator3)
                .with(animator4);
        set.start();
    }

    private void animateButton(View loginButton) {
        loginButton.setScaleX(0f);
        loginButton.setScaleY(0f);
        loginButton.setAlpha(0f);

        ObjectAnimator animator1 = ObjectAnimator.ofFloat(loginButton, "alpha", 1f);
        animator1.setDuration(1000);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(loginButton, "scaleX", 1f);
        animator2.setDuration(1000);
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(loginButton, "scaleY", 1f);
        animator3.setDuration(1000);

        AnimatorSet set = new AnimatorSet();
        set.play(animator1)
                .with(animator2)
                .with(animator3);
        set.start();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        View loginButton = view.findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VKSdk.login(activity, VKScopes.AUDIO);
            }
        });

        if (savedInstanceState == null) {
            animateButton(loginButton);
            animateLogo(view.findViewById(R.id.login_image));
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) getActivity()).getSupportActionBar().hide();
    }
}
