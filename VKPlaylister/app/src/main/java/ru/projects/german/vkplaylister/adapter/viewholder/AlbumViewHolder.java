package ru.projects.german.vkplaylister.adapter.viewholder;

import android.view.View;
import android.widget.TextView;

import ru.projects.german.vkplaylister.R;
import ru.projects.german.vkplaylister.TheApp;
import ru.projects.german.vkplaylister.model.Album;

public class AlbumViewHolder extends BinderViewHolder<Album> {
    private TextView title;
    private TextView itemCount;

    public AlbumViewHolder(View itemView) {
        super(itemView);
        title = (TextView) itemView.findViewById(R.id.title);
        itemCount = (TextView) itemView.findViewById(R.id.itemCount);
    }

    @Override
    public void bindItem(Album item) {
        title.setText(item.getTitle());
        int cnt = item.getTotalCount();
        itemCount.setText(TheApp.getApp().getResources().getQuantityString(R.plurals.items_count, cnt, cnt));
    }
}
