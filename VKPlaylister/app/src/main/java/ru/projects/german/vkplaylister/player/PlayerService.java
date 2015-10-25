package ru.projects.german.vkplaylister.player;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.MediaSession;
import android.media.session.MediaSessionManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

import ru.projects.german.vkplaylister.TheApp;
import ru.projects.german.vkplaylister.data.DataManager;
import ru.projects.german.vkplaylister.model.Audio;

/**
 * Created by root on 15.10.15.
 */
public class PlayerService extends Service {
    private static final String TAG = PlayerService.class.getSimpleName();

    private static final String ACTION_PLAY = "ACTION_PLAY";

    private static final String ORDER_KEY = "ORDER_KEY";
    private static final String POSITION_TO_PLAY_KEY = "POSITION_TO_PLAY_KEY";

    public static void startPlaying(Audio.AudioList order, int positionToPlay) {
        Log.d(TAG, "Creating intent to play...");
        Intent intent = new Intent(TheApp.getApp(), PlayerService.class);
        intent.setAction(ACTION_PLAY);
        intent.putExtra(ORDER_KEY, order);
        intent.putExtra(POSITION_TO_PLAY_KEY, positionToPlay);
        TheApp.getApp().startService(intent);
        Log.d(TAG, "...Intent created");
    }

    private MediaPlayer mediaPlayer;
    private MediaSessionManager manager;
    private MediaSession session;

    private Audio.AudioList order;
    private int currentPosition;

    private void playNext() {
        //mediaPlayer.pla
        Log.d(TAG, "playNext()");
    }

    private void playPrev() {
        Log.d(TAG, "playPrev()");
    }

    private void initMediaSession() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//        manager = (MediaSessionManager) getSystemService(MEDIA_SESSION_SERVICE);
//        session.setCallback(new MediaSession.Callback() {
//        });

//        private MediaController controller;
//        controller = new MusicController(getApplicationContext());
//        controller.setPrevNextListeners(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Log.d("Player", "onNext()")
//                    }
//                },
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Log.d("Player", "onPrev()")
//                    }
//                });
//        controller.setMediaPlayer(new MyMediaControl(mediaPlayer));
//        controller.setEnabled(true);

    }

    private void onPlay() {
        try {
            Audio audio = order.get(currentPosition);
            String url = DataManager.getAudioUrl(audio);
            if (mediaPlayer == null) {
                initMediaSession();
            }
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.reset();
            mediaPlayer.setDataSource(url);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                @Override
                public void onBufferingUpdate(MediaPlayer mp, int percent) {
                    Log.d(TAG, "onBufferingUpdate: " + percent);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction().equals(ACTION_PLAY)) {
            order = new Audio.AudioList((ArrayList<Audio>) intent.getSerializableExtra(ORDER_KEY));
            currentPosition = intent.getIntExtra(POSITION_TO_PLAY_KEY, 0);
            onPlay();
        }
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
