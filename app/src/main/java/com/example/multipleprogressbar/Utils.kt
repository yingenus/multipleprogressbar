package com.example.multipleprogressbar

import android.content.Context

internal fun dp2pix(context: Context, dp: Int): Int {
    return context.resources.displayMetrics.density.toInt() * dp
}