package com.fjz.custom.view.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.fjz.custom.view.R;


/**
 * Created by Jinzhen Feng on 2022/3/15.
 * Copyright (c) 2022 Podbean. All rights reserved.
 */

/**
 * 水波纹进度条
 */
public class WaveProgressView extends View {

    private final int MAX_WAVE_H = 30;
    private final int BLUR_RADIUS = 15;

    private float progress;
    private final float maxProgress;
    private final int radius;
    private final int circleColor;
    private final int progressBgColor;
    private final int waveColor;
    private final int textColor;
    private final int textSize;
    private final Paint roundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint wavePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    int width, height;
    float centerX, centerY;
    //背景圆的缓冲
    private Bitmap roundBitmap;
    //需要和水波纹做Xfermode处理的缓冲
    private Bitmap xfermodeDstBitmap;
    private Canvas XfermodeCanvas;

    private float blurRadius = 15;

    private Path wavePath = new Path();
    private LinearGradient linearGradient;
    private float offset = 0;
    //水波纹速度
    private float speed = 12;

    public WaveProgressView(Context context) {
        this(context, null);
    }

    public WaveProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayerType(LAYER_TYPE_SOFTWARE, null);

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.WaveProgressView);
        progress = typedArray.getFloat(R.styleable.WaveProgressView_progress, 0);
        maxProgress = typedArray.getFloat(R.styleable.WaveProgressView_maxProgress, 100);

        radius = typedArray.getDimensionPixelSize(R.styleable.WaveProgressView_radius, 100);
        blurRadius = typedArray.getDimensionPixelSize(R.styleable.WaveProgressView_blurRadius, BLUR_RADIUS);
        circleColor = typedArray.getColor(R.styleable.WaveProgressView_circleColor, Color.BLACK);
        progressBgColor = typedArray.getColor(R.styleable.WaveProgressView_progressBgColor, Color.WHITE);
        waveColor = typedArray.getColor(R.styleable.WaveProgressView_waveColor, Color.BLUE);
        textColor = typedArray.getColor(R.styleable.WaveProgressView_textColor, Color.BLACK);
        textSize = typedArray.getDimensionPixelSize(R.styleable.WaveProgressView_textSize, dpToPx(getContext(), 16));

        roundPaint.setColor(circleColor);
        roundPaint.setStyle(Paint.Style.FILL);

        wavePaint.setColor(waveColor);
        wavePaint.setDither(true);

        textPaint.setTypeface(Typeface.createFromAsset(context.getAssets(),"fonts/OpenSans-Semibold.ttf"));

        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawRound(canvas);
        if (progress > 0) {
            drawWave(canvas);
            offset -= speed;
            if (offset <= -width) {
                offset = 0;
            }
            invalidate();
        }
        drawText(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = height = (int) (radius * 2 + blurRadius * 4);
        setMeasuredDimension(width, height);
    }

    //画圆
    private void drawRound(Canvas canvas) {
        if (roundBitmap != null) {//画缓冲BlurMaskFilter的圆
            canvas.drawBitmap(roundBitmap, 0, 0, wavePaint);
            return;
        }

        width = height = (int) (radius * 2 + blurRadius * 4);
        centerX = width / 2.0f;
        centerY = height / 2.0f;

        roundBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        xfermodeDstBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        XfermodeCanvas = new Canvas(xfermodeDstBitmap);

        //画BlurMaskFilter的圆
        BlurMaskFilter maskFilter = new BlurMaskFilter(blurRadius, BlurMaskFilter.Blur.OUTER);
        roundPaint.setMaskFilter(maskFilter);
        Canvas roundCanvas = new Canvas(roundBitmap);
        roundCanvas.drawCircle(centerX, centerY, radius, roundPaint);

        //画Xfermode的圆
        wavePaint.setColor(progressBgColor);
        XfermodeCanvas.drawCircle(centerX, centerY, radius, wavePaint);

        canvas.drawBitmap(roundBitmap, 0, 0, wavePaint);

        //初始化shader
        linearGradient = new LinearGradient(0, height, 0, -height, new int[]{waveColor, Color.TRANSPARENT}, null, Shader.TileMode.CLAMP);
    }


    private void drawWave(Canvas canvas) {
        float ratio = 1 - progress / maxProgress;

        //水波纹Y坐标
        float waveY = height * ratio;
        wavePath.reset();
        wavePath.moveTo(0, waveY);
        wavePath.quadTo(centerX / 2, waveY + MAX_WAVE_H, centerX, waveY);
        wavePath.quadTo(centerX + (width - centerX) / 2, waveY - MAX_WAVE_H, width, waveY);

        wavePath.quadTo(centerX * 2 + (width - centerX) / 2, waveY + MAX_WAVE_H, centerX * 3, waveY);
        wavePath.quadTo(centerX * 3 + (width - centerX) / 2, waveY - MAX_WAVE_H, centerX * 4, waveY);

        //往下画，形成矩形
        wavePath.lineTo(centerX * 4, height);
        wavePath.lineTo(0, height);
        wavePath.lineTo(0, waveY);

        int layerCount = canvas.saveLayer(0, 0, canvas.getWidth(), canvas.getHeight(), null, Canvas.ALL_SAVE_FLAG);
        wavePaint.setColor(waveColor);
        canvas.drawBitmap(xfermodeDstBitmap, 0, 0, wavePaint);
        wavePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        wavePath.offset(offset, 0);
        wavePaint.setShader(linearGradient);
        canvas.drawPath(wavePath, wavePaint);
        wavePaint.setShader(null);
        wavePaint.setXfermode(null);
        canvas.restoreToCount(layerCount);
    }

    private void drawText(Canvas canvas) {
        Paint.FontMetrics m = new Paint.FontMetrics();
        textPaint.getFontMetrics(m);
        int baseLine = (int) (height / 2 - (m.top + m.bottom) / 2);

        String text = null;
        if (Math.abs(progress - maxProgress) < 0.001) {
            text = "Success";
        } else {
            text = (int) (progress / maxProgress * 100) + "%";
        }

        canvas.drawText(text, width / 2f - textPaint.measureText(text) / 2, baseLine, textPaint);
    }

    // @Override
    // public boolean onTouchEvent(MotionEvent event) {
    //     if (event.getAction() == MotionEvent.ACTION_DOWN) {
    //         setProgress(progress + 10);
    //     }
    //     return super.onTouchEvent(event);
    // }

    public float getMaxProgress() {
        return maxProgress;
    }
    public void setProgress(float progress) {
        this.progress = progress > maxProgress ? maxProgress : progress;
        postInvalidate();
    }

    public int dpToPx(Context context, int size) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (size * scale + 0.5f);
    }

}