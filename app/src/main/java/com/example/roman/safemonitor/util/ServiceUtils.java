package com.example.roman.safemonitor.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import static android.content.Context.ACTIVITY_SERVICE;

public class ServiceUtils {

    public static boolean isServiceRunning(Context context, String serviceName){
        if(serviceName==null||serviceName.equals(""))
            return false;
        ActivityManager manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>)manager.getRunningServices(30);
        for(int i=0;i<runningService.size();i++){
            if(runningService.get(i).service.getClassName().equals(serviceName))
                return true;
        }
        return false;
    }

    public static String getAppName(Context context, int pID)
    {
        FileReader fileReader;
        BufferedReader bufferedReader;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            try {
                fileReader = new FileReader(new File("/proc/"+pID+"/cmdline"));
                bufferedReader = new BufferedReader(fileReader);
                String line;
                if ((line = bufferedReader.readLine())!=null){
                    String packagename = line.trim().split(":")[0];
                    if (packagename.contains("com.baidu.map"))
                        return "百度地图";
                    if(packagename.startsWith("android")||packagename.startsWith("huawei"))
                        return null;
                    PackageManager pm = context.getPackageManager();
                    ApplicationInfo appinfo = pm.getApplicationInfo(packagename,PackageManager.GET_META_DATA);
                    return pm.getApplicationLabel(appinfo).toString();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }



}
