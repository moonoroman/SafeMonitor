package com.example.roman.safemonitor.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

public class ChartView extends View {
    private int XPoint = 80;
    private int YPoint;
    private int XScale = 30;  //刻度长度
    private int YScale = 90;
    private final int index = 20;
    private int XLength;
    private int YLength;

    private int MaxDataSize;

    private List<Float> data = new ArrayList<Float>();

    private String[] YLabel;

//    public Handler handler = new Handler(){
//        public void handleMessage(Message msg) {
//            if(msg.what == 0x1234){
//                ChartView.this.invalidate();
//            }
//        };
//    };

    public ChartView(Context context, AttributeSet attrs) {
        super(context,attrs);

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int ScreenWidth = wm.getDefaultDisplay().getWidth();
        int ScreenHeight = wm.getDefaultDisplay().getHeight();
        XLength = ScreenWidth - 2*XPoint;
        YLength = ScreenHeight/2 - 400;
        YPoint = YLength + 20;
        MaxDataSize = XLength / XScale;

        YLabel = new String[6];
        //Y轴刻度
        int temp=0;
        for(int i=0; i<YLabel.length; i++){
            YLabel[i] = temp + "%";
            temp += 20;
        }

//        new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                while(true){
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    if(data.size() >= MaxDataSize){
//                        data.remove(0);
//                    }
//                    if()
//                    data.add();
//                    handler.sendEmptyMessage(0x1234);
//                }
//            }
//        }).start();
    }

    public void addData(float rate){
        if(data.size() >= MaxDataSize)
            data.remove(0);
        data.add(rate);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true); //去锯齿
        paint.setColor(Color.BLACK);
        paint.setTextSize(28);
        paint.setStrokeWidth(5);

        //画Y轴
        canvas.drawLine(XPoint, YPoint - YLength, XPoint, YPoint, paint);

        //Y轴箭头
        canvas.drawLine(XPoint, YPoint - YLength, XPoint - 6, YPoint-YLength + 12, paint);  //箭头
        canvas.drawLine(XPoint, YPoint - YLength, XPoint + 6, YPoint-YLength + 12 ,paint);

        //添加刻度和文字
        for(int i=0; i < YLabel.length; i++) {
            canvas.drawLine(XPoint, YPoint - i * YScale, XPoint + 5, YPoint - i * YScale, paint);  //刻度
            paint.setStrokeWidth(0);
            canvas.drawText(YLabel[i], XPoint - 70, YPoint - i * YScale, paint);//文字
        }

        paint.setStrokeWidth(5);
        //画X轴
        canvas.drawLine(XPoint, YPoint, XPoint + XLength, YPoint, paint);
//        System.out.println("Data.size = " + data.size());

        //画曲线
        paint.setColor(Color.BLUE);
        if(data.size() > 1){
            for(int i=1; i<data.size(); i++){
                canvas.drawLine(XPoint + (i-1) * XScale, YPoint - data.get(i-1) / 20 * YScale,
                        XPoint + i * XScale, YPoint - data.get(i) / 20 * YScale, paint);
            }
        }
    }
}
