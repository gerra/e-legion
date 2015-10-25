package ru.projects.german.vkplaylister.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import java.util.HashSet;
import java.util.Set;

import ru.projects.german.vkplaylister.R;
import ru.projects.german.vkplaylister.adapter.viewholder.BaseAudioViewHolder;
import ru.projects.german.vkplaylister.adapter.viewholder.SelectAudioViewHolder;
import ru.projects.german.vkplaylister.model.Audio;

/**
 * Created on 22.10.15.
 *
 * @author German Berezhko, gerralizza@gmail.com
 */
public class SelectAudioAdapter extends BaseAudioListAdapter {
    public interface OnSelectItemListener {
        void onSelect(View view, int position);
    }

    private Set<Audio> selectedAudios = new HashSet<>();
    private Set<Integer> notAnimatedYet = new HashSet<>();

    private OnSelectItemListener onSelectItemListener;

    public SelectAudioAdapter(OnSelectItemListener onSelectItemListener) {
        this.onSelectItemListener = onSelectItemListener;
    }

    public void changeSelectStateAtPosition(int position) {
        Audio item = getItem(position);
        if (selectedAudios.contains(item)) {
            selectedAudios.remove(item);
        } else {
            selectedAudios.add(getItem(position));
        }
        notAnimatedYet.add(position);
        notifyItemChanged(position);
    }

    public Set<Audio> getSelectedAudios() {
        return selectedAudios;
    }

    @Override
    protected BaseAudioViewHolder getAudioViewHolder(ViewGroup parent) {
        View view = view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_audio_for_add, parent, false);
        return new SelectAudioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        super.onBindViewHolder(holder, position);
        if (holder instanceof SelectAudioViewHolder) {
            final boolean isSelected = selectedAudios.contains(getItem(position));
            final ImageView addButton = ((SelectAudioViewHolder) holder).addAudioButton;
//        ((SelectAudioViewHolder) holder).setSelected(selectedAudios.contains(getItem(position)));
            ((SelectAudioViewHolder) holder).addAudioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onSelectItemListener.onSelect(v, position);
                }
            });
            if (notAnimatedYet.contains(position)) {
                ScaleAnimation animation = new ScaleAnimation(1f, 0f, 1f, 1f,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);
                animation.setDuration(150);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        ScaleAnimation expandAnimation = new ScaleAnimation(0f, 1f, 1f, 1f,
                                Animation.RELATIVE_TO_SELF, 0.5f,
                                Animation.RELATIVE_TO_SELF, 0.5f);
                        expandAnimation.setDuration(150);
                        addButton.setImageResource(isSelected ? R.drawable.ic_remove_circle_white_48dp
                                : R.drawable.ic_add_circle_white_48dp);
                        addButton.startAnimation(expandAnimation);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                addButton.startAnimation(animation);
                notAnimatedYet.remove(position);
            } else {
                addButton.setImageResource(isSelected ? R.drawable.ic_remove_circle_white_48dp
                        : R.drawable.ic_add_circle_white_48dp);
            }
        }
    }
}
