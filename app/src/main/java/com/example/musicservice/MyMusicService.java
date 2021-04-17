package com.example.musicservice;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.widget.SeekBar;

import androidx.annotation.Nullable;

import java.util.Timer;

public class MyMusicService extends Service {
    public MyMusicService() {
    }

    private MediaPlayer mediaPlayer;
    private int musicDuration = 1;
    private int musicProgress = 0;

    /**
     * 更新进度的回调接口
     */
    private OnProgressListener onProgressListener;

    public void setOnProgressListener(OnProgressListener onProgressListener) {
        this.onProgressListener = onProgressListener;
    }

    public int getProgress() {
        if (mediaPlayer != null) {
            musicProgress = mediaPlayer.getCurrentPosition();
        }
        return musicProgress;
    }


    public int getMusicDuration() {
        if (mediaPlayer != null) {
            musicDuration = mediaPlayer.getDuration();
        }
        return musicDuration;
    }

    public void checkProgress() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mediaPlayer != null) {
                    if (onProgressListener != null) {
                        onProgressListener.onProgress(getProgress(), getMusicDuration());
//                        System.out.println("service:" + musicProgress + ", " + musicDuration);
                    }
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void controlPlayer(String action) {
        switch (action) {
            case "play":
                if (mediaPlayer == null) {
                    mediaPlayer = MediaPlayer.create(this, R.raw.rickroll);
                }
                mediaPlayer.start();
                checkProgress();
                System.out.println("Player started.");
                break;
            case "stop":
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
                break;
            case "pause":
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
                break;
            case "getInfo":
                if (mediaPlayer != null) {
                    mediaPlayer.getDuration();
                }
                break;
        }
    }

//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//
//        //获取意图传递的信息
//        String action = intent.getStringExtra("action");
//
//        switch (action) {
//            case "play":
//                if (mediaPlayer == null) {
//                    mediaPlayer = MediaPlayer.create(this, R.raw.newyear);
//                }
//                mediaPlayer.start();
//                System.out.println("Player started.");
//                break;
//            case "stop":
//                if (mediaPlayer != null) {
//                    mediaPlayer.stop();
//                    mediaPlayer.reset();
//                    mediaPlayer.release();
//                    mediaPlayer = null;
//                }
//                break;
//            case "pause":
//                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
//                    mediaPlayer.pause();
//                }
//                break;
////            case "getInfo":
////                if (mediaPlayer != null) {
////                    mediaPlayer.getDuration();
////                }
////                break;
//        }
//        return super.onStartCommand(intent, flags, startId);
//    }

    /**
     * 返回一个Binder对象
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        System.out.println("Bind.");
        return new MusicBinder();
    }

    public class MusicBinder extends Binder {
        /**
         * @return 获取当前对象实例
         */
        public MyMusicService getService() {
            return MyMusicService.this;
        }
    }
}