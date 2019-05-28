package com.yuan.netlibrary;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetWorkUtils {
    /**
     * 判读网络是否可用
     *
     * @return
     */
    @SuppressLint("MissingPermission")
    public static boolean isNetworkAvailable() {
        ConnectivityManager connMgr = (ConnectivityManager) NetWorkManager.getDefault().getApplication()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connMgr == null) {
            return false;
        }

        NetworkInfo[] info = connMgr.getAllNetworkInfo();
        if (info != null) {
            for (NetworkInfo anInfo : info) {
                if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取当前的网络类型：
     * <br/> -1:没有网络 <br/> 1:WIFI网络  <br/> 2:wap网络 <br/> 3:net网络
     *
     * @return
     */
    @SuppressLint("MissingPermission")
    public static NetType getNetType() {
        ConnectivityManager connMgr = (ConnectivityManager) NetWorkManager.getDefault().getApplication()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connMgr == null) {
            return NetType.NONE;
        }
        //获取当前激活的网络连接信息
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null) {
            return NetType.NONE;
        }
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_MOBILE) {
            if (networkInfo.getExtraInfo().toLowerCase().equals("cmnet")) {
                return NetType.CMNET;
            } else {
                return NetType.CMWAP;
            }
        } else if (nType == ConnectivityManager.TYPE_WIFI) {
            return NetType.WIFI;
        }

        return NetType.NONE;
    }

    /**
     * 打开网络设置界面
     *
     * @param context
     * @param requestCode
     */
    public static void openSetting(Context context, int requestCode) {
        Intent intent = new Intent();
        ComponentName cm = new ComponentName("com.android,setting", "com.android.setting.WirelessSettings");
        intent.setComponent(cm);
        intent.setAction("android.intent.action.VIEW");
        ((Activity) context).startActivityForResult(intent, requestCode);
    }
}
