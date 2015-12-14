package ru.projects.german.vkplaylister.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import ru.projects.german.vkplaylister.R;
import ru.projects.german.vkplaylister.RecycleViewLoadingScrollListener;
import ru.projects.german.vkplaylister.TheApp;
import ru.projects.german.vkplaylister.activity.MainActivity;
import ru.projects.german.vkplaylister.adapter.BaseAudioListAdapter;
import ru.projects.german.vkplaylister.adapter.RecyclerItemClickListener;
import ru.projects.german.vkplaylister.loader.LoadingHelper;
import ru.projects.german.vkplaylister.loader.ModernAudiosLoader;
import ru.projects.german.vkplaylister.model.Album;
import ru.projects.german.vkplaylister.model.Audio;

/**
 * Created on 22.10.15.
 *
 * @author German Berezhko, gerralizza@gmail.com
 */
public abstract class BaseAudiosFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Audio.AudioList>, HasTitle {
    private static final String TAG = BaseAudiosFragment.class.getSimpleName();
    protected static final String ALBUM_KEY = "ALBUM_KEY";
    protected static final String ALBUM_IS_JUST_CREATED_KEY = "ALBUM_IS_JUST_CREATED_KEY";

    protected RecyclerView audioList;
    protected BaseAudioListAdapter adapter;
    protected RecyclerItemClickListener onItemClickListener;
    protected RecyclerView.OnScrollListener onScrollListener;

    protected abstract void initAdapter();
    protected void initOnItemClickListener() {}

    private void initOnScrollListener() {
        if (onScrollListener == null) {
            onScrollListener = new RecycleViewLoadingScrollListener(
                    10,
                    new RecycleViewLoadingScrollListener.OnLoadListener() {
                        @Override
                        public void onLoad(int layoutManagerItemCount) {
                            Log.d(TAG, "Scrolling to the bottom");
                            ModernAudiosLoader loader = (ModernAudiosLoader)
                                    getLoaderManager().<Audio.AudioList>getLoader(R.id.audios_loader);
                            if (loader != null && !loader.isRunning()) {
                                Log.d(TAG, "loader is running: " + loader.isRunning());
                                loader.loadMoreAudios(adapter.getItemCount());
                            }
                        }
                    });
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        if (adapter == null) {
            initAdapter();
        }
        Album album = getAlbum();
        if (album != null) {
            updateAudiosInAdapter(album.getAudios());
            adapter.setAlbum(album);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "AudioListLoader in lm: " + (getLoaderManager().getLoader(R.id.audios_loader) != null));
        Bundle args = getArguments();
        boolean isJustCreated = false;
        if (args != null) {
            isJustCreated = args.getBoolean(ALBUM_IS_JUST_CREATED_KEY, false);
        }
        Album album = getAlbum();
        if ((album == null || album.isSynchronizedWithVk()) && !isJustCreated) {
            if (getLoaderManager().getLoader(R.id.audios_loader) == null) {
                getLoaderManager().initLoader(R.id.audios_loader, getArguments(), this);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_audios, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        audioList = (RecyclerView) view.findViewById(R.id.audio_list);
        audioList.setLayoutManager(new LinearLayoutManager(getActivity()));
        audioList.setAdapter(adapter);
//        audioList.addItemDecoration(new RecyclerView.ItemDecoration() {
//            @Override
//            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//                int margin = getResources().getDimensionPixelOffset(R.dimen.extra_small_item_padding);
//                outRect.set(margin, margin, margin, 0);
//            }
//        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (onItemClickListener == null) {
            initOnItemClickListener();
        }
        if (onScrollListener == null) {
            initOnScrollListener();
        }
        if (onItemClickListener != null) {
            audioList.addOnItemTouchListener(onItemClickListener);
        }
        if (onScrollListener != null) {
            audioList.addOnScrollListener(onScrollListener);
        }
        getMainActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getMainActivity().getSupportActionBar().setDisplayShowHomeEnabled(true);
        getMainActivity().getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (onItemClickListener != null) {
            audioList.removeOnItemTouchListener(onItemClickListener);
        }
        if (onScrollListener != null) {
            audioList.removeOnScrollListener(onScrollListener);
        }
    }

    private void updateAudiosInAdapter(Audio.AudioList audios) {
        Log.d(TAG, "updateAudiosInAdapter()");
        adapter.addAudios(audios);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getMainActivity().closeCurrentFragment();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Audio.AudioList> onCreateLoader(int id, Bundle args) {
        if (id == R.id.audios_loader) {
            Log.d(TAG, "onCreateLoader");
            final Album album = getAlbum();
            ModernAudiosLoader loader = new ModernAudiosLoader(TheApp.getApp(), album, new LoadingHelper() {
                @Override
                public void onStartLoading() {
                    adapter.addLoadingItem();
                }

                @Override
                public boolean needLoading() {
                    int adapterCount = adapter.getItemCount();
                    int totalCount = adapter.getAudios().getTotalCount();
                    if (album != null && album.getTotalCount() > totalCount) {
                        totalCount = album.getTotalCount();
                    }
                    Log.d(TAG, "checkNeedLoading(): " + adapterCount + " " + totalCount);
                    Log.d(TAG, "checkNeedLoading(): " + (totalCount == -1 || adapterCount < totalCount));
                    return totalCount == -1 || adapterCount < totalCount;
                }
            });

            return loader;
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Audio.AudioList> loader, Audio.AudioList data) {
        if (loader.getId() == R.id.audios_loader) {
            Log.d(TAG, "onLoadFinished");
            if (data != null) {
                Log.d(TAG, "Audios were gotten, size = " + data.size());
                updateAudiosInAdapter(data);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Audio.AudioList> loader) {
        Log.d(TAG, "onLoaderReset");
    }

    public MainActivity getMainActivity() {
        return (MainActivity) getActivity();
    }

    protected Album getAlbum() {
        if (getArguments() != null) {
            return (Album) getArguments().getSerializable(ALBUM_KEY);
        }
        return null;
    }

    @Override
    public void onStart() {
        super.onStart();
        TheApp.getPlayerHelper().registerListener(adapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        TheApp.getPlayerHelper().unregisterListener(adapter);
    }
}
