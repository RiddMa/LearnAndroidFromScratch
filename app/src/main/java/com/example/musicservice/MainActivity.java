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
import android.view.View.OnClickListener;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView tv_1;
    private MyMusicService myMusicService;
    private int mProgress = 0;
    private int mDuration = 1;
    private SeekBar seekBar;
    private boolean isBound = false;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//      绑定 service
        Intent intent = new Intent(this, MyMusicService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);


        seekBar = (SeekBar) findViewById(R.id.seekBar);
        tv_1 = (TextView) findViewById(R.id.tv_1);
        tv_1.setText("播放状态0：停止播放。。。");

//        seekBar.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                myMusicService.getProgress();
//                listenProgress();
//            }
//        });
    }

//    public void listenProgress() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                mProgress = myMusicService.getProgress();
//                seekBar.setProgress((mProgress *100)/ mDuration);
//                System.out.println((mProgress *100)/ mDuration);
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//    }


    ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
            System.out.println("ServiceDisconnected");
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyMusicService.MusicBinder musicBinder = (MyMusicService.MusicBinder) service;
            //返回一个MyMusicService对象
            myMusicService = musicBinder.getService();
            isBound = true;

            myMusicService.setOnProgressListener(new OnProgressListener() {
                @Override
                public void onProgress(int progress,int duration) {
                    seekBar.setProgress((progress*100)/duration);
//                    System.out.println("service:" + progress + ", " + duration);
                }
            });
            System.out.println("ServiceConnected");
        }
    };


    @SuppressLint("SetTextI18n")
    public void play_onclick(View view) {
        myMusicService.controlPlayer("play");
        mDuration = myMusicService.getMusicDuration();
        System.out.println(mDuration);
        tv_1.setText("播放状态1：正在播放。。。");
    }

    @SuppressLint("SetTextI18n")
    public void stop_onclick(View view) {
        Intent intent = new Intent(this, MyMusicService.class);

        intent.putExtra("action", "stop");

        startService(intent);

        tv_1.setText("播放状态0：停止播放。。。");
    }

    @SuppressLint("SetTextI18n")
    public void pause_onclick(View view) {
        Intent intent = new Intent(this, MyMusicService.class);

        intent.putExtra("action", "pause");

        startService(intent);

        tv_1.setText("播放状态2：暂停播放。。。");
    }

    public void exit_onclick(View view) {
        stop_onclick(view);
        finish();
    }


    @Override
    protected void onDestroy() {
        unbindService(conn);
        super.onDestroy();
    }
}