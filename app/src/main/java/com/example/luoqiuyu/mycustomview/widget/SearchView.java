package com.example.luoqiuyu.mycustomview.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * 来自：https://github.com/GcsSloop/AndroidNote/blob/master/CustomView/Advance/Code/SearchView.java
 * Created  on 17/4/14.
 */

public class SearchView extends View {

    private Paint mPaint;
    private int mViewWidth;
    private int mViewHeigth;

    //不同状态
    public static enum State{
        NONE,
        STARTING,
        SEARCHING,
        ENDING
    }

    private State mCurrentState = State.NONE;//当前状态

    private Path path_search;//放大镜
    private Path path_circle;//外部圆环
    private PathMeasure mMeasure;
    private int defaultDuration = 2000;//动效周期

    //控制各个过程的动画
    private ValueAnimator mStartingAnimator;
    private ValueAnimator mSearchingAnimator;
    private ValueAnimator mEndingAnimator;

    //动画数值？？
    private float mAnimatorValue = 0;

    //动效过程监听器
    private ValueAnimator.AnimatorUpdateListener mUpdateListener;
    private Animator.AnimatorListener mAnimatorListener;

    private Handler mAnimatorHandler;//控制动画状态转换

    private boolean isOver = false;//判断是否已经搜索结束
    private int count = 0;


    public SearchView(Context context) {
        super(context,null);
    }

    public SearchView(Context context,AttributeSet attrs) {
        super(context, attrs);
        initAll();
    }

    public void initAll(){
        initPaint();
        initPath();
        initListener();
        initHandler();
        initAnimator();

        mCurrentState = State.STARTING;
        mStartingAnimator.start();
    }

    private void initPaint(){
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(10);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setAntiAlias(true);
    }

    private void initPath(){
        path_circle = new Path();
        path_search = new Path();

        mMeasure = new PathMeasure();

        RectF oval1 = new RectF(-50,-50,50,50);
        path_search.addArc(oval1,45,359.9f);

        RectF oval2 = new RectF(-100,-100,100,100);
        path_circle.addArc(oval2,45,-359.9f);

        float[] pos = new float[2];
        mMeasure.setPath(path_circle,false);
        mMeasure.getPosTan(0,pos,null);

        path_search.lineTo(pos[0],pos[1]);
    }

    private void initListener(){
        mUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAnimatorValue = (float) animation.getAnimatedValue();
                invalidate();
            }
        };
        mAnimatorListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimatorHandler.sendEmptyMessage(0);

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        };

    }

    private void initHandler(){
        mAnimatorHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (mCurrentState){
                    case STARTING:
                        isOver = false;
                        mCurrentState = State.SEARCHING;
                        mStartingAnimator.removeAllUpdateListeners();
                        mStartingAnimator.start();
                        break;
                    case SEARCHING:
                        if (!isOver){
                            mSearchingAnimator.start();
                            Log.i("tag","RESTART");
                            count++;
                            if (count > 2){
                                isOver = true;
                            }
                        }else {
                            mCurrentState = State.ENDING;
                            mEndingAnimator.start();
                        }
                        break;
                    case ENDING:
                        mCurrentState = State.NONE;
                        break;

                }
            }
        };

    }

    private void initAnimator(){
        //??
        mStartingAnimator = ValueAnimator.ofFloat(0,1).setDuration(defaultDuration);
        mSearchingAnimator = ValueAnimator.ofFloat(0,1).setDuration(defaultDuration);
        mEndingAnimator = ValueAnimator.ofFloat(1,0).setDuration(defaultDuration);

        mStartingAnimator.addUpdateListener(mUpdateListener);
        mSearchingAnimator.addUpdateListener(mUpdateListener);
        mEndingAnimator.addUpdateListener(mUpdateListener);

        mStartingAnimator.addListener(mAnimatorListener);
        mSearchingAnimator.addListener(mAnimatorListener);
        mEndingAnimator.addListener(mAnimatorListener);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeigth = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawSearch(canvas);
    }

    private void drawSearch(Canvas canvas){
        mPaint.setColor(Color.WHITE);

        canvas.translate(mViewWidth/2,mViewHeigth/2);
        canvas.drawColor(Color.parseColor("#0082D7"));

        switch (mCurrentState){
            case NONE:
                canvas.drawPath(path_search,mPaint);
                break;
            case STARTING:
                mMeasure.setPath(path_search,false);
                Path dst = new Path();
                mMeasure.getSegment(mMeasure.getLength()*mAnimatorValue,mMeasure.getLength(),dst,true);
                canvas.drawPath(dst,mPaint);
                break;
            case SEARCHING:
                mMeasure.setPath(path_circle,false);
                Path dst2 = new Path();
                float stop = mMeasure.getLength()*mAnimatorValue;
                float start = (float) (stop - ((0.5 - Math.abs(mAnimatorValue - 0.5)) * 200f));//??
                mMeasure.getSegment(start,stop,dst2,true);
                canvas.drawPath(dst2,mPaint);
                break;
            case ENDING:
                mMeasure.setPath(path_search,false);
                Path dst3 = new Path();
                mMeasure.getSegment(mMeasure.getLength()*mAnimatorValue,mMeasure.getLength(),dst3,true);
                canvas.drawPath(dst3,mPaint);
                break;
        }

    }
}
