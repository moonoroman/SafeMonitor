package com.example.roman.safemonitor.util;

import android.os.IBinder;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MonitorList {

    private static HashMap<String, List> serviceList;
    public static final String DATA_FILE_PATH = "data.txt";

    public MonitorList(){
        serviceList = new HashMap<>();

        ArrayList<String> list = new ArrayList<>();
        list.add("requestLocationUpdates");//获取位置信息

        ArrayList<String> list1 = new ArrayList<>();
        list1.add("call");//拨打电话
        list1.add("setDataEnabled");//打开移动网络
        list1.add("getDeviceId");//获取IMEI

        ArrayList<String> list2 = new ArrayList<>();
        list2.add("sendTextForSubscriber");//发送短信

        ArrayList<String> list3 = new ArrayList<>();
        list3.add("getDeviceId");//获取IMEI
        list3.add("getLine1Number");//获取本机号码

        ArrayList<String> list4 = new ArrayList<>();

        ArrayList<String> list5 = new ArrayList<>();
        list5.add("getAdnRecordsInEf");//获取SIM卡联系人

        serviceList.put("android.location.ILocationManager",list);
        serviceList.put("com.android.internal.telephony.ITelephony",list1);
        serviceList.put("com.android.internal.telephony.ISms",list2);
        serviceList.put("com.android.internal.telephony.IPhoneSubInfo",list3);
        serviceList.put("android.content.IContentService",list4);
        serviceList.put("com.android.internal.telephony.IIccPhoneBook",list5);//sim卡获取联系人
    }

    public static int getTransactionId(String servicename, String serviceclass, String apiname) {
        int transactionId = 0;

        try {
            //获取服务
            Class ServiceManager = Class.forName("android.os.ServiceManager");
            Method getService = ServiceManager.getDeclaredMethod("getService", String.class);
            getService.setAccessible(true);
            IBinder binder = (IBinder) getService.invoke(null, servicename);

            //获取代理对象
            Class Stub = Class.forName(serviceclass + "$Stub");
            Method asInterface = Stub.getDeclaredMethod("asInterface", IBinder.class);
            asInterface.setAccessible(true);
            Object binderProxy = asInterface.invoke(null, binder);

            //获取API的transaction_id
            Class outclass = binderProxy.getClass().getEnclosingClass();
            Field idField = outclass.getDeclaredField(apiname);
            idField.setAccessible(true);
            transactionId = (int) idField.get(binderProxy);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return transactionId;
    }

    //遍历监控列表，匹配是否为敏感行为
    public static String findAction(String service, int apiId){
        if (serviceList!=null) {
            ArrayList<String> temp = (ArrayList<String>) serviceList.get(service);
            String serviceName = null;
            switch (service){
                case "android.location.ILocationManager":
                    serviceName = "location";
                    for (String s : temp) {
                        int id = getTransactionId(serviceName,  service, "TRANSACTION_" + s);
                        if (apiId == id) {
                            Log.i("Monitor", "find sensative behavior > " + s);
                            return "获取位置信息";
                        }
                    }
                    break;
                case "com.android.internal.telephony.ITelephony":
                    serviceName = "phone";
                    for (String s : temp) {
                        int id = getTransactionId(serviceName,  service, "TRANSACTION_" + s);
                        if (apiId == id) {
                            Log.i("Monitor", "find sensative behavior > " + s);
                            switch (s){
                                case "call":
                                    return "拨打电话";
                                case "setDataEnabled":
                                    return "打开移动数据";
                                case "getDeviceId":
                                    return "获取设备IMEI";
                                default:
                                    return null;
                            }
                        }
                    }
                    break;
                case "com.android.internal.telephony.ISms":
                    serviceName = "isms";
                    for (String s : temp) {
                        int id = getTransactionId(serviceName,  service, "TRANSACTION_" + s);
                        if (apiId == id) {
                            Log.i("Monitor", "find sensative behavior > " + s);
                            return "发送短信";
                        }
                    }
                    break;
                case "com.android.internal.telephony.IPhoneSubInfo":
                    serviceName = "iphonesubinfo";
                    for (String s : temp) {
                        int id = getTransactionId(serviceName,  service, "TRANSACTION_" + s);
                        if (apiId == id) {
                            Log.i("Monitor", "find sensative behavior > " + s);
                            return s;
                        }
                    }
                    break;
                case "com.android.internal.telephony.IIccPhoneBook":
                    serviceName = "simphonebook";
                    for (String s : temp) {
                        int id = getTransactionId(serviceName,  service, "TRANSACTION_" + s);
                        if (apiId == id) {
                            Log.i("Monitor", "find sensative behavior > " + s);
                            return "SIM卡获取联系人";
                        }
                    }
                    break;
            }
        }
        return null;
    }

    public static String findDataAction(String uri){
        String action=null;
        switch (uri){
            case "call_log":
                action = "读取通话记录";
                break;
            case "com.android.contacts":
                action = "读取联系人";
                break;
            case "sms":
                action = "读取短信";
                break;
            case "media/external/images":
                action = "读取媒体库";
                break;
            case "media/internal/images":
                action = "读取媒体库";
                break;
            case "media/external/audio":
                action = "读取媒体库";
                break;
            case "media/external/video":
                action = "读取媒体库";
                break;
        }
        return action;
    }



}
