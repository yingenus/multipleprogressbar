package com.yingenus.multipleprogressbar

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.util.Log
import android.util.Property
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import androidx.annotation.ColorInt
import androidx.annotation.StyleableRes
import java.lang.ref.WeakReference
import kotlin.reflect.KMutableProperty1

internal const val correction = 270
var animationDuration : Long = 300


internal fun TypedArray.getStateList(@StyleableRes index: Int, defColorStateList: ColorStateList)
    = this.getColorStateList(index)?: defColorStateList

internal fun TypedArray.getStateList(@StyleableRes index: Int,@ColorInt defColor: Int)
        = this.getStateList(index,ColorStateList.valueOf(defColor))

internal fun applyCorrection( angle : Float): Float{
    val corrected = angle + correction
    return if (corrected >= 0){
        corrected.rem(360)
    }else{
        360 - corrected.rem(360)
    }
}

internal class RelativeRotateAnimation(
        fromDegrees: Float,
        toDegrees: Float,
        pivotXType: Int,
        pivotXValue: Float,
        pivotYType: Int,
        pivotYValue: Float) :
        RotateAnimation(
                fromDegrees,
                toDegrees,
                pivotXType,
                pivotXValue,
                pivotYType,
                pivotYValue) {

    var refDiameter : Int = -1
        set(value) {
            field = value
            if ( field > 0 && viewWidth != -1){
                duration = ringDuration(field,viewWidth,duration)
            }
        }

    override fun setDuration(durationMillis: Long) {
        if ( refDiameter > 0 && viewWidth != -1){
            super.setDuration(ringDuration(refDiameter,viewWidth,durationMillis))
        }else{
            super.setDuration(durationMillis)
        }
    }

    var viewWidth: Int = -1

    override fun initialize(width: Int, height: Int, parentWidth: Int, parentHeight: Int) {
        viewWidth = width
        if ( refDiameter > 0){
            duration = ringDuration(refDiameter,viewWidth,duration)
        }
        super.initialize(width, height, parentWidth, parentHeight)
    }
}


internal fun ringDuration(firstDiameter : Int, nextDiameter: Int, duration: Long ): Long
        = ((nextDiameter.toFloat()/firstDiameter.toFloat()) * duration).toLong()

internal fun dp2pix(context: Context, dp: Int): Int {
    return context.resources.displayMetrics.density.toInt() * dp
}

internal fun getValueAnimator(progressItem: ProgressItem, parameter : KMutableProperty1<ProgressItem,Int>,
                              startValue: Int, endValue: Int): Animator{

    val animation = ValueAnimator.ofInt(startValue,endValue)
    animation.duration = animationDuration
    animation.addUpdateListener { listener ->
        parameter.set(progressItem,listener.animatedValue as Int)
        progressItem.invalidate()
    }

    return animation
}

internal fun getRotatedAnimator(rotateRight: Boolean, view : ProgressItem): Animator {

    val finalAngle = if (rotateRight) 360 else -360
    val animation = ObjectAnimator.ofInt(view, ProgressAngleProperty(Int::class.java,"initialAngle") , 0 , finalAngle)
    animation.repeatMode = ValueAnimator.RESTART
    animation.repeatCount = ValueAnimator.INFINITE
    animation.interpolator = LinearInterpolator()

    return animation
}

private class ProgressAngleProperty(type: Class<Int>?, name: String?) : Property<ProgressItem, Int>(type, name) {
    override fun set(`object`: ProgressItem?, value: Int?) {
        `object`?.initialAngle = value?: 0
        `object`?.invalidate()
    }

    override fun get(`object`: ProgressItem?): Int {
        return `object`?.initialAngle?: 0
    }
}

internal fun getRotatedAnimation(animationDuration : Long, rotateRight: Boolean): RelativeRotateAnimation {
    val finalAngle = if (rotateRight) 360 else -360
    val animation = RelativeRotateAnimation(0.toFloat(), finalAngle.toFloat(), Animation.RELATIVE_TO_SELF,
            0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
    animation.duration = animationDuration
    animation.repeatCount = Animation.INFINITE
    animation.repeatMode = Animation.RESTART
    animation.interpolator = LinearInterpolator()
    animation.startTime = Animation.START_ON_FIRST_FRAME.toLong()

    return animation
}