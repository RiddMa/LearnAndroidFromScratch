package com.example.musicservice;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class MyMusicService extends Service {
    public MyMusicService() {
    }

    private MediaPlayer mediaPlayer;

    /**
     * 更新进度的回调接口
     */
    private OnProgressListener onProgressListener;

    /**
     * 注册回调接口的方法，供外部调用
     * @param onProgressListener
     */
    public void setOnProgressListener(OnProgressListener onProgressListener) {
        this.onProgressListener = onProgressListener;
    }

    public int getMusicProgress() {
        if (mediaPlayer != null) {
            return mediaPlayer.getCurrentPosition();
        } else {
            return 0;
        }
    }

    public int getMusicDuration() {
        if (mediaPlayer != null) {
            return mediaPlayer.getDuration();
        } else {
            return 0;
        }
    }

    /**
     * 新建线程检测音乐播放状态
     */
    public void checkProgress() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (onProgressListener != null) {
                    // 通过接口通知调用方进度
                    onProgressListener.onProgress(getMusicProgress(), getMusicDuration());
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    /**
     * 控制播放器
     *
     * @param action 动作字符串
     */
    public void controlPlayer(String action) {
        switch (action) {
            case "play":
                if (mediaPlayer == null) {
                    // NEVER GONNA GIVE YOU UP
                    mediaPlayer = MediaPlayer.create(this, R.raw.rickroll);
                    // NEVER GONNA GIVE YOU UP // NEVER GONNA GIVE YOU UP // NEVER GONNA GIVE YOU UP
                    mediaPlayer.setLooping(true);
                }
                mediaPlayer.start();
                checkProgress();
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
        }
    }

    /**
     * 控制音乐播放进度
     *
     * @param mSec 进度
     */
    public void controlPlayerSeek(int mSec) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(mSec);
        }
    }

    /**
     * 音乐是否在播放？
     *
     * @return 音乐是否在播放
     */
    public boolean isMyMusicPlaying() {
        if (mediaPlayer != null) {
            return mediaPlayer.isPlaying();
        }
        return false;
    }

    /**
     * @param intent 获取意图
     * @return 返回 IBinder 对象，当前对象实例
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
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