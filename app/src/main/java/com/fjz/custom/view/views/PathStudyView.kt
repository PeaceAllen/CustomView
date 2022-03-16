package com.fjz.custom.view.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View


/**
 * Created by Jinzhen Feng on 2022/3/15.
 * Copyright (c) 2022 Feng. All rights reserved.
 */
class PathStudyView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // 水波移动的速度
    private var mSpeed = 5f
    // 水波的波长
    private var waveWidth: Float = 400f
    // 水波的振幅/2
    private var waveHeight: Float = 40f
    // 水波移动的起点
    private var mOffset = -waveWidth

    private val mBgPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLUE
            style = Paint.Style.FILL_AND_STROKE
        }
    }

    private val mPathPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.RED
            style = Paint.Style.FILL
            strokeWidth = 5f
        }
    }

    private val mWavePath by lazy {
        Path()
    }


    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val w = MeasureSpec.getSize(widthMeasureSpec)
        val h = MeasureSpec.getSize(heightMeasureSpec)

        setMeasuredDimension(w, h)

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.drawRect(0f, 0f, measuredWidth-1f, measuredHeight-1f, mBgPaint)
        // drawLine(canvas)
        drawPath(canvas)

        mOffset += mSpeed
        if (mOffset >= 0) {
            mOffset = -waveWidth
        }
        invalidate()
    }

    private fun drawPath(canvas: Canvas?) {

        mWavePath.apply {

            reset()
            var x1 = 0f + mOffset
            var y1 = height / 2f
            var x2  = 0f
            var y2 = 0f
            var controlX: Float
            var controlY: Float

            moveTo(x1,y1)

            for (i in 0 until ((width+ 2 * waveWidth) / waveWidth).toInt()) {
                controlX = x1 + i * waveWidth
                controlY = y1 + waveHeight
                x2 = controlX + waveWidth/4
                y2 = height / 2f
                quadTo(controlX, controlY, x2, y2)

                controlX = x2 + waveWidth/4
                controlY = y2 - waveHeight
                x2 = controlX + waveWidth/4
                y2 = height / 2f
                quadTo(controlX, controlY, x2, y2)
            }

            lineTo(x2, height-1f)
            lineTo(x1, height-1f)
            lineTo(x1, y1)

        }
        canvas?.drawPath(mWavePath, mPathPaint)
    }

    private fun drawLine(canvas: Canvas?) {
        var x1 = 0f
        var y1 = height / 2f
        var x2 = width - 1f
        var y2 = y1
        canvas?.drawLine(x1, y1, x2, y2, mPathPaint)
    }


    // override fun onTouchEvent(event: MotionEvent?): Boolean {
    //
    //
    //     if (event?.action == MotionEvent.ACTION_DOWN) {
    //         mOffset += mSpeed
    //         if (mOffset >= 0) {
    //             mOffset =  -waveWidth
    //         }
    //         postInvalidate()
    //     }
    //     return super.onTouchEvent(event)
    // }

}