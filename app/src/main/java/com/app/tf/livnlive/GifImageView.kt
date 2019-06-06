package com.app.tf.livnlive


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.net.Uri;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.io.FileNotFoundException;
import java.io.InputStream;
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import android.view.Display
import android.util.DisplayMetrics





class GifImageView : View {

    private var mInputStream: InputStream? = null
    private var mMovie: Movie? = null
    private var mWidth: Int = 0
    private var mHeight: Int = 0
    private var mStart: Long = 0
    private var mContext: Context? = null

    constructor(context: Context) : super(context) {
        this.mContext = context
    }

    @JvmOverloads constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr) {
        this.mContext = context
        if (attrs.getAttributeName(1).equals("background")) {
            val id = Integer.parseInt(attrs.getAttributeValue(1).substring(1))
            setGifImageResource(id)
        }
    }


    private fun init() {
        setFocusable(true)
        mMovie = Movie.decodeStream(mInputStream)
        mWidth = mMovie!!.width()
        mHeight = mMovie!!.height()

        requestLayout()
    }

    protected override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(mWidth, mHeight)
    }

    protected override fun onDraw(canvas: Canvas) {

        val now = SystemClock.uptimeMillis()

        if (mStart == 0L) {
            mStart = now
        }

        if (mMovie != null) {

            var duration = mMovie!!.duration()
            if (duration == 0) {
                duration = 1000
            }

            val relTime = ((now - mStart) % duration).toInt()

            mMovie!!.setTime(relTime)
            val displayMetrics = mContext!!.resources.displayMetrics
            val width = displayMetrics.widthPixels
            val height = displayMetrics.heightPixels
            val ratio = width.toFloat()/mWidth.toFloat()
            val heightRatio = height.toFloat()/mHeight.toFloat()

            canvas.scale(ratio, heightRatio)

            mMovie!!.draw(canvas, 0f, 0f)
            invalidate()
        }
    }

    fun setGifImageResource(id: Int) {
        mInputStream = mContext!!.getResources().openRawResource(id)
        init()
    }

    fun setGifImageUri(uri: Uri) {
        try {
            mInputStream = mContext!!.getContentResolver().openInputStream(uri)
            init()
        } catch (e: FileNotFoundException) {
            Log.e("GIfImageView", "File not found")
        }

    }
}