package com.yuan.netlibrary;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NetWorkReceiver extends BroadcastReceiver {
    private NetType netType;
    private Map<Object, List<NetWorkMethod>> cacheMap;


    public NetWorkReceiver() {
        netType = NetType.NONE;
        cacheMap = new HashMap<>();
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
            } else {
                Log.e(Constants.LOG_TAG, "网络连接失败");
            }
            post(netType);
        }
    }

    private void post(NetType netType) {
        Set<Object> keySet = cacheMap.keySet();
        for (Object getter : keySet) {
            //所有注解的方法
            List<NetWorkMethod> list = cacheMap.get(getter);
            if (list != null) {
                //循环每个方法
                for (NetWorkMethod netWorkMethod : list) {
                    //两个参数比较
                    if (netType.getClass().isAssignableFrom(netWorkMethod.getClazz())) {
                        switch (netWorkMethod.getType()) {
                            case AUTO:
                                invoke(netWorkMethod, getter, netType);
                                break;
                            case WIFI:
                                if (netType == NetType.WIFI || netType == NetType.NONE) {
                                    invoke(netWorkMethod, getter, netType);
                                }
                                break;
                            case CMNET:
                                if (netType == NetType.CMNET || netType == NetType.NONE) {
                                    invoke(netWorkMethod, getter, netType);
                                }
                                break;
                            case CMWAP:
                                if (netType == NetType.CMWAP || netType == NetType.NONE) {
                                    invoke(netWorkMethod, getter, netType);
                                }
                            case ETHERNET:
                                if (netType == NetType.ETHERNET || netType == NetType.NONE) {
                                    invoke(netWorkMethod, getter, netType);
                                }
                                break;
                        }

                    }
                }
            }
        }
//        Iterator<Object> iterator = keySet.iterator();
//        while (iterator.hasNext()){
//            iterator.next();
//
//        }

    }

    private void invoke(NetWorkMethod netWorkMethod, Object object, NetType netType) {
        try {
            //在“object”对象中执行方法，且参数为netType
            netWorkMethod.getMethod().invoke(object, netType);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }


    public void register(Object register) {
        List<NetWorkMethod> list = cacheMap.get(register);
        if (list == null) {
            list = findNetWorkMethod(register);
            cacheMap.put(register, list);
        }
    }

    public void unregister(Object unregister) {
        if (!cacheMap.isEmpty()) cacheMap.remove(unregister);

//          应用退出
//        if (!cacheMap.isEmpty()){
//            cacheMap.clear();
//        }
//        NetWorkManager.getDefault().getApplication().unregisterReceiver(this);
//        cacheMap = null;
    }

    private List<NetWorkMethod> findNetWorkMethod(Object object) {
        List<NetWorkMethod> list = new ArrayList<>();
        Class<?> clazz = object.getClass();
        Method[] methods = clazz.getMethods();

        while (clazz != null) {
            //找父类的时候判读是否是系统级别的父类
            String name = clazz.getName();
            if (name.startsWith("java.") || name.startsWith("javax.") || name.startsWith("android.")) {
                break;
            }

            for (Method method : methods) {
                //找到 Subscrible注解的方法
                NetWork annotation = method.getAnnotation(NetWork.class);
                if (annotation == null) {
                    continue;
                }
                //判断带有Subscrible注解方发中的参数类型
                Class<?>[] types = method.getParameterTypes();
                if (types.length != 1) {
                    throw new IllegalStateException("参数必须只为一个");
                }
                NetType netType = annotation.netType();
                NetWorkMethod subscribleMethod = new NetWorkMethod(method, netType, types[0]);
                list.add(subscribleMethod);
            }
            clazz = clazz.getSuperclass();
        }

        return list;
    }


}
