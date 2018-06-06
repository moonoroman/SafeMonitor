package com.example.roman.safemonitor.util;

import android.content.Context;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileHelper {

    public static void save(Context context, String filename, String content) throws IOException{
        FileOutputStream outputStream = context.openFileOutput(filename, Context.MODE_APPEND);
        outputStream.write(content.getBytes());
        outputStream.close();
    }

    public static String read(Context context, String filename) throws IOException{
        FileInputStream inputStream = context.openFileInput(filename);
        byte []temp = new byte[1024];
        StringBuilder sb = new StringBuilder("");
        int len;
        while ((len=inputStream.read(temp))>0){
            sb.append(new String(temp,0,len));
        }
        inputStream.close();
        return sb.toString();
    }

    public void writeFileData(String fileName, InputStream message, int filesize) {
        try {
            FileOutputStream fout = new FileOutputStream(fileName);
            byte[] bytes = new byte[filesize];
            message.read(bytes);
            fout.write(bytes);
            fout.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean fileIsExists(String fileName) {
        File f = new File(fileName);
        if (!f.exists())
            return false;
        return true;
    }
}
