package ru.projects.german.vkplaylister;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

import java.util.Stack;

import ru.projects.german.vkplaylister.fragment.AlbumsFragment;
import ru.projects.german.vkplaylister.fragment.AuthorizeFragment;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private Stack<Fragment> fragmentStack = new Stack<>();

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
        } else {
            fragmentStack = (Stack<Fragment>) getLastCustomNonConfigurationInstance();
        }
//        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
//            @Override
//            public void onBackStackChanged() {
//                FragmentManager fm = getSupportFragmentManager();
//                Log.d(TAG, "BackStackEntryCount=" + fm.getBackStackEntryCount());
//                for (int i = 0; i < fm.getBackStackEntryCount(); i++) {
//                    Log.d(TAG, "BackStackEntry at " + i + " " + fm.getBackStackEntryAt(i).getName());
//                }
//            }
//        });
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return fragmentStack;
    }

    public void openFragment(Fragment fragment, boolean addToBackStack) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (addToBackStack) {
            ft.add(R.id.container, fragment);
            fragmentStack.push(fragment);
        } else {
            if (fragmentStack.size() > 0) {
                ft.remove(fragmentStack.pop());
            }
            ft.add(R.id.container, fragment);
            fragmentStack.push(fragment);
        }
        ft.commit();
    }

    public void openFragment(Fragment fragment) {
        openFragment(fragment, false);
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

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && fragmentStack.size() > 1) {
            Log.d(TAG, "Back button pressed");
            getSupportFragmentManager().beginTransaction().remove(fragmentStack.pop()).commit();
            return true;
        }
        return false;
    }
}
