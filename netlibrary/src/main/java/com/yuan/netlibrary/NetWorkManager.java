package com.yuan.netlibrary;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.net.Network;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NetWorkManager {
    private static volatile NetWorkManager instance;
    private NetWorkReceiver netWorkReceiver;
    private Application application;
    private Map<Object, List<NetWorkMethod>> cacheMap;


    public static NetWorkManager getDefault() {
        if (instance == null) {
            synchronized (NetWorkManager.class) {
                if (instance == null) {
                    instance = new NetWorkManager();
                }
            }
        }
        return instance;
    }

    public void setListener(NetChangeObserver listener) {
        netWorkReceiver.setListener(listener);
    }

    private NetWorkManager() {
        netWorkReceiver = new NetWorkReceiver();
    }

    public Application getApplication() {
        return application;
    }

    public void init(Application application) {
        this.application = application;
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ANDROID_NET_CHANGE_ACTION);
        application.registerReceiver(netWorkReceiver, filter);
    }

    public void register(Object object) {
        List<NetWorkMethod> list = cacheMap.get(object);
        if (list == null) {
            list = findNetWorkMethod(object);
            cacheMap.put(object, list);
        }
    }

    private List<NetWorkMethod> findNetWorkMethod(Object object) {
        List<NetWorkMethod> list = new ArrayList<>();
        Class<?> clazz = object.getClass();
        while (clazz == null) {
            String name = clazz.getName();
            //判断是否是系统方法
            if (name.startsWith("java.") || name.startsWith("javax.") || name.startsWith("android.")) {
                break;
            }
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                NetWork work = method.getAnnotation(NetWork.class);
                if (work == null) {
                    continue;
                }
                NetType netType = work.netType();
                NetWorkMethod netWorkMethod = new NetWorkMethod(method, netType);
                list.add(netWorkMethod);
            }
        }
        return list;
    }

    private void invoke(Method method, Object object, Object type) {
        try {
            method.invoke(object, type);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
