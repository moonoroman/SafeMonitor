package com.example.roman.safemonitor.Activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.example.roman.safemonitor.R;
import com.example.roman.safemonitor.adapter.MyAdapter;
import com.example.roman.safemonitor.model.Data;
import com.example.roman.safemonitor.util.MonitorList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ActionLogActivity extends TitleActivity{

    private ArrayList<Data> mDatalist;
    private MyAdapter mMyAdapter;
    private ListView mListView;
    private Button mUpdateButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getData();
        initView();

    }

    private void initView(){
        setContentView(R.layout.activity_log);
        setTitle("敏感行为监控列表");
        showBackwordView(R.string.backword, true);
        showUpdateView(true);


        mListView = findViewById(R.id.listView);

        //绑定数据
        mMyAdapter = new MyAdapter(getApplicationContext(), mDatalist);
        mListView.setAdapter(mMyAdapter);

        mUpdateButton = findViewById(R.id.btn_update);
        mUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //重新获取并绑定数据
                getData();
                mMyAdapter = new MyAdapter(getApplicationContext(), mDatalist);
                mListView.setAdapter(mMyAdapter);
            }
        });
    }

    private void getData(){
        mDatalist = new ArrayList<>();
        try {
            if (readline(getApplicationContext(), MonitorList.DATA_FILE_PATH, mDatalist)==0){
                Log.i("ActionLogActivity","Failed to read a line from file!");
            }
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    private int readline(Context context, String filename, ArrayList<Data> datalist) throws IOException{
        if(datalist==null) {
            System.out.println("Datalist is null");
            return 0;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(System.currentTimeMillis());
        String time = simpleDateFormat.format(date);
        FileInputStream inputStream = context.openFileInput(filename);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line=bufferedReader.readLine())!=null){
            if(!line.startsWith(time)){
                inputStream.close();
                File file = new File(getFilesDir()+"/"+filename);
                if (file.delete()){
                    return 1;
                }else{
                    return 0;
                }
            }
            String[] arr = line.split(", ");
            Data data = new Data(arr[0],Integer.parseInt(arr[1]),arr[2],arr[3]);
            datalist.add(data);
        }
        inputStream.close();
        return 1;
    }

}
