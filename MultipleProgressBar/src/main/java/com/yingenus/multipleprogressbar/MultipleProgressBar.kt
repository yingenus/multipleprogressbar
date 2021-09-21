package com.yingenus.multipleprogressbar

import android.content.Context
import kotlin.ranges.*
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout

public class MultipleProgressBar : FrameLayout, ProgressItem.OrientationChangedObserver {

    private object SavedParams{
        const val animate = "mpb_animate"
        const val animationDuration = "mpb_animationSteed"
        const val progressSize = "mpb_progressSize"
        const val dividerSize = "mpb_dividerSize"
        const val superSP = "super"
    }

    var animate : Boolean = false
        set(value){
            field = value
            updateAnimations()
        }
    var animationDuration : Int    = 3000
        set(value){
            field = value
            if(animate){

                for ( index in 0 until childCount){
                    val view = super.getChildAt(index)

                    if (view is ProgressItem && view.animation != null ){
                        view.animation.duration = field.toLong()
                    }
                }
            }
        }
    var progressSize : Float = 10f
        set(value){
            field = value
            requestUpdate()
        }
    var dividerSize : Float = 5f
        set(value){
            field = value
            requestUpdate()
        }

    private val progressItems = mutableListOf<ProgressItem>()


    constructor(context : Context): super(context){
        init(null)
    }
    constructor(context: Context, attrs : AttributeSet?): super(context, attrs){
        init(attrs)
    }

    constructor(context: Context,attrs: AttributeSet?, defStyleAttr : Int): super(context, attrs, defStyleAttr){
        init(attrs)
    }

    private fun init(attrs: AttributeSet?){
        isSaveEnabled = true
        if (attrs != null){

            val attribute = context.theme.obtainStyledAttributes(attrs,
                R.styleable.MultipleProgressBar,0,0)

            try {
                animate = attribute.getBoolean(R.styleable.MultipleProgressBar_mpb_animate, animate)
                animationDuration = attribute.getInteger(R.styleable.MultipleProgressBar_mpb_animationDuration, animationDuration)
                progressSize = attribute.getDimension(R.styleable.MultipleProgressBar_mpb_progressSize, progressSize)
                dividerSize = attribute.getDimension(R.styleable.MultipleProgressBar_mpb_dividerSize, dividerSize)

            }finally {
                attribute.recycle()
            }
        }
    }

    override fun onChanged(orientation: Int, view: ProgressItem) {
        view.clearAnimation()
        val animation = getRotatedAnimation(animationDuration.toLong(),
                orientation == ProgressItem.Orientation.RIGHT)
        if (progressItems.first().width>0)
            animation.refDiameter = progressItems.first().width
        view.animation = animation
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        findItems()
        invalidateItems()
    }

