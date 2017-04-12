package com.example.luoqiuyu.mycustomview.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/**
 * 参考：http://blog.csdn.net/crazy__chen/article/details/50163693
 * Created by luoqiuyu on 17/4/12.
 * 绘制关键点：以中心点为出发点，找到六角形的顶点，然后一步步绘制
 */

public class RadarView extends View {
    private int count = 6;
    private float angle = (float) (Math.PI*2/count);
    private float radius;//网格最大半径
    private int centerX;
    private int centerY;
    private String[] titles={"a","b","c","d","e","f"};
    private double[] data = {100,60,60,100,50,10,20};
    private float maxValue = 100;
    private Paint mainPaint;
    private Paint valuePaint;
    private Paint textPaint;


    public RadarView(Context context) {
        super(context,null);
    }

    public RadarView(Context context,AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    //初始化
    private void init(){
        count = Math.min(data.length,titles.length);

        mainPaint = new Paint();
        mainPaint.setAntiAlias(true);
        mainPaint.setColor(Color.GRAY);
        mainPaint.setStyle(Paint.Style.STROKE);

        valuePaint = new Paint();
        valuePaint.setAntiAlias(true);
        valuePaint.setColor(Color.BLUE);
        valuePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.BLACK);
        textPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        radius = Math.min(h,w)/2*0.9f;
        centerX = w/2;
        centerY = h/2;
        postInvalidate();//为什么要做这一步操作??

        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
        drawPolygon(canvas);
        drawLines(canvas);
        drawText(canvas);
        drawRegion(canvas);

    }

    /**
     * 绘制正多边形
     * @param canvas
     */
    private void drawPolygon(Canvas canvas){
        Path path = new Path();
        float r = radius/(count - 1);
        for (int i=1;i<count;i++){
            float curR = r*i;
            path.reset();
            for (int j=0;j<count;j++){
                if (j==0){
                    path.moveTo(centerX+curR,centerY);
                }else {
                    float x = (float) (centerX+curR*Math.cos(angle*j));
                    float y = (float) (centerY+curR*Math.sin(angle*j));
                    path.lineTo(x,y);
                }
            }
            path.close();
            canvas.drawPath(path,mainPaint);
        }

    }

    /**
     *绘制直线
     * @param canvas
     */
    private void drawLines(Canvas canvas){
        Path path = new Path();
        for (int i=0;i<count;i++){
            path.reset();
            path.moveTo(centerX,centerY);
            float x = (float) (centerX+radius*Math.cos(angle*i));
            float y = (float) (centerY+radius*Math.sin(angle*i));
            path.lineTo(x,y);
            canvas.drawPath(path,mainPaint);
        }
    }

    /**
     *绘制文字
     * @param canvas
     */
    private void drawText(Canvas canvas){
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float fontHeight = fontMetrics.descent - fontMetrics.ascent;
        for (int i=0;i<count;i++){
            float currentAngle = angle*i;
            float x = (float) (centerX+(radius+fontHeight/2)*Math.cos(currentAngle));
            float y = (float) (centerY+(radius+fontHeight/2)*Math.sin(currentAngle));
            if (currentAngle>=0&&currentAngle<=Math.PI/2){
                canvas.drawText(titles[i],x,y,textPaint);
            }else if (currentAngle>=3*Math.PI/2&&currentAngle<=Math.PI*2){
                canvas.drawText(titles[i],x,y,textPaint);
            }else if (currentAngle>Math.PI/2&&currentAngle<=Math.PI){
                float dis = textPaint.measureText(titles[i]);
                canvas.drawText(titles[i],x-dis,y,textPaint);
            }else if (currentAngle>Math.PI&&currentAngle<3*Math.PI/2){
                float dis = textPaint.measureText(titles[i]);
                canvas.drawText(titles[i],x-dis,y,textPaint);
            }

        }

    }

    /**
     *绘制区域
     * @param canvas
     */
    private void drawRegion(Canvas canvas){
        Path path = new Path();
        valuePaint.setAlpha(255);
        for (int i=0;i<count;i++){
            double percent = data[i]/maxValue;
            float x = (float) (centerX+radius*Math.cos(angle*i)*percent);
            float y = (float) (centerY+radius*Math.sin(angle*i)*percent);
            if (i==0){
                path.moveTo(x,y);
            }else {
                path.lineTo(x,y);
            }
            canvas.drawCircle(x,y,5,valuePaint);
        }
        valuePaint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(path,valuePaint);
        valuePaint.setAlpha(127);

        valuePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawPath(path,valuePaint);
    }

    public void setTitles(String[] titles) {
        this.titles = titles;
    }

    public void setData(double[] data) {
        this.data = data;
    }

    public void setMaxValue(float maxValue) {
        this.maxValue = maxValue;
    }

    public void setMainPaint(Paint mainPaint) {
        this.mainPaint = mainPaint;
    }

    public void setValuePaint(Paint valuePaint) {
        this.valuePaint = valuePaint;
    }

    public void setTextPaint(Paint textPaint) {
        this.textPaint = textPaint;
    }

    public float getMaxValue() {
        return maxValue;
    }
}
