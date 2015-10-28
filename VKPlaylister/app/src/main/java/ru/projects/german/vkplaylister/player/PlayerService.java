package ru.projects.german.vkplaylister.player;

import android.app.Notification;
import android.app.PendingIntent;
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
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.io.IOException;

import ru.projects.german.vkplaylister.R;
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

    private NotificationCompat.Builder notificationBuilder;

    private Audio currentAudio;
    private MediaPlayer mediaPlayer;
    private Messenger playerHelper;

    private NotificationCompat.Action generateAction(int icon, String intentAction) {
        Intent intent = new Intent(getApplicationContext(), PlayerService.class);
        intent.setAction(intentAction);
        int requestCode;
        if (PlayerHelper.ACTION_PREV.equals(intentAction)) {
            requestCode = 1001;
        } else if (PlayerHelper.ACTION_PLAY_PAUSE.equals(intentAction)) {
            requestCode = 1002;
        } else if (PlayerHelper.ACTION_NEXT.equals(intentAction)) {
            requestCode = 1003;
        } else {
            requestCode = 0;
        }
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return new NotificationCompat.Action.Builder(icon, "", pendingIntent)
                .build();
    }

    private Notification buildNotification() {
        if (notificationBuilder == null) {
            notificationBuilder = new NotificationCompat.Builder(this);
            NotificationCompat.MediaStyle style = new NotificationCompat.MediaStyle();
            style.setShowActionsInCompactView(0, 1, 2);
            notificationBuilder.setStyle(style)
                    .setSmallIcon(R.drawable.ic_play_circle_filled_white_48dp)
                    .setShowWhen(false)
                    .addAction(generateAction(android.R.drawable.ic_media_previous, PlayerHelper.ACTION_PREV))
                    .addAction(generateAction(android.R.drawable.ic_media_play, PlayerHelper.ACTION_PLAY_PAUSE))
                    .addAction(generateAction(android.R.drawable.ic_media_next, PlayerHelper.ACTION_NEXT))
                    .setContentTitle("")
                    .setContentText("");
        }

        if (!notificationBuilder.mContentTitle.equals(currentAudio.getTitle())) {
            notificationBuilder.setContentTitle(currentAudio.getTitle());
        }
        if (!notificationBuilder.mContentText.equals(currentAudio.getArtist())) {
            notificationBuilder.setContentText(currentAudio.getArtist());
        }
        notificationBuilder.mActions.set(1, generateAction(mediaPlayer.isPlaying() ? android.R.drawable.ic_media_pause
                : android.R.drawable.ic_media_play, PlayerHelper.ACTION_PLAY_PAUSE));

        return notificationBuilder.build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null && mediaPlayer != null) {
            Log.d(TAG, "onStartCommand(), action=" + intent.getAction());
            if (intent.getAction().equals(PlayerHelper.ACTION_NEXT)) {
                playNext();
            } else if (intent.getAction().equals(PlayerHelper.ACTION_PREV)) {
                playPrev();
            } else if (intent.getAction().equals(PlayerHelper.ACTION_PLAY_PAUSE)) {
                if (mediaPlayer.isPlaying()) {
                    pause();
                } else {
                    resume();
                }
                try {
                    playerHelper.send(Message.obtain(null, PlayerHelper.PLAY_PAUSE, mediaPlayer.isPlaying()));
                } catch (RemoteException e) {
                    Log.e(TAG, "Unable to send PLAY_PAUSE message: " + e.getMessage());
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private boolean waitingAudio = true;

    private void initMediaSession() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
                startForeground(1, buildNotification());
                waitingAudio = false;
            }
        });
        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                if (percent != 100) {
                    Log.d(TAG, "onBufferingUpdate(): " + percent);
                }
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d(TAG, "onCompletion()");
                if (!waitingAudio) {
                    mp.reset();
                    playNext();
                }
                waitingAudio = true;
            }
        });
    }

    private Runnable playRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "Play " + currentAudio.toString());

            try {
                if (mediaPlayer == null) {
                    initMediaSession();
                }
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.reset();
            String url = DataManager.getAudioUrl(currentAudio);
//                String url = currentAudio.getUrl();

                startForeground(1, buildNotification());
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setDataSource(url);
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    private void play(Audio audio) {
        currentAudio = audio;
        handler.removeCallbacks(playRunnable);
        handler.postDelayed(playRunnable, 300);
    }

    private void playNext() {
        try {
            waitingAudio = true;
            playerHelper.send(Message.obtain(null, PlayerHelper.PLAY_NEXT));
        } catch (RemoteException e) {
            waitingAudio = false;
            Log.e(TAG, "Unable to send PLAY_NEXT message: " + e.getMessage());
        }
    }

    private void playPrev() {
        try {
            waitingAudio = true;
            playerHelper.send(Message.obtain(null, PlayerHelper.PLAY_PREV));
        } catch (RemoteException e) {
            waitingAudio = false;
            Log.e(TAG, "Unable to send PLAY_PREV message: " + e.getMessage());
        }
    }

    private void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            startForeground(1, buildNotification());
        }
    }

    private void resume() {
        if (mediaPlayer != null) {
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
                if (!mediaPlayer.isPlaying()) {
                    play(currentAudio);
                } else {
                    startForeground(1, buildNotification());
                }
            }
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
