package com.example.roman.safemonitor.util;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StreamGobbler extends Thread {
    InputStream is;
    String type;
    Context mContext;

    public StreamGobbler(InputStream is, String type, Context context) {
        this.is = is;
        this.type = type;
        mContext = context;
    }

    public void run() {
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);
            String line=null;
            while ( (line = br.readLine()) != null) {
                System.out.println(type + ">" + line);
                if(type.equals("Socket")){
                    String data[] = line.split(", ");
                    if (data.length>0){
                        int pid = Integer.parseInt(data[0].split("=")[1]);//进程id
                        String serviceName = data[1].split("=")[1];//服务名称
                        String action=null;
                        if(serviceName.equals("android.content.IContentService")){//读取敏感数据库
                            String uri = data[2].split("=")[1];
                            action = MonitorList.findDataAction(uri);
                        }else {//敏感API调用
                            int transaction_id = Integer.parseInt(data[2].split("=")[1]);//rpc调用号
                            action = MonitorList.findAction(serviceName, transaction_id);
                        }

                        if(action!=null){
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date date = new Date(System.currentTimeMillis());
                            String time = simpleDateFormat.format(date);
                            String appName = ServiceUtils.getAppName(mContext, pid);
                            if (appName==null||appName.equals(mContext.getPackageName()))
                                continue;
                            String temp = time + ", " + pid + ", " + appName + ", " + action +"\n";
                            FileHelper.save(mContext, MonitorList.DATA_FILE_PATH, temp);//将数据保存到本地
                        }
                    }
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally{
            try {
                br.close();
                isr.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}