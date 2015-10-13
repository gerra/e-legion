package com.homework3.german.viewpager;

import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    }

    public class IconPagerAdapter extends FragmentPagerAdapter {

        public IconPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return PlaceholderFragment.newInstance(R.drawable.android);
                case 1:
                    return PlaceholderFragment.newInstance(R.drawable.apple);
                case 2:
                    return PlaceholderFragment.newInstance(R.drawable.github);
                default:
                    return PlaceholderFragment.newInstance(R.drawable.playmarket);
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    }

    public static class PlaceholderFragment extends Fragment {
        private static final String IMAGE_ID = "image_id";

        public static PlaceholderFragment newInstance(@DrawableRes int imageId) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(IMAGE_ID, imageId);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            ImageView icon = (ImageView) rootView.findViewById(R.id.icon);
            icon.setImageResource(getArguments().getInt(IMAGE_ID));
            return rootView;
        }
    }
}
