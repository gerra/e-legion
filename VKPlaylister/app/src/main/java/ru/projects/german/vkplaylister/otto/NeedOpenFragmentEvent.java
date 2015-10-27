package ru.projects.german.vkplaylister.otto;

import android.support.v4.app.Fragment;

/**
 * Created on 26.10.15.
 *
 * @author German Berezhko, gerralizza@gmail.com
 */
public class NeedOpenFragmentEvent {
    private Fragment fragment;
    private boolean addFragmentToBackStack;

    public NeedOpenFragmentEvent(Fragment fragment, boolean addFragmentToBackStack) {
        this.fragment = fragment;
        this.addFragmentToBackStack = addFragmentToBackStack;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public boolean getAddFragmentToBackStack() {
        return addFragmentToBackStack;
    }
}
