package com.example.roman.safemonitor.Monitor;

import android.app.IntentService;
import android.content.Intent;
import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.support.annotation.Nullable;

import com.example.roman.safemonitor.util.FileHelper;
import com.example.roman.safemonitor.util.StreamGobbler;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class BinderDataCollectService extends IntentService {

    private StringBuilder sb;
    private FileHelper mFileHelper;
    public static String EXECUTABLE_FILE_PATH;

    public BinderDataCollectService() {
        super("BinderDataCollectService");
        sb = new StringBuilder();
        mFileHelper = new FileHelper();
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        EXECUTABLE_FILE_PATH = getFilesDir() + "/inject";
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            handleBinderMonitor();
        }
    }

    private void handleBinderMonitor() {
        writeInject(EXECUTABLE_FILE_PATH);//将实现进程注入的可执行文件写入到files目录下
        RootCommand();//执行inject，进行注入
        createSocket();//建立与Native层通信的socket服务
    }


    public void createSocket(){
        LocalServerSocket serverSocket = null;
        LocalSocket receiver = null;
        InputStream inputStream=null;
        try {
            //建立服务端
            serverSocket = new LocalServerSocket("com.safemonitor.localsocket");
            while (true) {
                receiver = serverSocket.accept();
                inputStream = receiver.getInputStream();
                //读取输出流，获取客户端发来的数据
                StreamGobbler outGobbler = new StreamGobbler(inputStream, "Socket",getApplicationContext());
                outGobbler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(inputStream!=null)inputStream.close();
                if(receiver!=null) receiver.close();
                if(serverSocket!=null) serverSocket.close();
            }catch (IOException e){
                e.printStackTrace();;
            }
        }
    }

    public void writeInject(String executablePath) {
        mFileHelper.fileIsExists(getFilesDir().getParent() + "/files/");
        if (!mFileHelper.fileIsExists(executablePath)) {
            try {
                InputStream input = getResources().getAssets().open("inject");//读取Assets目录下的可执行文件到输入流
                int filesize = input.available(); //获取文件大小
                mFileHelper.writeFileData(EXECUTABLE_FILE_PATH, input, filesize);//将文件写到executableFilePath下
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void RootCommand() {
        Process process = null;
        DataOutputStream outputStream = null;
        StringBuilder s = new StringBuilder();
        try {
            process = Runtime.getRuntime().exec("su");
            outputStream = new DataOutputStream(process.getOutputStream());
            outputStream.writeBytes("setenforce 0\n");

            outputStream.writeBytes("cd " + getFilesDir() + "\n");
            outputStream.writeBytes("chmod 0777 inject\n");
            outputStream.writeBytes("./inject\n");
            outputStream.writeBytes("exit\n");
            outputStream.flush();

            //处理标准输出流和错误输出流
            StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), "ERROR",getApplicationContext());
            errorGobbler.start();
            StreamGobbler outGobbler = new StreamGobbler(process.getInputStream(), "STDOUT",getApplicationContext());
            outGobbler.start();

            process.waitFor();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(outputStream!=null) outputStream.close();
                if(process!=null) process.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
