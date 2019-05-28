package com.yuan.netlibrary;

import android.net.Network;

import java.lang.reflect.Method;

public class NetWorkMethod {
    private Method method;
    private NetType type;

    public NetWorkMethod(Method method, NetType type) {
        this.method = method;
        this.type = type;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public NetType getType() {
        return type;
    }

    public void setType(NetType type) {
        this.type = type;
    }
}
