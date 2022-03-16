package com.fjz.custom.view.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.fjz.custom.view.R


/**
 * Created by Jinzhen Feng on 2022/3/15.
 * Copyright (c) 2022 Feng. All rights reserved.
 */

private const val TAG = "CircleProgressView"

class CircleProgressView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // 圆形半径
    private var circleRadius: Float = 0f

    // 环形宽度
    private var ringWidth: Float = 0f

    // 环形颜色
    private var ringColor: Int = 0

    // 圆形背景色
    private var circleColor: Int = 0

    // 文字颜色
    private var textColor: Int = 0

    // 文字大小
    private var percentSize: Float = 0f

    // 进度最大值
    private var maxProgress = 0

    var progress: Int = 10

    private val circlePaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = circleColor
            style = Paint.Style.FILL
        }
    }
    private val ringPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = ringColor
            style = Paint.Style.STROKE
            strokeWidth = ringWidth
        }
    }

    private val textPain by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = textColor
            style = Paint.Style.FILL
            textSize = percentSize
        }
    }


    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.WaveProgressView)

        maxProgress = typedArray.getInteger(R.styleable.WaveProgressView_maxProgress, 100)
        circleRadius = typedArray.getDimension(R.styleable.WaveProgressView_circleRadius, 50f)
        ringWidth = typedArray.getDimension(R.styleable.WaveProgressView_ringWidth, 5f)
        ringColor = typedArray.getColor(R.styleable.WaveProgressView_ringColor, Color.RED)
        circleColor = typedArray.getColor(R.styleable.WaveProgressView_circleColor, Color.GRAY)
        textColor = typedArray.getColor(R.styleable.WaveProgressView_textColor, Color.WHITE)
        percentSize = typedArray.getDimension(R.styleable.WaveProgressView_textSize, 20f)


        Log.d(
            TAG, "parameters: $maxProgress, $circleRadius, " +
                    "$ringWidth, $ringColor, $circleColor, $percentSize"
        )
        typedArray.recycle()
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val w = 2 * (circleRadius + ringWidth) + 10
        val h = w

        setMeasuredDimension(w.toInt(), h.toInt())
    }

    override fun onDraw(canvas: Canvas?) {
        drawBg(canvas)
        drawRing(canvas)
        drawText(canvas)
    }

    private fun drawBg(canvas: Canvas?) {
        val centerX: Float = width / 2f
        val centerY: Float = height / 2f
        canvas?.drawCircle(centerX, centerY, circleRadius + ringWidth, circlePaint)

    }

    private fun drawRing(canvas: Canvas?) {
        val startAngle = -90f
        val endAngle = progress * 360f / maxProgress
        canvas?.drawArc(
            ringWidth / 2,
            ringWidth / 2,
            width - ringWidth / 2,
            height - ringWidth / 2,
            startAngle,
            endAngle,
            false,
            ringPaint
        )
    }

    private fun drawText(canvas: Canvas?) {
        val text = "${progress * 100 / maxProgress}%"

        val metrics = textPain.fontMetrics
        val x = width / 2f - textPain.measureText(text) / 2f
        val y = height / 2f - (metrics.bottom + metrics.top) / 2f

        canvas?.drawText(text, x, y, textPain)
    }

    fun updateProgress(progress: Int) {
        if (progress > maxProgress) {
            this.progress = maxProgress
        } else {
            this.progress = progress
        }
        postInvalidate()
    }

}