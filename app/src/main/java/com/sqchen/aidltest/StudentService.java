package com.sqchen.aidltest;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class StudentService extends Service {

    private static final String TAG = "StudentService";

    private CopyOnWriteArrayList<Student> mStuList;

    private static RemoteCallbackList<ITaskCallback> sCallbackList;

    private Binder mBinder = new IStudentService.Stub() {

        @Override
        public void register(ITaskCallback callback) throws RemoteException {
            //todo callback传递到这里时，为空 ？
            if (callback == null) {
                Log.i(TAG, "callback == null");
                return;
            }
            sCallbackList.register(callback);
        }

        @Override
        public void unregister(ITaskCallback callback) throws RemoteException {
            if (callback == null) {
                return;
            }
            sCallbackList.unregister(callback);
        }

        @Override
        public List<Student> getStudentList() throws RemoteException {
            return mStuList;
        }

        @Override
        public void addStudent(Student student) throws RemoteException {
            if (mStuList == null) {
                dispatchResult(false, "add student failed, mStuList = null");
            } else {
                mStuList.add(student);
                dispatchResult(true, "add student successfully");
            }
        }

        @Override
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            //包名验证
            String[] packages = getPackageManager().getPackagesForUid(getCallingUid());
            String pkgName = null;
            if (packages != null && packages.length > 0) {
                pkgName = packages[0];
            }
            if (TextUtils.isEmpty(pkgName) || !pkgName.startsWith("com.sqchen")) {
                Log.i(TAG, "invalid pkgName : " + pkgName);
                return false;
            }
            return super.onTransact(code, data, reply, flags);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
        init();
    }

    private void init() {
        mStuList = new CopyOnWriteArrayList<>();
        sCallbackList = new RemoteCallbackList<>();
    }

    /**
     * 分发结果
     * @param result
     * @param msg
     */
    private void dispatchResult(boolean result, String msg) {
        int length = sCallbackList.beginBroadcast();
        for (int i = 0; i < length; i++) {
            ITaskCallback callback = sCallbackList.getBroadcastItem(i);
            try {
                if (result) {
                    callback.onSuccess(msg);
                } else {
                    callback.onFailed(msg);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        //在调用beginBroadcast之后，必须调用该方法
        sCallbackList.finishBroadcast();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
