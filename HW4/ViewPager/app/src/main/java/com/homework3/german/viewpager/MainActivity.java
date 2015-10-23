package com.homework3.german.viewpager;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    private IconPagerAdapter mIconPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mIconPagerAdapter = new IconPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mIconPagerAdapter);
        mViewPager.setPageTransformer(false, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View page, float position) {
                int pageWidth = page.getWidth();
                int pageHeight = page.getHeight();
                ImageView icon = (ImageView) page.findViewById(R.id.icon);

                icon.setTranslationX(-position * pageWidth / 2);
                icon.setTranslationY(-position * pageHeight / 2);

                if (position < -1f || position > 1f) {
                    icon.setAlpha(0f);
                } else if (position < 0f) {
                    icon.setAlpha(1f + position);
                    icon.setScaleX(1f + position);
                    icon.setScaleY(1f + position);
                } else {
                    icon.setAlpha(1f - position);
                    icon.setScaleX(1f - position);
                    icon.setScaleY(1f - position);
                }
            }
        });

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIconPagerAdapter.getCount() > 1) {
                    DialogFragment dialog = new DialogFragment() {
                        @NonNull
                        @Override
                        public Dialog onCreateDialog(Bundle savedInstanceState) {
                            return new AlertDialog.Builder(getActivity(), R.style.AppTheme_Dialog)
                                    .setTitle(R.string.deletion_confirm_title)
                                    .setMessage(R.string.deletion_confirm_message)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            removeCurrentPage();
                                            if (mIconPagerAdapter.getCount() == 1) {
                                                fab.hide();
                                            }
                                        }
                                    })
                                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }).create();
                        }
                    };
                    dialog.setShowsDialog(true);
                    dialog.show(getSupportFragmentManager(), "YesNoDialog");
                }
            }
        });
    }

    private void removeCurrentPage() {
        int currentItem = mViewPager.getCurrentItem();
        mIconPagerAdapter.removePage(currentItem);
    }
}
