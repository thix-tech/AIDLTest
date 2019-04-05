package com.sqchen.aidltest;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    private final static String PKG_NAME = "com.sqchen.aidltest";

    private Button btnBind;
    private Button btnAddData;
    private Button btnGetData;
    private Button btnUnbind;

    private IStudentService mStudentService;

    private ITaskCallback mCallback;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mStudentService = IStudentService.Stub.asInterface(service);
            if (mStudentService == null) {
                Log.i(TAG, "mStudentService == null");
                return;
            }
            try {
                //设置死亡代理
                mStudentService.asBinder().linkToDeath(mDeathRecipient, 0);
                if (mCallback != null) {
                    Log.i(TAG, "mCallback != null");
                    mStudentService.register(mCallback);
                } else {
                    Log.i(TAG, "mCallback == null");
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            //也可以在这里重新绑定服务
        }
    };

    private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            if (mStudentService == null) {
                return;
            }
            //解除死亡代理
            mStudentService.asBinder().unlinkToDeath(mDeathRecipient, 0);
            mStudentService = null;
            //重新绑定服务
            bindStudentService();
            Log.i(TAG, "binderDied, bindService again");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initView() {
        btnBind = findViewById(R.id.btn_bind);
        btnAddData = findViewById(R.id.btn_add_data);
        btnGetData = findViewById(R.id.btn_get_data);
        btnUnbind = findViewById(R.id.btn_unbind);
        initListener();
    }

    private void initListener() {
        btnBind.setOnClickListener(this);
        btnAddData.setOnClickListener(this);
        btnGetData.setOnClickListener(this);
        btnUnbind.setOnClickListener(this);
    }

    private void initData() {
        mCallback = new ITaskCallback.Stub() {
            @Override
            public void onSuccess(String result) throws RemoteException {
                Log.i(TAG, "result = " + result);
            }

            @Override
            public void onFailed(String errorMsg) throws RemoteException {
                Log.e(TAG, "errorMsg = " + errorMsg);
            }
        };
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_bind:
                bindStudentService();
                break;
            case R.id.btn_add_data:
                addData();
                break;
            case R.id.btn_get_data:
                getData();
                break;
            case R.id.btn_unbind:
                unbindStudentService();
                break;
            default:
                break;
        }
    }

    private void bindStudentService() {
        Intent intent = new Intent(this, StudentService.class);
        intent.setPackage(PKG_NAME);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private void addData() {
        if (mStudentService == null) {
            Log.i(TAG, "mStudentService = null");
            return;
        }
        try {
            mStudentService.addStudent(new Student(1, "陈贤靖"));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void getData() {
        if (mStudentService == null) {
            Log.i(TAG, "mStudentService = null");
            return;
        }
        try {
            List<Student> studentList = mStudentService.getStudentList();
            Log.i(TAG, "studentList = " + studentList);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void unbindStudentService() {
        unbindService(mConnection);
        mStudentService = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindStudentService();
    }
}
