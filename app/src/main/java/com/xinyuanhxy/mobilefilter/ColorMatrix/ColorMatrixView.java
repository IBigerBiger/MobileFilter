package com.xinyuanhxy.mobilefilter.ColorMatrix;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.xinyuanhxy.mobilefilter.R;

/**
 * Created by xinyuanhxy on 16/9/22.
 */
public class ColorMatrixView extends View{

    private int width, height;// 控件宽高

    /**
     * 色彩平移运算
     *
     */

    ColorMatrix colorMatrix = new ColorMatrix(new float[]{
            1, 0, 0, 0, 100,
            0, 1, 0, 0, 0,
            0, 0, 1, 0, 0,
            0, 0, 0, 1, 0,
    });

    /**
     * 色彩反转
     *
     */
//    ColorMatrix colorMatrix = new ColorMatrix(new float[]{
//            -1,0,0,0,255,
//            0,-1,0,0,255,
//            0,0,-1,0,255,
//            0,0,0,1,0
//    });

    /**
     * 色彩缩放 增大亮度变大
     *
     */

//    ColorMatrix colorMatrix = new ColorMatrix(new float[]{
//            0.8f, 0, 0, 0, 0,
//            0, 0.8f, 0, 0, 50,
//            0, 0, 0.8f, 0, 0,
//            0, 0, 0, 0.8f, 0,
//    });

    /**
     * 通道输出
     *
     */

//    ColorMatrix colorMatrix = new ColorMatrix(new float[]{
//            0, 0, 0, 0, 0,
//            0, 1, 0, 0, 0,
//            0, 0, 0, 0, 0,
//            0, 0, 0, 1, 0,
//    });

    private Paint mPaint = new Paint();
    private Bitmap bitmap;

    public ColorMatrixView(Context context) {
        this(context, null);
    }

    public ColorMatrixView(Context context, AttributeSet attrs) {
         this(context, attrs, 0);
    }

    public ColorMatrixView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mPaint.setAntiAlias(true);
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.p_300px);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 绘制原始位图
        canvas.drawBitmap(bitmap, null, new Rect(0, 0, getWidth() / 2, getWidth() / 2 * bitmap.getHeight() / bitmap.getWidth()), mPaint);

        canvas.translate(getWidth() / 2, 0);

        ColorMatrix mSaturationMatrix = new ColorMatrix();
//        mSaturationMatrix.setSaturation(5);
//        mSaturationMatrix.setScale(1.2f,1.5f,1.1f,1);
        mSaturationMatrix.setRotate(2,90);

        mPaint.setColorFilter(new ColorMatrixColorFilter(mSaturationMatrix));
        canvas.drawBitmap(bitmap, null, new Rect(0, 0, getWidth()/2, getWidth()/2 * bitmap.getHeight() / bitmap.getWidth()), mPaint);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 获得宽高测量模式和大小
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        // 保存测量结果
        int width, height;

        if (widthMode == MeasureSpec.EXACTLY) {
            // 宽度
            width = widthSize;
        } else {
            // 宽度加左右内边距
            width = this.width + getPaddingLeft() + getPaddingRight();
            if (widthMode == MeasureSpec.AT_MOST) {
                // 取小的那个
                width = Math.min(width, widthSize);
            }

        }

        if (heightMode == MeasureSpec.EXACTLY) {
            // 高度
            height = heightSize;
        } else {
            // 高度加左右内边距
            height = this.height + getPaddingTop() + getPaddingBottom();
            if (heightMode == MeasureSpec.AT_MOST) {
                // 取小的那个
                height = Math.min(height, heightSize);
            }

        }
        setMeasuredDimension(width, height);

    }
}
