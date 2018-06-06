package com.example.roman.safemonitor.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.example.roman.safemonitor.R;
import com.example.roman.safemonitor.util.ServiceUtils;
import com.example.roman.safemonitor.view.ChartView;
import com.example.roman.safemonitor.Monitor.CpuDataCollectService;

public class CPUActivity extends TitleActivity {

    public static final String ACTION_DATAUPDATE = "com.safeMonitor.DATAUPDATE";
    public DataUpdateReceiver mDataUpdateReceiver;
    private ChartView mChartView1;
    private ChartView mChartView2;
    private TextView CpuTextView;
    private TextView MemoryTextView;
    private Float lastCpuRate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //动态注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_DATAUPDATE);
        mDataUpdateReceiver = new DataUpdateReceiver();
        registerReceiver(mDataUpdateReceiver, filter);

        if (!ServiceUtils.isServiceRunning(getApplicationContext(), getPackageName() + R.string.cpu_service_name)) {
            //开启cpu监控服务
            Intent CpuService = new Intent(this, CpuDataCollectService.class);
            startService(CpuService);
        }

        initView();
    }

    private void initView() {
        setContentView(R.layout.activity_cpu);
        setTitle("性能监控");
        showBackwordView(R.string.backword, true);
        mChartView1 = findViewById(R.id.cpu_chart);
        mChartView2 = findViewById(R.id.memory_chart);
        CpuTextView = findViewById(R.id.cpu_text);
        MemoryTextView = findViewById(R.id.memory_text);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mDataUpdateReceiver);
    }

    //定义数据刷新的广播接收器
    private class DataUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {//数据刷新时更新UI
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                float cpurate = intent.getExtras().getFloat("cpurate");
                float memory = intent.getExtras().getFloat("memory");

                if (cpurate > 0) {
                    CpuTextView.setText(String.format("%.2f",cpurate).toString() + "%");
                    lastCpuRate = cpurate;
                    mChartView1.addData(cpurate);
                    mChartView1.invalidate();
                } else if (cpurate == 0) {
                    CpuTextView.setText(String.format("%.2f",lastCpuRate).toString() + "%");
                    mChartView1.addData(lastCpuRate);
                    mChartView1.invalidate();
                }

                if(memory>0){
                    MemoryTextView.setText(String.format("%.2f",memory).toString() + "%");
                    mChartView2.addData(memory);
                    mChartView2.invalidate();
                }
            }
        }
    }


}