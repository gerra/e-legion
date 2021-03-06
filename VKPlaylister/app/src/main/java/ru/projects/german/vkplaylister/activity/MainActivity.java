package ru.projects.german.vkplaylister.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.squareup.otto.Subscribe;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

import ru.projects.german.vkplaylister.BuildConfig;
import ru.projects.german.vkplaylister.R;
import ru.projects.german.vkplaylister.fragment.AlbumsFragment;
import ru.projects.german.vkplaylister.fragment.AuthorizeFragment;
import ru.projects.german.vkplaylister.fragment.HasTitle;
import ru.projects.german.vkplaylister.fragment.OnBackPressedListener;
import ru.projects.german.vkplaylister.otto.NeedCloseFragmentEvent;
import ru.projects.german.vkplaylister.otto.NeedOpenFragmentEvent;
import ru.projects.german.vkplaylister.otto.Otto;
import ru.projects.german.vkplaylister.player.MusicController;
import ru.projects.german.vkplaylister.player.MyMediaUiControll;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private FragmentManager.OnBackStackChangedListener onBackStackChangedListener;

    public MusicController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            if (VKAccessToken.currentToken() == null) {
                launchLoginFragment();
            } else {
                launchAfterLoginFragment();
            }
        }
        updateTitle();
        if (onBackStackChangedListener == null) {
            onBackStackChangedListener = new FragmentManager.OnBackStackChangedListener() {
                @Override
                public void onBackStackChanged() {
                    updateTitle();
                }
            };
        }

        controller = new MusicController(this);
        controller.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "next");
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "prev");
            }
        });
        controller.setAnchorView(findViewById(R.id.main_container));
        controller.setMediaPlayer(new MyMediaUiControll());
        controller.setEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportFragmentManager().addOnBackStackChangedListener(onBackStackChangedListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getSupportFragmentManager().removeOnBackStackChangedListener(onBackStackChangedListener);
    }

    private void updateTitle() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
        updateTitle(fragment);
    }

    private void updateTitle(Fragment fragment) {
        if (fragment instanceof HasTitle) {
            getSupportActionBar().setTitle(((HasTitle) fragment).getTitle());
        } else {
            getSupportActionBar().setTitle(R.string.app_name);
        }
    }

    public void closeCurrentFragment() {
        getSupportFragmentManager().popBackStackImmediate();
        updateTitle();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public void openFragment(Fragment fragment, boolean addFragmentToBackStack) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (!addFragmentToBackStack) {
            if (fm.getBackStackEntryCount() > 0) {
                fm.popBackStack();
                addFragmentToBackStack = true;
            }
        }
        if (addFragmentToBackStack) {
            ft.addToBackStack(null);
        }
        ft.replace(R.id.container, fragment);
        ft.commit();
        updateTitle(fragment);
    }

    public void openFragment(Fragment fragment) {
        openFragment(fragment, false);
    }

    private void launchLoginFragment() {
        openFragment(AuthorizeFragment.newInstance());
    }

    public void launchAfterLoginFragment() {
        openFragment(AlbumsFragment.newInstance());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, requestCode + " " + resultCode);
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                // User passed Authorization
                launchAfterLoginFragment();
            }

            @Override
            public void onError(VKError error) {
                // User didn't pass Authorization
                Log.d(TAG, error.toString());
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed");
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
        if (fragment != null
                && fragment instanceof OnBackPressedListener && fragment.isResumed()) {
            ((OnBackPressedListener) fragment).onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Otto.register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Otto.unregister(this);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onNeedOpenFragment(NeedOpenFragmentEvent event) {
        openFragment(event.getFragment(), event.getAddFragmentToBackStack());
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onNeedCloseFragment(NeedCloseFragmentEvent event) {
        getSupportFragmentManager().beginTransaction()
                .remove(getSupportFragmentManager().findFragmentByTag(event.getTag()))
                .commit();
    }
}
