package com.example.roman.safemonitor.Monitor;

import android.app.ActivityManager;
import android.app.IntentService;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.util.Log;

import com.example.roman.safemonitor.Activity.CPUActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class CpuDataCollectService extends IntentService {

    private static final String CPU_FILE_PATH = "/proc/stat";
    private Timer timer;
    private TimerTask task;

    public CpuDataCollectService() {
        super("CpuDataCollectService");
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                handleCPUMonitor();
            }
        };
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            timer.schedule(task, 0,2000);
        }
    }

    /**
     * 采集cpu数据
     * parameters.
     */
    private void handleCPUMonitor() {
        // TODO: Handle action Foo
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        float totalCPUconsume[] = new float[2];
        float totalIdle[] = new float[2];

        //两次读取cpu信息
        for (int i=0;i<2;i++){
            try {
                //打开/proc/stat文件
                fileReader = new FileReader(new File(CPU_FILE_PATH));
                bufferedReader = new BufferedReader(fileReader,1000);
                String str;
                if((str = bufferedReader.readLine())!=null){
                    if(str.toLowerCase().startsWith("cpu ")){
                        String cpuInfos[] = str.split(" ");
                        //计算从系统启动到当前时刻的总CPU使用时间
                        totalCPUconsume[i] = Long.parseLong(cpuInfos[2])
                                + Long.parseLong(cpuInfos[3]) + Long.parseLong(cpuInfos[4])
                                + Long.parseLong(cpuInfos[5]) + Long.parseLong(cpuInfos[6])
                                + Long.parseLong(cpuInfos[7]) + Long.parseLong(cpuInfos[8]);
                        //从系统启动到当前时刻的CPU空闲时间
                        totalIdle[i] = Long.parseLong(cpuInfos[5]);
                    }
                }
                if(i==0){
                    try {
                        //当前线程暂停50毫秒
                        Thread.sleep(50);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        float rate = -1;
        if(totalCPUconsume[0]>0&&totalCPUconsume[1]>0&&totalIdle[0]!=totalIdle[1]){
            rate = 100*((totalCPUconsume[1]-totalIdle[1])-(totalCPUconsume[0]-totalIdle[0]))/(totalCPUconsume[1]-totalCPUconsume[0]);
        }

        Log.d("CpuMonitor", "total1="+String.valueOf(totalCPUconsume[0])+", total2="+String.valueOf(totalCPUconsume[1])+
                ", idle1="+String.valueOf(totalIdle[0])+", idle2="+String.valueOf(totalIdle[1])+", rate="+String.valueOf(rate));
        Intent intent = new Intent();
        intent.setAction(CPUActivity.ACTION_DATAUPDATE);
        intent.putExtra("cpurate",rate);
        intent.putExtra("memory",getMemory());//获取内存占用率

        sendBroadcast(intent);//发送广播通知更新UI
    }

    private float getMemory(){
        float[] res = new float[2];
        float rate;
        ActivityManager activityManager = (ActivityManager) getApplicationContext().
                getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        res[0] = memoryInfo.availMem;
        res[1] = memoryInfo.totalMem;
        rate = ((res[1]-res[0])/res[1])*100;
        Log.d("CpuMonitor", "availMem="+String.valueOf(res[0])+", totalMem="+String.valueOf(res[1])
                +", rate="+String.valueOf(rate));
        return rate;
    }

}