    private fun findItems(){
        for (index in 0 until super.getChildCount()){
            val child = super.getChildAt(index)
            if ( child is ProgressItem){
                progressItems.add(child)
                child.orientationChangedObserver = this
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        updateAnimations()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        val childCount = super.getChildCount()
        for (child in 0 until childCount){
            super.getChildAt(child).clearAnimation()

        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val childCount = super.getChildCount()

        val paddingLeft = paddingLeft
        val paddingRight = paddingRight
        val paddingTop = paddingTop
        val paddingBottom = paddingBottom

        var minWidth = paddingLeft+paddingRight
        var minHeight = paddingTop+paddingBottom

        if (childCount != 0){
            val requiredSize= (childCount*2)*progressSize.toInt() + (childCount*2 - 1)*dividerSize.toInt()

            minHeight += requiredSize
            minWidth += requiredSize
        }

        val width = View.resolveSizeAndState(minWidth,widthMeasureSpec,0)
        val height = View.resolveSizeAndState(minHeight, heightMeasureSpec,0)

        setMeasuredDimension(width,height)

        for (index in 0 until childCount){
            var childWidth = measuredWidth - paddingLeft - paddingRight - (progressSize.toInt() + dividerSize.toInt())*index*2
            var childHeight = measuredHeight - paddingTop - paddingBottom - (progressSize.toInt() + dividerSize.toInt())*index*2

            if (childHeight != childWidth){
                childHeight = Math.min( childHeight, childWidth)
                childWidth = childHeight
            }

            val childMeasureSpecW = View.MeasureSpec.makeMeasureSpec(Math.max(0,childWidth), MeasureSpec.EXACTLY)
            val childMeasureSpecH = View.MeasureSpec.makeMeasureSpec(Math.max(0,childHeight), MeasureSpec.EXACTLY)

            super.getChildAt(index).measure(childMeasureSpecW,childMeasureSpecH)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val childCount = super.getChildCount()

        for ( index in 0 until childCount){
            val view = super.getChildAt(index)

            if (view.animation != null && view.animation is RelativeRotateAnimation){
                (view.animation as RelativeRotateAnimation).refDiameter = w
            }
        }

    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        layoutChildren(left,top,right,bottom)
    }

    private fun layoutChildren(leftL: Int, topL: Int, rightL: Int, bottomL: Int){
        val childCount = super.getChildCount()

        val left = 0 + paddingLeft
        val right = rightL - leftL - paddingRight
        val top = 0 + paddingTop
        val bottom = bottomL - topL - paddingBottom

        for ( index in 0 until childCount){

            val view = super.getChildAt(index)

            val marginTop = top + ((bottom - top)/2) - (view.measuredHeight / 2)
            val marginLeft = left + ((right - left)/2) - (view.measuredWidth / 2)

            val childLeft = left + marginLeft
            val childRight = childLeft + view.measuredWidth
            val childTop = top + marginTop
            val childButton = childTop + view.measuredHeight

            view.layout(childLeft,childTop,childRight,childButton)
        }
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superPars = super.onSaveInstanceState()
        val bundle = Bundle()
        bundle.putParcelable(SavedParams.superSP,superPars)
        bundle.putBoolean(SavedParams.animate,animate)
        bundle.putInt(SavedParams.animationDuration,animationDuration)
        bundle.putFloat(SavedParams.dividerSize,dividerSize)
        bundle.putFloat(SavedParams.progressSize, progressSize)
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle){
            animate = state.getBoolean(SavedParams.animate)
            animationDuration = state.getInt(SavedParams.animationDuration)
            dividerSize = state.getFloat(SavedParams.dividerSize)
            dividerSize  = state.getFloat(SavedParams.dividerSize)
            super.onRestoreInstanceState(state.getParcelable(SavedParams.superSP))
        }else {
            super.onRestoreInstanceState(state)
        }
    }

    private fun invalidateItems(){
        for( item in progressItems ){
            item.strokeWide = progressSize
        }
    }

    private fun requestUpdate(){
        invalidateItems()
        updateAnimations()
        requestLayout()
        invalidate()
    }

    private fun updateAnimations(){
        val childCount = super.getChildCount()
            var firstChildWidth = -1
            for (child in 0 until childCount){
                if (super.getChildAt(child) is ProgressItem){
                    val pri = super.getChildAt(child) as ProgressItem
                    if (firstChildWidth == -1) firstChildWidth = pri.width
                    if (animate){
                        pri.clearAnimation()
                        val animation = getRotatedAnimation(animationDuration.toLong(),
                                pri.orientation == ProgressItem.Orientation.RIGHT)
                        if (firstChildWidth>0)
                            animation.refDiameter = firstChildWidth
                        pri.animation = animation

                    }else{
                        pri.clearAnimation()
                    }
                }

            }

    }

    fun getProgressItemById(id : Int): ProgressItem?{
        return progressItems.find { it.id == id }
    }

    fun getProgressItemByTag(tag : String): ProgressItem?{
        return progressItems.find { it.TAG != null && it.TAG.equals(tag) }
    }

    fun addProgressItem(progressItem: ProgressItem, tag : String){
        val active = progressItems.find { it.TAG != null && it.TAG.equals(tag) }
        progressItem.TAG = tag
        if (active != null){
            progressItems[progressItems.indexOf(active)] = progressItem
        }
        else{
            progressItems.add(progressItem)
        }
        invalidateItems()
        super.addView(progressItem)
        updateAnimations()
    }

    fun addProgressItem(progressItem: ProgressItem, tag : String, position: Int){
        val active = progressItems.find { it.TAG != null && it.TAG.equals(tag) }

        progressItem.TAG = tag
        progressItem.orientationChangedObserver = this
        if (active != null){
            progressItems.remove(active)
        }
        progressItems.add(position,progressItem)
        invalidateItems()
        super.addView(progressItem,position)
        updateAnimations()
    }

    fun addProgressItem(progressItem: ProgressItem, position: Int){
        progressItems.add(position,progressItem)
        progressItem.orientationChangedObserver = this
        invalidateItems()
        super.addView(progressItem,position)
        updateAnimations()
    }

    fun addProgressItem(progressItem: ProgressItem){
        progressItems.add(progressItem)
        progressItem.orientationChangedObserver = this
        invalidateItems()
        super.addView(progressItem)
        updateAnimations()
    }

    fun removeItemById(id : Int){
        val item =  progressItems.find { it.id == id }
        if (item != null){
            removeItem(progressItems.indexOf(item))
        }
    }
    fun removeItemByTag(tag : String){
        val item = progressItems.find { it.TAG != null && it.TAG.equals(tag) }
        if (item != null){
            removeItem(progressItems.indexOf(item))
        }
    }
    fun removeItem(position: Int){
        if (position < progressItems.size){
            val item = progressItems.removeAt(position)
            item.orientationChangedObserver = null
            item.clearAnimation()
            super.removeView(item)
        }
    }


}