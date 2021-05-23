package com.example.musicservice;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity {

    TextView titleInfo;
    SeekBar seekBar;
    TextView musicName;
    TextView musicCur;
    TextView musicLen;

    private MyMusicService myMusicService;
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat msFormat = new SimpleDateFormat("mm:ss");
    boolean isTouchingBar = false;
    boolean wasPlaying = false;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //绑定 service
        Intent intent = new Intent(this, MyMusicService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);

        bindViews();
    }

    /**
     * 绑定视图组件
     */
    private void bindViews() {
        titleInfo = (TextView) findViewById(R.id.tv_1);
        titleInfo.setText("停止播放");

        musicName = (TextView) findViewById(R.id.music_name);
        musicCur = (TextView) findViewById(R.id.music_cur);
        musicLen = (TextView) findViewById(R.id.music_length);

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        //实现改变 seekBar 时的操作
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (isTouchingBar) {
                    myMusicService.controlPlayerSeek(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isTouchingBar = true;
                if (myMusicService.isMyMusicPlaying()) {
                    wasPlaying = true;
                    myMusicService.controlPlayer("pause");
                } else {
                    wasPlaying = false;
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isTouchingBar = false;
                if (wasPlaying) {
                    myMusicService.controlPlayer("play");
                }
            }
        });
    }

    ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyMusicService.MusicBinder musicBinder = (MyMusicService.MusicBinder) service;
            //获取MyMusicService对象实例
            myMusicService = musicBinder.getService();
            myMusicService.setOnProgressListener((progress, duration) -> {
                if (duration != 0) {
                    seekBar.setMax(duration);
                    seekBar.setProgress(progress);
                } else {
                    seekBar.setMax(100);
                    seekBar.setProgress(0);
                }
                //post方法通知UI线程更新界面
                musicLen.post(() -> {
                    musicCur.setText(msFormat.format(progress));
                    musicLen.setText(msFormat.format(duration));
                });
            });
        }
    };


    @SuppressLint("SetTextI18n")
    public void play_onclick(View view) {
        myMusicService.controlPlayer("play");
        titleInfo.setText("正在播放");
        musicName.setText("Never Gonna Give You Up - Rick Astley");

    }

    @SuppressLint("SetTextI18n")
    public void pause_onclick(View view) {
        myMusicService.controlPlayer("pause");
        titleInfo.setText("暂停播放");
    }

    @SuppressLint("SetTextI18n")
    public void stop_onclick(View view) {
        myMusicService.controlPlayer("stop");
        titleInfo.setText("停止播放");
        musicName.setText("");
    }

    public void exit_onclick(View view) {
        stop_onclick(view);
        onDestroy();
        finish();
    }

    @Override
    protected void onDestroy() {
        unbindService(conn);
        super.onDestroy();
    }
}