package com.homework3.german.task2;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private BroadcastReceiver broadcastReceiver;
    private IntentFilter intentFilter;

    private String getPackageName(Intent intent) {
        Uri uri = intent.getData();
        return uri != null ? uri.getSchemeSpecificPart() : null;
    }

    private void goToTeGooglePlay(String packageName) {
        try {
            startActivity(new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=" + packageName))
            );
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=" + packageName))
            );
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i("InstallationDetector", "New package was installed");

                final String packageName = getPackageName(intent);
                if (packageName != null) {
                    findViewById(R.id.packageName).setVisibility(View.VISIBLE);
                    findViewById(R.id.goToPM).setVisibility(View.VISIBLE);
                    ((TextView) findViewById(R.id.packageName)).setText(packageName);
                    findViewById(R.id.goToPM).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            goToTeGooglePlay(packageName);
                        }
                    });
                }
            }
        };
        intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addDataScheme("package");
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }
}
