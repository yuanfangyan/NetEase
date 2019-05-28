package com.yuan.netlibrary;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NetWorkReceiver extends BroadcastReceiver {
    private NetType netType;
    private NetChangeObserver listener;

    public NetWorkReceiver() {
        netType = NetType.NONE;
    }

    public void setListener(NetChangeObserver listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null) {
            Log.e(Constants.LOG_TAG, "异常.....");
            return;
        }

        if (intent.getAction().equalsIgnoreCase(Constants.ANDROID_NET_CHANGE_ACTION)) {
            Log.e(Constants.LOG_TAG, "网络发生改变");
            netType = NetWorkUtils.getNetType();//当前联网的具体网络类型
            if (NetWorkUtils.isNetworkAvailable()) {//所有能联网的方式循环判断
                Log.e(Constants.LOG_TAG, "网络连接成功");
                if (listener != null) {
                    listener.connect(netType);
                }
            } else {
                Log.e(Constants.LOG_TAG, "网络连接失败");
                if (listener != null) {
                    listener.onDiscConnect();
                }
            }
        }
    }

    
}
