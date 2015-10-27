package ru.projects.german.vkplaylister.adapter.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ru.projects.german.vkplaylister.R;
import ru.projects.german.vkplaylister.TheApp;
import ru.projects.german.vkplaylister.model.Album;

public class AlbumViewHolder extends BinderViewHolder<Album> {
    private TextView title;
    private TextView audioCount;
    private ImageView syncWithVk;

    public AlbumViewHolder(View itemView) {
        super(itemView);
        title = (TextView) itemView.findViewById(R.id.title);
        audioCount = (TextView) itemView.findViewById(R.id.itemCount);
        syncWithVk = (ImageView) itemView.findViewById(R.id.sync_with_vk);
    }

    @Override
    public void bindItem(Album item) {
        title.setText(item.getTitle());
        int cnt = item.getAvailableCount();
        audioCount.setText(TheApp.getApp().getResources().getQuantityString(R.plurals.items_count, cnt, cnt));
        syncWithVk.setImageResource(item.isSynchronizedWithVk() ? R.drawable.vk
                : R.drawable.android);
    }
}
