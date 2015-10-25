package ru.projects.german.vkplaylister.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

import ru.projects.german.vkplaylister.R;
import ru.projects.german.vkplaylister.fragment.AlbumsFragment;
import ru.projects.german.vkplaylister.fragment.AuthorizeFragment;
import ru.projects.german.vkplaylister.fragment.HasTitle;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private FragmentManager.OnBackStackChangedListener onBackStackChangedListener;

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

    public void openFragment(Fragment fragment, boolean addFragmentToBackStack) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (!addFragmentToBackStack) {
            addFragmentToBackStack = fm.popBackStackImmediate();
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
}
