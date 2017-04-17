package com.jkwar.client;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.jkwar.serviceExample.IParticipateCallback;
import com.jkwar.serviceExample.IRemoteService;
import com.jkwar.serviceExample.Person;

import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = "RemoteService";
    private IRemoteService iRemoteService;
    private IBinder mToken = new Binder();
    private boolean mIsBound = false;
    private boolean mIsJoin = false;
    private boolean mIsRegistered = false;
    //随机数
    private Random mRand = new Random();
    private Button join, mRegisterBtn;

    private ListView mList;
    private ArrayAdapter<String> mAdapter;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            iRemoteService = IRemoteService.Stub.asInterface(service);
            Log.d(TAG, "onServiceConnected: ");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected: ");
            iRemoteService = null;
        }
    };

    //点击回调事件
    private IParticipateCallback mParticipateCallback = new IParticipateCallback.Stub() {
        @Override
        public void onParticipate(String name, boolean joinOrLeave) throws RemoteException {
            if (joinOrLeave) {
                mAdapter.add(name);
            } else {
                mAdapter.remove(name);
            }
        }
    };

    //将隐式启动转换为显示启动
    public static Intent getExplicitIntent(Context context, Intent implicitIntent) {
        // Retrieve all services that can match the given intent
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);
        // Make sure only one match was found
        if (resolveInfo == null || resolveInfo.size() != 1) {
            return null;
        }
        // Get component info and create ComponentName
        ResolveInfo serviceInfo = resolveInfo.get(0);
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        ComponentName component = new ComponentName(packageName, className);
        // Create a new intent. Use the old one for extras and such reuse
        Intent explicitIntent = new Intent(implicitIntent);
        // Set the component to be explicit
        explicitIntent.setComponent(component);
        return explicitIntent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.bind_btn).setOnClickListener(this);
        findViewById(R.id.unbind_btn).setOnClickListener(this);
        findViewById(R.id.call_btn).setOnClickListener(this);
        findViewById(R.id.get_participators).setOnClickListener(this);

        join = (Button) findViewById(R.id.join);
        join.setOnClickListener(this);

        mRegisterBtn = (Button) findViewById(R.id.register_callback);
        mRegisterBtn.setOnClickListener(this);

        mList = (ListView) findViewById(R.id.list);
        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        mList.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bind_btn:
                Toast.makeText(this, "bind", Toast.LENGTH_SHORT).show();
                //第一种调用方式
                Intent intent = new Intent(IRemoteService.class.getName());
                intent.setClassName("com.jkwar.serviceExample", "com.jkwar.serviceExample.RemoteService");
                bindService(intent, connection, BIND_AUTO_CREATE);
                mIsBound = true;
                break;
            case R.id.unbind_btn:
                Toast.makeText(this, "unbind", Toast.LENGTH_SHORT).show();
                if (mIsBound) {
                    unbindService(connection);
                    mIsBound = false;
                }
                break;
            case R.id.call_btn:
                call();
                break;
            case R.id.join:
                toggleJoin();
                break;
            case R.id.get_participators:
                updateParticipators();
                break;
            case R.id.register_callback:
                toggleRegisterCallback();
                break;
            default:
                break;
        }
    }

    private void toggleRegisterCallback() {
        if (!isServiceReady()) {
            return;
        }
        try {
            if (mIsRegistered) {
                iRemoteService.unregisterParticipateCallback(mParticipateCallback);
                mRegisterBtn.setText(R.string.register);
                mIsRegistered = false;
            } else {
                iRemoteService.registerParticipateCallback(mParticipateCallback);
                mRegisterBtn.setText(R.string.unregister);
                mIsRegistered = true;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    private void updateParticipators() {
        if (!isServiceReady()) {
            return;
        }
        try {
            List<String> participators = iRemoteService.getParticipators();
            mAdapter.clear();
            mAdapter.addAll(participators);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    private void toggleJoin() {
        if (!isServiceReady()) {
            return;
        }
        try {
            if (!mIsJoin) {
                String name = "client:" + mRand.nextInt(10);
                iRemoteService.join(mToken, name);
                join.setText(R.string.leave);
                mIsJoin = true;
            } else {
                iRemoteService.leave(mToken);
                join.setText(R.string.join);
                mIsJoin = false;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void call() {
        if (isServiceReady()) {
            try {
                int result = iRemoteService.add(3, 3);
                String str = iRemoteService.toUpperCase("Hello Wrod");
                iRemoteService.save(new Person(1, "jkwar"));
                Toast.makeText(this, "result call return  " + result, Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "str call return  " + str, Toast.LENGTH_SHORT).show();
            } catch (RemoteException e) {
                Toast.makeText(this, "通信异常", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    private boolean isServiceReady() {
        if (iRemoteService != null) {
            return true;
        } else {
            Toast.makeText(this, "service 没有启动", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
