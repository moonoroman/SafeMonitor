package com.example.roman.safemonitor.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.roman.safemonitor.R;

public class TitleActivity extends AppCompatActivity implements View.OnClickListener{

    private Button mBackWordButton;
    private Button mUpdateButton;
    private TextView mTitleText;
    private FrameLayout mContentLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView(){
        super.setContentView(R.layout.activity_title);
        mBackWordButton = findViewById(R.id.btn_backward);
        mUpdateButton = findViewById(R.id.btn_update);
        mTitleText = findViewById(R.id.text_title);
        mContentLayout = findViewById(R.id.layout_content);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_backward:
                finish();
            case R.id.btn_update:

        }
    }

    /*
    params:
    BackWordResid  返回按钮的文字
    isShow  是否显示
     */
    protected void showBackwordView(int BackWordResid, boolean isShow){
        if(mBackWordButton!=null){
            if (isShow){
                mBackWordButton.setText(BackWordResid);
                mBackWordButton.setVisibility(View.VISIBLE);
            }else {
                mBackWordButton.setVisibility(View.INVISIBLE);
            }
        }
    }

    /*
    params:
    BackWordResid  刷新按钮的文字
    isShow  是否显示
     */
    protected void showUpdateView(boolean isShow){
        if(mUpdateButton!=null){
            if (isShow){
                mUpdateButton.setVisibility(View.VISIBLE);
            }else {
                mUpdateButton.setVisibility(View.INVISIBLE);
            }
        }
    }

    /*
    设置标题文字
     */
    public void setTitle(String title){
        mTitleText.setText(title);
    }

    public void setTitle(int StringID){
        mTitleText.setText(StringID);
    }

    //取出FrameLayout并调用父类removeAllViews()方法
    @Override
    public void setContentView(int layoutResID) {
        mContentLayout.removeAllViews();
        View.inflate(this, layoutResID, mContentLayout);
        onContentChanged();
    }

    @Override
    public void setContentView(View view) {
        mContentLayout.removeAllViews();
        mContentLayout.addView(view);
        onContentChanged();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        mContentLayout.removeAllViews();
        mContentLayout.addView(view,params);
        onContentChanged();
    }
}
