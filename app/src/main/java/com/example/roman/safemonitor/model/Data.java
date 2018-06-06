package com.example.roman.safemonitor.model;

public class Data {

    private int pid;
    private String packageName;
    private String action;
    private String time;

    public Data(String time, int pid, String packageName, String action){
        this.pid = pid;
        this.packageName = packageName;
        this.action = action;
        this.time = time;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
