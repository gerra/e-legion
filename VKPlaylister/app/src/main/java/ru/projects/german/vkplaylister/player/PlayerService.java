package ru.projects.german.vkplaylister.player;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;

import ru.projects.german.vkplaylister.data.DataManager;
import ru.projects.german.vkplaylister.model.Audio;

/**
 * Created by root on 15.10.15.
 */
public class PlayerService extends Service {
    private static final String TAG = PlayerService.class.getSimpleName();

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case PlayerHelper.REGISTER_HELPER:
                    playerHelper = msg.replyTo;
                    break;
                case PlayerHelper.PLAY_MESSAGE:
                    play((Audio) msg.obj);
                    break;
                case PlayerHelper.PAUSE_MESSAGE:
                    pause();
                    break;
                case PlayerHelper.RESUME_MESSAGE:
                    resume();
                    break;
                default:
                    Log.e(TAG, "Unexpected message: " + msg.toString());
            }
            return true;
        }
    });
    private Messenger messenger = new Messenger(handler);

    private MediaPlayer mediaPlayer;
    private Messenger playerHelper;

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
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                Log.d(TAG, "onBufferingUpdate(): " + percent);
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d(TAG, "onCompletion()");
                mp.reset();
                try {
                    playerHelper.send(Message.obtain(null, PlayerHelper.COMPLETE_MESSAGE));
                } catch (RemoteException e) {
                    Log.e(TAG, "Unable to send complete message: " + e.getMessage());
                }
            }
        });
    }

    private void play(Audio audio) {
        Log.d(TAG, "Play " + audio.toString());
        try {
            if (mediaPlayer == null) {
                initMediaSession();
            }
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.reset();
            String url = DataManager.getAudioUrl(audio);
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    private void resume() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }
}
