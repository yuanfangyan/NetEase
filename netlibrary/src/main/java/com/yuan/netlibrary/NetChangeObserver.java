package com.yuan.netlibrary;

/**
 * 网络监听接口
 */
public interface NetChangeObserver {
    void connect(NetType type);

    //无网络连接
    void onDiscConnect();
}
