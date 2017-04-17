package com.jkwar.serviceExample;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Process;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private MyService.DownloadBinder downloadBinder;

    private ServiceConnection connection = new ServiceConnection() {
        //绑定service
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downloadBinder = (MyService.DownloadBinder) service;
            downloadBinder.startDownload();
            downloadBinder.getProgress();
        }

        //解除绑定
        @Override
        public void onServiceDisconnected(ComponentName name) {
            downloadBinder = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //设置点击事件
        findViewById(R.id.start_Service).setOnClickListener(this);
        findViewById(R.id.stop_Service).setOnClickListener(this);
        findViewById(R.id.bind_Service).setOnClickListener(this);
        findViewById(R.id.unbind_Service).setOnClickListener(this);
        findViewById(R.id.Proscenium_Service).setOnClickListener(this);
        findViewById(R.id.Intent_Service).setOnClickListener(this);
        findViewById(R.id.dounload_Service).setOnClickListener(this);
        // 打印当前主线程的id
        Log.d("MyService", "MainActivity Thread id is " + Thread.currentThread().getId());
        //打印当前进程
        Log.d("TAG", "MainActivity process id is " + Process.myPid());
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, MyService.class);
        switch (v.getId()) {
            case R.id.start_Service:
                startService(intent);
                break;
            case R.id.stop_Service:
                stopService(intent);
                break;
            case R.id.bind_Service:
                bindService(intent, connection, BIND_AUTO_CREATE);
                break;
            case R.id.unbind_Service:
                unbindService(connection);
                break;
            case R.id.Proscenium_Service:
                startService(new Intent(this, ProsceniumService.class));
                break;
            case R.id.Intent_Service:
                // 打印当前主线程的id
                Log.d("MyIntentService", "MainActivity Thread id is " + Thread.currentThread().getId());
                startService(new Intent(this, MyIntentService.class));
                break;
            case R.id.dounload_Service:
                startActivity(new Intent(this,DownloadActivity.class));
                break;
        }
    }
}
