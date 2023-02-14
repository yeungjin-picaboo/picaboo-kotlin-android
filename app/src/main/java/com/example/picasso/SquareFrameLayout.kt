package com.example.picasso

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

//이거 잘 모르는데 이 레이아웃은 정사각형임
class SquareFrameLayout : FrameLayout {
    constructor(context: Context?) : super(context!!) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context!!, attrs, defStyle
    ) {
    }

    public override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // 여기가 핵심!
        if(widthMeasureSpec > heightMeasureSpec) super.onMeasure(heightMeasureSpec, heightMeasureSpec)
        else super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }
}