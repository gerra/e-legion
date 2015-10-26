package ru.projects.german.vkplaylister.loader;

/**
 * Created on 26.10.15.
 *
 * @author German Berezhko, gerralizza@gmail.com
 */
public abstract class LoadingHelper {
    public void onStartLoading() {
    }
    public boolean needLoading() {
        return true;
    }
}
