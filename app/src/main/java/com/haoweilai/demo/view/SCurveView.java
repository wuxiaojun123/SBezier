package com.haoweilai.demo.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.haoweilai.demo.R;

import java.util.ArrayList;
import java.util.List;

/***
 * 1.绘制S型曲线
 * 2.绘制4个icon
 * 3.绘制节点1到节点2的动画
 */
public class SCurveView extends View {

    private static final String TAG = "SCurveView";

    private Paint mPaint; // 画笔
    private int mCenterX; // view x轴中心点
    private int mCenterY; // view y轴中心点

    private PointF mStartPoint; // 开始节点
    private PointF mEndPoint; // 结束节点
    private PointF mControl1; // 控制点1
    private PointF mControl2; // 控制点2

    private Path path; // s型曲线路径
    private int mRadius; // 圆的半径
    private float[] pos; // 某个path上的坐标点
    private PathMeasure measure; // 测量path
    private float mPathLength; // path的长度

    private Bitmap mBitmap1; // icon1
    private Bitmap mBitmap2; // icon2

    private List<PointF> mPointFs = new ArrayList<>(); // 节点1到节点2上的所有点集合

    public SCurveView(Context context) {
        this(context, null);
    }

    public SCurveView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SCurveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initPaint();

        path = new Path();
        pos = new float[2];
        mRadius = dip2px(60);
        measure = new PathMeasure();

        mBitmap1 = BitmapFactory.decodeResource(getResources(), R.mipmap.f015);
        mBitmap2 = BitmapFactory.decodeResource(getResources(), R.mipmap.f040);
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setDither(true);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(8);
        mPaint.setColor(Color.RED);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.rotate(15, mCenterX, mCenterY);
        drawBezier(canvas);
        drawBitmap(canvas);
        drawAnimLine(canvas);
    }


    private void drawAnimLine(Canvas canvas) {
        if (mPointFs.size() == 0) {
            return;
        }
        mPaint.setColor(Color.BLUE);
        // 这里可能会造成重复绘制，暂时没想到更好的方式，当然可以使用一个对象加上标记，只绘制没有绘制过的点
        for (int i = 0; i < mPointFs.size(); i++) {
            PointF pointF = mPointFs.get(i);
            canvas.drawPoint(pointF.x, pointF.y, mPaint);
        }
    }


    private void drawBezier(Canvas canvas) {
        mPaint.setColor(Color.RED);
        path.moveTo(mStartPoint.x, mStartPoint.y);
        path.cubicTo(mControl1.x, mControl1.y, mControl2.x, mControl2.y, mEndPoint.x, mEndPoint.y);

        canvas.drawPath(path, mPaint);
    }


    private void drawBitmap(Canvas canvas) {
        measure.setPath(path, false);
        mPathLength = measure.getLength();

        measure.getPosTan(mPathLength * 0, pos, null);
        canvas.drawBitmap(mBitmap1, pos[0] - mBitmap1.getWidth() / 2, pos[1] - mBitmap1.getHeight() / 2, null);

        measure.getPosTan((mPathLength * 0.25f), pos, null);
        canvas.drawBitmap(mBitmap2, pos[0] - mBitmap2.getWidth() / 2, pos[1] - mBitmap2.getHeight() / 2, null);

        measure.getPosTan((mPathLength * 0.75f), pos, null);
        canvas.drawBitmap(mBitmap1, pos[0] - mBitmap1.getWidth() / 2, pos[1] - mBitmap1.getHeight() / 2, null);

        measure.getPosTan((mPathLength * 1), pos, null);
        canvas.drawBitmap(mBitmap2, pos[0] - mBitmap2.getWidth() / 2, pos[1] - mBitmap2.getHeight() / 2, null);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCenterX = w / 2;
        mCenterY = h / 2;
        initPointF();
    }

    /***
     * 初始化起始点和结束点以及两个控制点，这里的*2和*3只是为了让S型曲线更饱满点
     * 也可以根据现在的比例来动态计算，这样可能扩展性更高点
     */
    private void initPointF() {
        if (mStartPoint == null) {
            mStartPoint = new PointF();
        }
        if (mEndPoint == null) {
            mEndPoint = new PointF();
        }
        if (mControl1 == null) {
            mControl1 = new PointF();
        }
        if (mControl2 == null) {
            mControl2 = new PointF();
        }

        mStartPoint.x = mCenterX;
        mStartPoint.y = mCenterY - mRadius * 2;

        mEndPoint.x = mCenterX;
        mEndPoint.y = mCenterY + mRadius * 2;

        mControl1.x = mCenterX - mRadius * 3;
        mControl1.y = mCenterY - mRadius;

        mControl2.x = mCenterX + mRadius * 3;
        mControl2.y = mCenterY + mRadius;

    }

    public void startAnim() {
        final float[] pos = new float[2];
        final ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, measure.getLength() * 0.25f);
        valueAnimator.setDuration(4000);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                // 拿到当前S型曲线上的各个节点
                measure.getPosTan(value, pos, null);
                mPointFs.add(new PointF(pos[0], pos[1]));
                postInvalidate();
            }
        });
        valueAnimator.start();
    }

    private int dip2px(int dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, getResources().getDisplayMetrics());
    }

}
