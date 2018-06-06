package com.example.roman.safemonitor.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.roman.safemonitor.R;
import com.example.roman.safemonitor.model.Data;

import java.util.ArrayList;

public class MyAdapter extends BaseAdapter{

    private Context mContext;
    private ArrayList<Data> mDataList;

    public MyAdapter(Context context, ArrayList<Data> list){
        mContext = context;
        mDataList = list;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.item,null);
            holder = new ViewHolder();
            holder.time_text = view.findViewById(R.id.time);
            holder.pid_text = view.findViewById(R.id.pid);
            holder.packageName_text = view.findViewById(R.id.packageName);
            holder.action_text = view.findViewById(R.id.action);
            view.setTag(holder);
        }else {
            holder = (ViewHolder) view.getTag();
        }
        holder.time_text.setText(mDataList.get(i).getTime());
        holder.pid_text.setText(mDataList.get(i).getPid()+"");
        holder.packageName_text.setText(mDataList.get(i).getPackageName());
        holder.action_text.setText(mDataList.get(i).getAction());

        return view;
    }

    private class ViewHolder{
        TextView time_text;
        TextView pid_text;
        TextView packageName_text;
        TextView action_text;
    }
}
