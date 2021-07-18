package com.gl.gllibrary.widget.pullrefersh

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

abstract class PullRefreshEmptyView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    abstract fun onEmptyStatus(status: Int)

}