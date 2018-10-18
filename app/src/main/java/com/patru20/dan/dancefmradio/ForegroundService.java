package com.patru20.dan.dancefmradio;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.util.Objects;

public class ForegroundService extends Service {

    MediaPlayer mediaPlayer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Objects.requireNonNull(intent.getAction()).equals(Constants.ACTION.PLAY_ACTION)){

            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setDataSource("http://edge126.rdsnet.ro:84/profm/dancefm.mp3");
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();
                }
            });

            Intent stopIntent = new Intent(this, ForegroundService.class);
            stopIntent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
            PendingIntent pendingStopIntent = PendingIntent.getService(this, 0,
                    stopIntent, 0);

            Notification notification = new Notification.Builder(this)
                    .setContentTitle("DanceFm Player")
                    .setTicker("DanceFm Player")
                    .setContentText("DanceFm")
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setOngoing(true)
                    .addAction(R.drawable.ic_action_stop,"Stop",pendingStopIntent)
                    .build();

            startForeground(42, notification);
        }
        else if (intent.getAction().equals(
                Constants.ACTION.STOPFOREGROUND_ACTION)) {
            if(mediaPlayer != null && mediaPlayer.isPlaying()){
                mediaPlayer.stop();
                mediaPlayer.release();
                stopForeground(true);
                stopSelf();
            }
        }
        return START_STICKY;
    }
}
