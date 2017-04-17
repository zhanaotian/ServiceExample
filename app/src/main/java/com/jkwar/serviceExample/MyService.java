package com.jkwar.serviceExample;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class MyService extends Service {
    private final String TAG = "MyService";

    private DownloadBinder mBinder = new DownloadBinder();

    public MyService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: executed");
        //打印当前线程ID
        Log.d(TAG, "Thread id is " + Thread.currentThread(). getId());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: executed");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: executed");
    }

    class DownloadBinder extends Binder {
        void startDownload() {
            Log.d(TAG, "startDownload: executed");
        }

        int getProgress() {
            Log.d(TAG, "getProgress: executed");
            return 0;
        }
    }
}
