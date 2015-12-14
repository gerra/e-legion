package ru.projects.german.vkplaylister.fragment;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.otto.Subscribe;

import ru.projects.german.vkplaylister.R;
import ru.projects.german.vkplaylister.TheApp;
import ru.projects.german.vkplaylister.activity.MainActivity;
import ru.projects.german.vkplaylister.adapter.AlbumListAdapter;
import ru.projects.german.vkplaylister.adapter.RecyclerItemClickListener;
import ru.projects.german.vkplaylister.data.DataManager;
import ru.projects.german.vkplaylister.fragment.dialog.AlbumTitleDialogFragment;
import ru.projects.german.vkplaylister.fragment.dialog.ProgressDialogFragment;
import ru.projects.german.vkplaylister.fragment.dialog.SyncWithVkDialogFragment;
import ru.projects.german.vkplaylister.loader.AlbumsLoader;
import ru.projects.german.vkplaylister.model.Album;
import ru.projects.german.vkplaylister.otto.Otto;
import ru.projects.german.vkplaylister.otto.SyncWithVkEvent;

/**
 * Created on 17.10.15.
 *
 * @author German Berezhko, gerralizza@gmail.com
 */
public class AlbumsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Album.AlbumList> {
    private static final String TAG = AlbumsFragment.class.getSimpleName();

    private RecyclerView albumList;
    private AlbumListAdapter adapter;
    private FloatingActionButton addAlbumButton;

    public static AlbumsFragment newInstance() {
        AlbumsFragment fragment = new AlbumsFragment();
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        Otto.register(this);
        TheApp.getPlayerHelper().registerListener(adapter);
        getMainActivity().getSupportActionBar().show();
    }

    @Override
    public void onStop() {
        Otto.unregister(this);
        super.onStop();
        TheApp.getPlayerHelper().unregisterListener(adapter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (adapter == null) {
            adapter = new AlbumListAdapter();
            adapter.addAlbums(DataManager.getAlbumList());
            adapter.setOnClickListener(new RecyclerItemClickListener.OnItemClickListener() {
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
//        if (onItemClickListener == null) {
//            onItemClickListener = new RecyclerItemClickListener(TheApp.getApp(), new RecyclerItemClickListener.OnItemClickListener() {
//                @Override
//                public boolean onItemClick(View view, int position) {
//                    Album album = adapter.getItem(position);
//                    Log.d(TAG, "Clicked album " + album.getTitle());
//                    ((MainActivity) getActivity()).openFragment(
//                            AlbumFragment.newInstance(album), true);
//                    return true;
//                }
//
//                @Override
//                public boolean onItemLongPress(View view, int position) {
//                    Album album = adapter.getItem(position);
//                    Log.d(TAG, "Long press on album " + album.getTitle());
//                    return true;
//                }
//            });
//        }
        Otto.register(adapter);

        if (savedInstanceState == null) {
            if (!DataManager.isSyncWithVk()) {
                SyncWithVkDialogFragment fragment = new SyncWithVkDialogFragment();
                fragment.show(getFragmentManager(), SyncWithVkDialogFragment.TAG);
            }
        }
//        TheApp.getPlayerHelper().registerListener(adapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        TheApp.getPlayerHelper().unregisterListener(adapter);
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
//        albumList.addOnItemTouchListener(onItemClickListener);
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
//        albumList.removeOnItemTouchListener(onItemClickListener);
        addAlbumButton.setOnClickListener(null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //getLoaderManager().initLoader(R.id.albums_loader, null, this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_albums, container, false);
        addAlbumButton = (FloatingActionButton) view.findViewById(R.id.add_album_fab);
        albumList = (RecyclerView) view.findViewById(R.id.album_list);
//        albumList.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        albumList.setLayoutManager(new LinearLayoutManager(getActivity()));
        albumList.setAdapter(adapter);
        albumList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int margin = getResources().getDimensionPixelOffset(R.dimen.extra_small_item_padding);
                outRect.set(margin, margin, margin, 0);
            }
        });
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

    @SuppressWarnings("unused")
    @Subscribe
    public void onSyncWithVk(SyncWithVkEvent event) {
        ProgressDialogFragment progressDialog = ProgressDialogFragment.newInstance(
                getResources().getString(R.string.dialog_wait_title),
                getResources().getString(R.string.sync_with_vk_dialog_running)
        );
        progressDialog.show(getFragmentManager(), ProgressDialogFragment.TAG);
        DataManager.syncWithVk(new DataManager.MyRequestListener() {
            @Override
            public void onComplete(Object object) {
                Fragment progressDialog = getFragmentManager().findFragmentByTag(ProgressDialogFragment.TAG);
                if (progressDialog != null) {
                    getFragmentManager().beginTransaction()
                            .remove(progressDialog)
                            .commit();
                }
                if (adapter != null) {
                    adapter.addAlbums((Album.AlbumList) object);
                }
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, error);
                Fragment progressDialog = getFragmentManager().findFragmentByTag(ProgressDialogFragment.TAG);
                if (progressDialog != null) {
                    getFragmentManager().beginTransaction()
                            .remove(progressDialog)
                            .commit();
                }
            }
        });
    }
}
