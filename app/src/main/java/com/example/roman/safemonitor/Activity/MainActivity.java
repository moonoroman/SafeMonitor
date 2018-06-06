package com.example.roman.safemonitor.Activity;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.roman.safemonitor.Monitor.BinderDataCollectService;
import com.example.roman.safemonitor.R;
import com.example.roman.safemonitor.util.MonitorList;
import com.example.roman.safemonitor.util.ServiceUtils;

public class MainActivity extends TitleActivity {

    public Button btn_cpu;//跳转到性能监控界面的按钮
    public Button btn_action;//跳转到敏感行为监控界面的按钮

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        if (!ServiceUtils.isServiceRunning(getApplicationContext(),getPackageName()+R.string.binder_service_name)){
            startService();
        }
    }

    //初始化控件布局
    private void initView(){
        setContentView(R.layout.activity_main);

        setTitle(R.string.app_name);
        showBackwordView(0,false);

        btn_cpu = findViewById(R.id.btn_cpu);
        btn_action = findViewById(R.id.btu_action);

        btn_cpu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //跳转到cpu监控界面
                Intent intent = new Intent(MainActivity.this, CPUActivity.class);
                startActivity(intent);
            }
        });

        btn_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ActionLogActivity.class);
                startActivity(intent);
            }
        });
    }

    private void startService(){
        //开启binder监控服务
        MonitorList monitorList = new MonitorList();
        Intent BinderService = new Intent(this, BinderDataCollectService.class);
        startService(BinderService);

    }

    private void updateLocalLog(){

    }


}
