package ru.projects.german.vkplaylister;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

import ru.projects.german.vkplaylister.fragment.AlbumsFragment;
import ru.projects.german.vkplaylister.fragment.AuthorizeFragment;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

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
    }

    public void openFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (addToBackStack) {
            ft.addToBackStack(null);
        }
        ft.replace(R.id.container, fragment).commit();
    }

    public void openFragment(Fragment fragment) {
        openFragment(fragment, false);
    }

    public void closeLastFragment() {
        getSupportFragmentManager()
                .popBackStackImmediate();
    }

    public void closeFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .remove(fragment)
                .commit();
    }

    private void launchLoginFragment() {
        openFragment(AuthorizeFragment.newInstance());
    }

    public void launchAfterLoginFragment() {
        openFragment(AlbumsFragment.newInstance());
//        openFragment(AudiosFragment.newInstance());
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
