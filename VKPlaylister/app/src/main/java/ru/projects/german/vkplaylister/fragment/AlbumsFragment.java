package ru.projects.german.vkplaylister.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.projects.german.vkplaylister.R;
import ru.projects.german.vkplaylister.TheApp;
import ru.projects.german.vkplaylister.activity.MainActivity;
import ru.projects.german.vkplaylister.adapter.AlbumListAdapter;
import ru.projects.german.vkplaylister.adapter.RecyclerItemClickListener;
import ru.projects.german.vkplaylister.fragment.dialog.AlbumTitleDialogFragment;
import ru.projects.german.vkplaylister.loader.AlbumsLoader;
import ru.projects.german.vkplaylister.model.Album;
import ru.projects.german.vkplaylister.otto.Otto;

/**
 * Created on 17.10.15.
 *
 * @author German Berezhko, gerralizza@gmail.com
 */
public class AlbumsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Album.AlbumList> {
    private static final String TAG = AlbumsFragment.class.getSimpleName();

    private RecyclerView albumList;
    private AlbumListAdapter adapter;
    private RecyclerItemClickListener onItemClickListener;
    private FloatingActionButton addAlbumButton;

    public static AlbumsFragment newInstance() {
        AlbumsFragment fragment = new AlbumsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (adapter == null) {
            adapter = new AlbumListAdapter();
        }
        if (onItemClickListener == null) {
            onItemClickListener = new RecyclerItemClickListener(TheApp.getApp(), new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public boolean onItemClick(View view, int position) {
                    Album album = adapter.getItem(position);
                    Log.d(TAG, "Clicked album " + album.getTitle());
                    ((MainActivity) getActivity()).openFragment(
                            AlbumFragment.newInstance(album), true);
                    return true;
                }

                @Override
                public boolean onItemLongPress(View view, int position) {
                    Album album = adapter.getItem(position);
                    Log.d(TAG, "Long press on album " + album.getTitle());
                    return true;
                }
            });
        }
        Otto.register(adapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (adapter != null) {
            Otto.register(adapter);
        }
    }

    @Override
    public void onDetach() {
        Otto.unregister(adapter);
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        albumList.addOnItemTouchListener(onItemClickListener);
        addAlbumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialog = new AlbumTitleDialogFragment();
                dialog.setShowsDialog(true);
                dialog.show(getFragmentManager(), AlbumTitleDialogFragment.TAG);
            }
        });
        getMainActivity().getSupportActionBar().setHomeButtonEnabled(false);
        getMainActivity().getSupportActionBar().setDisplayShowHomeEnabled(false);
        getMainActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        albumList.removeOnItemTouchListener(onItemClickListener);
        addAlbumButton.setOnClickListener(null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(R.id.albums_loader, null, this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_albums, container, false);
        addAlbumButton = (FloatingActionButton) view.findViewById(R.id.add_album_fab);
        albumList = (RecyclerView) view.findViewById(R.id.album_list);
        albumList.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        albumList.setAdapter(adapter);
        return view;
    }

    @Override
    public Loader<Album.AlbumList> onCreateLoader(int id, Bundle args) {
        if (id == R.id.albums_loader) {
            Log.d(TAG, "onCreateLoader");
            return new AlbumsLoader(TheApp.getApp());
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Album.AlbumList> loader, Album.AlbumList data) {
        if (loader.getId() == R.id.albums_loader) {
            Log.d(TAG, "onLoadFinished");
            adapter.addAlbums(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Album.AlbumList> loader) {
        Log.d(TAG, "onLoaderReset");
    }

    public MainActivity getMainActivity() {
        return (MainActivity) getActivity();
    }
}
