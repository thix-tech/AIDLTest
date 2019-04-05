// ITaskCallback.aidl
package com.sqchen.aidltest;

// Declare any non-default types here with import statements

interface ITaskCallback {

    void onSuccess(String result);

    void onFailed(String errorMsg);
}
