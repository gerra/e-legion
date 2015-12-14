package com.homework3.german.viewpager;

import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by root on 13.10.15.
 */
public class PlaceholderFragment extends Fragment {
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
