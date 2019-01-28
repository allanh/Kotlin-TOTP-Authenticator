package com.udnshopping.udnsauthorizer.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import androidx.core.content.ContextCompat
import android.view.View
import com.udnshopping.udnsauthorizer.R


class Box internal constructor(context: Context) : View(context) {
    private val paint = Paint()

    var rectangle: Rect = Rect(100, 100, 100, 100)
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        val width = right - left
        val height = bottom - top
        rectangle = Rect(width / 4, height / 2 - width / 4, right - (width / 4), height / 2 + width / 4)
    }

    override fun onDraw(canvas: Canvas) { // Override the onDraw() Method
        super.onDraw(canvas)

        paint.setStyle(Paint.Style.STROKE)
        paint.setColor(ContextCompat.getColor(context, R.color.colorAccent))
        paint.setStrokeWidth(5.0f)

        //center
        val x0 = canvas.getWidth() / 2
        val y0 = canvas.getHeight() / 2
        val dx = canvas.getHeight() / 6
        val dy = canvas.getHeight() / 6
        //draw guide box
//        canvas.drawRect((x0 - dx).toFloat(), (y0 - dy).toFloat(), (x0 + dx).toFloat(), (y0 + dy).toFloat(), paint)
        canvas.drawRect(rectangle, paint)
    }
}