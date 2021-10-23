package com.example.doanmp3.Service;

import static com.example.doanmp3.Application.Notification.CHANNEL_ID;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.doanmp3.Application.BroadcastReceiver;
import com.example.doanmp3.NewModel.Song;
import com.example.doanmp3.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class MusicForegroundService extends Service {

    MediaPlayer mediaPlayer;
    MusicBinder musicBinder;
    int currentSong;
    boolean random;
    Loop loopState;
    ArrayList<Song> songs;
    ArrayList<Bitmap> bitmaps;
    ArrayList<Integer> playedList;
    Stack<Integer> playedStack;
    MediaPlayer.OnCompletionListener onCompletionListener = mp -> {
        ActionNext();
        SendActionToActivity(ACTION_PLAY_COMPLETED);
    };

    final public static int ACTION_PREVIOUS_SONG = 1;
    final public static int ACTION_PLAY_OR_PAUSE = 2;
    final public static int ACTION_NEXT_SONG = 3;
    final public static int ACTION_CLEAR = 4;
    final public static int ACTION_CHANGE_POS = 5;
    final public static int ACTION_START_PLAY = 7;
    final public static int ACTION_PLAY_FAILED = 8;
    final public static int ACTION_PLAY_COMPLETED = 9;
    final public static int ABC = 9;


    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        musicBinder = new MusicBinder();
        playedList = new ArrayList<>();
        playedStack = new Stack<>();
        bitmaps = new ArrayList<>();
        random = false;
        loopState = Loop.DISABLED;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return musicBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        GetSongData(intent);
        return START_STICKY;
    }

    /*
     *   Get data and action from activity and notification
     * */
    private void GetSongData(Intent intent) {
        if (intent == null) return;

        //Get data song from activity
        if (intent.hasExtra("songs")) {
            // delete old song
            if (songs != null) {
                songs.clear();
                bitmaps.clear();
            }
            if (playedList != null) {
                playedList.clear();
                playedStack.clear();
            }
            songs = intent.getParcelableArrayListExtra("songs");
            GetBitmapFromSongs();
        }
        // Get Current Song
        if (intent.hasExtra("currentSong")) {
            currentSong = intent.getIntExtra("currentSong", 0);
            PlaySong();
        }

        // Get Action from Activity
        if (intent.hasExtra("action_from_activity")) {
            int action = intent.getIntExtra("action_from_activity", 0);
            HandleActionControlMusic(action);
        }

        // Get action from notification
        if (intent.hasExtra("action_from_notification")) {
            int action = intent.getIntExtra("action_from_notification", 0);
            HandleActionControlMusic(action);
        }
    }

    /*
     * Get Bitmap using Glide to set large icon for notification
     */
    private void GetBitmapFromSongs() {
        for (Song song : songs) {
            Glide.with(getApplication()).asBitmap().load(song.getThumbnail()).into(new CustomTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    bitmaps.add(resource);
                }

                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {

                }
            });
        }
    }

    /*
     * Set Intent for notification
     */
    @SuppressLint("UnspecifiedImmutableFlag")
    private PendingIntent getPendingIntent(Context context, int action) {
        Intent intent = new Intent(this, BroadcastReceiver.class);
        intent.putExtra("action_from_service_to_notification", action);
        return PendingIntent.getBroadcast(context.getApplicationContext(), action, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /*
     *  Push Notification
     */
    private void MusicControlNotification() {


        MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(this, "tag");
        mediaSessionCompat.setActive(true);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            MediaMetadata mediaMetadata = new MediaMetadata.Builder()
//                    .putLong(MediaMetadata.METADATA_KEY_DURATION, mediaPlayer.getDuration())
//                    .build();
//            mediaSessionCompat.setMetadata(MediaMetadataCompat.fromMediaMetadata(mediaMetadata));
//        }
//
//        mediaSessionCompat.setPlaybackState(new PlaybackStateCompat.Builder()
//                .setActions(PlaybackStateCompat.ACTION_SEEK_TO)
//                .build());
//

        PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder();
        stateBuilder.setActions(
                PlaybackStateCompat.ACTION_PLAY|
                PlaybackStateCompat.ACTION_PAUSE|
                PlaybackStateCompat.ACTION_SEEK_TO|
                PlaybackStateCompat.ACTION_SKIP_TO_NEXT|
                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
        );
        mediaSessionCompat.setMediaButtonReceiver(null);
        mediaSessionCompat.setPlaybackState(stateBuilder.build());
        mediaSessionCompat.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public void onSeekTo(long pos) {
                super.onSeekTo(pos);
                Log.e("EEE", "Hello seek to");
            }
        });

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_song)
                .setLargeIcon(GetBitmapOfCurrentSong())
                .setSubText(songs.get(currentSong).getName())
                .setContentTitle(songs.get(currentSong).getName())
                .setContentText(songs.get(currentSong).getAllSingerNames())
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setSilent(true)
                .addAction(R.drawable.ic_prev, "Previous", getPendingIntent(this, ACTION_PREVIOUS_SONG))
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(1, 3)
                        .setMediaSession(mediaSessionCompat.getSessionToken()))
                .setProgress(10000, mediaPlayer.getCurrentPosition(), true);

        if (mediaPlayer.isPlaying())
            notificationBuilder.addAction(R.drawable.ic_pause, "Play", getPendingIntent(this, ACTION_PLAY_OR_PAUSE));
        else
            notificationBuilder.addAction(R.drawable.icon_play, "Play", getPendingIntent(this, ACTION_PLAY_OR_PAUSE));

        notificationBuilder.addAction(R.drawable.ic_next, "Next", getPendingIntent(this, ACTION_NEXT_SONG))
                .addAction(R.drawable.ic_clear, "Cancel", getPendingIntent(this, ACTION_CLEAR));

        startForeground(1, notificationBuilder.build());
    }


    /*
     *   Get Bitmap from thumbnail of current song
     * */
    private Bitmap GetBitmapOfCurrentSong() {
        if (bitmaps == null || bitmaps.size() <= currentSong) {
            return BitmapFactory.decodeResource(getResources(), R.drawable.music2);
        }
        return bitmaps.get(currentSong);
    }


    /*
     * Handle Action Control Music
     */
    private void
    HandleActionControlMusic(int action) {
        switch (action) {
            case ACTION_PREVIOUS_SONG:
                ActionPrevious();
                break;
            case ACTION_NEXT_SONG:
                ActionNext();
                break;
            case ACTION_PLAY_OR_PAUSE:
                ActionPlayOrPause();
                SendActionToActivity(action);
                break;
            case ACTION_CLEAR:
                ActionClear();
                SendActionToActivity(action);
                break;
            case ACTION_CHANGE_POS:
                PlaySong();
                break;
            case ACTION_START_PLAY:
                SendActionToActivity(action);
                break;
            case ABC:
                Log.e("EEE", "ABC");
                break;
        }
    }


    /*
     *   Handle Action Previous Song
     * */
    public void ActionPrevious() {
        if (playedList != null && playedList.size() > 0) {
            currentSong = playedList.get(playedList.size() - 1);
            playedList.remove(playedList.size() - 1);
        } else {
            currentSong--;
            if (currentSong < 0)
                currentSong = songs.size() - 1;
        }
        HandleActionControlMusic(ACTION_CHANGE_POS);
    }


    /*
     *   Handle Action Next Song
     * */
    public void ActionNext() {
        AddSongPlayed();
        if (random) {
            Random random = new Random();
            currentSong = random.nextInt(songs.size());
            while (playedStack.contains(currentSong))
                currentSong = random.nextInt(songs.size());
        } else {
            currentSong++;
            if (currentSong > songs.size() - 1)
                currentSong = 0;
        }
        HandleActionControlMusic(ACTION_CHANGE_POS);
    }

    /*
     *   Handle action play and pause media played
     * */
    public void ActionPlayOrPause() {
        if (mediaPlayer.isPlaying())
            mediaPlayer.pause();
        else
            mediaPlayer.start();
        MusicControlNotification();
    }

    /*
     *   Handle action clear
     * */
    @SuppressLint("NewApi")
    public void ActionClear() {
        mediaPlayer.seekTo(0);
        mediaPlayer.pause();
        stopForeground(true);
        stopSelf();
    }

    /*
     *   Handle action after action next or action play complete happens
     * */
    private void AddSongPlayed() {
        playedList.add(currentSong);
        playedStack.add(currentSong);
        if (playedStack.size() == songs.size())
            playedStack.clear();
    }


    /*
     * Send action to Activity
     * */
    private void SendActionToActivity(int action) {
        Intent intent = new Intent("action_from_service");
        intent.putExtra("action", action);
        intent.putExtra("currentSong", currentSong);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    /*
     * Play Music
     * */
    private void PlaySong() {

        /*
         *   Reset media player
         *   Set new data source
         *
         */

        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(songs.get(currentSong).getLink());
            mediaPlayer.setOnCompletionListener(null);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
                MusicControlNotification();
                HandleActionControlMusic(ACTION_START_PLAY);    // Send Action Play To Activity
                mp.setOnCompletionListener(onCompletionListener);
            });
        } catch (IOException e) {
            e.printStackTrace();
            SendActionToActivity(ACTION_PLAY_FAILED);
        }
    }

    public int getSongDuration() {
        if (mediaPlayer == null)
            return 0;

        return mediaPlayer.getDuration();
    }

    public int getSongProgress() {
        if (mediaPlayer == null)
            return 0;
        return mediaPlayer.getCurrentPosition();
    }

    public void setSongProgress(int progress) {
        mediaPlayer.seekTo(progress);
    }

    public int isMediaPlaying() {
        if (mediaPlayer.isPlaying()) {
            return R.drawable.ic_pause_circle_outline;
        }
        return R.drawable.ic_play_circle_outline;
    }

    public int ChangeRandomState() {
        random = !random;
        return getRandomState();
    }

    public int getRandomState() {
        if (random) {
            return R.drawable.ic_random_enable;
        }
        return R.drawable.ic_random_disabled;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public int ChangeLoopState() {
        switch (loopState) {
            case DISABLED:
                loopState = Loop.ONE;
                break;
            case ONE:
                loopState = Loop.ALL;
                break;
            default:
                loopState = Loop.DISABLED;
        }
        return getLoopState();
    }

    public int getLoopState() {
        switch (loopState) {
            case DISABLED:
                return R.drawable.ic_loop_disable;
            case ONE:
                return R.drawable.ic_loop_one;
            default:
                return R.drawable.ic_loop_all_enable;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        Log.e("EEE", "onDestroy");
    }

    public class MusicBinder extends Binder {
        public MusicForegroundService getService() {
            return MusicForegroundService.this;
        }
    }

    public enum Loop {
        DISABLED,
        ONE,
        ALL
    }
}
