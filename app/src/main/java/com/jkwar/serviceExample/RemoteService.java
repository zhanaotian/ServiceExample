package com.jkwar.serviceExample;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class RemoteService extends Service {
    private final String TAG = "RemoteService";
    //用户列表
    private List<Client> mClients = new ArrayList<>();
    //回调接口
    private RemoteCallbackList<IParticipateCallback> mCallbacks = new RemoteCallbackList<>();

    private final IRemoteService.Stub mBinder = new IRemoteService.Stub() {
        @Override
        public int add(int a, int b) throws RemoteException {
            Log.d(TAG, "add: executed");
            return a + b;
        }

        @Override
        public String toUpperCase(String str) throws RemoteException {
            Log.d(TAG, "toUpperCase: executed");
            return str.toUpperCase();
        }

        @Override
        public Person save(Person person) throws RemoteException {
            Log.d(TAG, "save: executed");
            if (person == null) {
                return new Person(2, "hello word");
            }
            Log.d(TAG, "Name: " + person.getName());
            Log.d(TAG, "Id: " + person.getId());
            return person;
        }

        @Override
        public void join(IBinder token, String userName) throws RemoteException {
            int index = findClient(token);
            if (index >= 0) {
                Log.d(TAG, "already joined");
                return;
            }
            Client client = new Client(token, userName);
            //注册
            token.linkToDeath(client, 0);
            //添加
            mClients.add(client);
            // 通知client加入
            notifyParticipate(client.mUserNmae, true);
        }

        @Override
        public void leave(IBinder token) throws RemoteException {
            int index = findClient(token);
            if (index < 0) {
                Log.d(TAG, "already left");
                return;
            }
            Client client = mClients.get(index);
            //删除
            mClients.remove(client);
            //取消注册
            token.unlinkToDeath(client, 0);
            // 通知client离开
            notifyParticipate(client.mUserNmae, false);
        }

        @Override
        public List<String> getParticipators() throws RemoteException {
            ArrayList<String> names = new ArrayList<>();
            for (Client client : mClients) {
                names.add(client.mUserNmae);
            }
            return names;
        }

        @Override
        public void registerParticipateCallback(IParticipateCallback cb) throws RemoteException {
            mCallbacks.register(cb);
        }

        @Override
        public void unregisterParticipateCallback(IParticipateCallback cb) throws RemoteException {
            mCallbacks.unregister(cb);
        }
    };

    public RemoteService() {
    }

    private void notifyParticipate(String name, boolean joinOrLeave) {
        final int len = mCallbacks.beginBroadcast();
        for (int i = 0; i < len; i++) {
            try {
                //通知回调
                mCallbacks.getBroadcastItem(i).onParticipate(name, joinOrLeave);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        mCallbacks.finishBroadcast();
    }

    // 通过IBinder查找Client
    private int findClient(IBinder token) {
        for (int i = 0; i < mClients.size(); i++) {
            if (mClients.get(i).mToken == token) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //打印当前进程
        Log.d("TAG", "process id is " + Process.myPid());
        Log.d(TAG, "onCreate: executed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: executed");
        return mBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: executed");
        // 取消掉所有的回调
        mCallbacks.kill();
    }

    //注册IBinder.DeathRecipient回调
    private final class Client implements IBinder.DeathRecipient {
        private final IBinder mToken;
        private final String mUserNmae;

        public Client(IBinder mToken, String mUserNmae) {
            this.mToken = mToken;
            this.mUserNmae = mUserNmae;
        }

        @Override
        public void binderDied() {
            //用户不要了
            int index = mClients.indexOf(this);
            if (index < 0) {
                return;
            }
            Log.d(TAG, "client died: " + mUserNmae);
            mClients.remove(this);
            // 通知client离开
            notifyParticipate(mUserNmae, false);
        }
    }
}
