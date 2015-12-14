package ru.projects.german.vkplaylister.otto;

/**
 * Created on 26.10.15.
 *
 * @author German Berezhko, gerralizza@gmail.com
 */
public class NeedCloseFragmentEvent {
    private String tag;

    public NeedCloseFragmentEvent(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }
}
