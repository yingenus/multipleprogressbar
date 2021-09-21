package com.yingenus.multipleprogressbar

import android.animation.Animator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.*
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min


public class ProgressItem : View{

    companion object{
        const val ORIENTATION_RIGHT = 0
        const val ORIENTATION_LEFT = 1
        const val TEXT_GRAVITY_RIGHT = 2
        const val TEXT_GRAVITY_LEFT = 1
        const val TEXT_GRAVITY_ADAPTIVE = 0
        const val TEXT_PERCENT = 0
        const val TEXT_RAF = 1
    }

    object Orientation{
        const val RIGHT = 0
        const val LEFT = 1
    }

    object TextGravity{
        const val RIGHT = 2
        const val LEFT = 1
        const val ADAPTIVE = 0
    }

    private object SavedParams{
        const val max = "mpb_max"
        const val min = "mpb_min"
        const val progress = "mpb_progress"
        const val secondaryProgress = "mpb_secondaryProgress"
        const val orientation = "mpb_orientation"
        const val superSP = "super"
    }

    interface OrientationChangedObserver{
        fun onChanged( orientation : Int, view: ProgressItem)
    }

    var max: Int = 100
        set(value){
            field = value
            progress = progress
            secondaryProgress = secondaryProgress
            requestInvalidate()
        }
    var min: Int = 0
        set(value){
            field = value
            progress = progress
            secondaryProgress = secondaryProgress
            requestInvalidate()
        }
    var labelText = ""
        set(value) {
            field = value
            requestInvalidate()
        }
    var showLabel = false
        set(value) {
            field = value
            requestInvalidate()
        }
    var labelGravity = TextGravity.ADAPTIVE
        set(value) {
            field = max(min(value,2),0)
            requestInvalidate()
        }
    var progressText = TEXT_PERCENT
        set(value) {
            field = max(min(value,1),0)
            requestInvalidate()
        }
    var showProgressText = false
        set(value) {
            field = value
            requestLayout()
        }
    var progressTextGravity = TextGravity.ADAPTIVE
        set(value) {
            field = max(min(value,2),0)
            requestInvalidate()
        }
    private var progress: Int = 0
    private var secondaryProgress : Int = 0
    var orientation : Int = Orientation.RIGHT
        set(value) {
            field = if (value <=0)
                Orientation.RIGHT
            else
                Orientation.LEFT

            orientationChangedObserver?.onChanged(field,this)

            requestInvalidate()
        }
    internal var strokeWide : Float = 10f
        set(value) {
            field = value
            initPaint()
            requestInvalidate()
        }
    internal var initialAngle : Int = 0
        set(value) {
            field = value
            requestInvalidate()
       }

    internal var TAG : String? = null

    internal var currentProcessAngle : Int = 0
        set(value){
            field = value
            smtChanged = true
        }
    internal var currentSecondaryProgressAngle : Int = 0
        set(value){
            field = value
            smtChanged = true
        }

    internal var orientationChangedObserver : OrientationChangedObserver? = null

    private var activeAnimation : Animator? = null
        set(value){
            if (field != null && field!!.isRunning)
                field?.cancel()
            field = value
        }


    var progressColor = ColorStateList.valueOf(Color.BLUE)
        set(value) {
            if (field != value){
                field = value
                updateColors()
            }else{
                field = value
            }
        }
    var secondaryProgressColor = ColorStateList.valueOf(Color.TRANSPARENT)
        set(value) {
            if (field != value){
                field = value
                updateColors()
            }else{
                field = value
            }
        }
    var progressTextColor = ColorStateList.valueOf(Color.BLACK)
        set(value) {
            if (field != value){
                field = value
                updateColors()
            }else{
                field = value
            }
        }
    var labelColor = ColorStateList.valueOf(Color.BLACK)
        set(value) {
            if (field != value){
                field = value
                updateColors()
            }else{
                field = value
            }
        }

    private var currentProgressColor = Color.BLUE
    private var currentSecondaryProgressColor = Color.TRANSPARENT
    private var currentProgressTextColor = Color.BLACK
    private var currentLabelColor = Color.BLACK

    private lateinit var paint : Paint
    private lateinit var labelPaint : Paint
    private lateinit var textPaint : Paint
    private lateinit var rectF: RectF
    private var mBitmap : Bitmap? = null
    private var mCanvas : Canvas? = null
    private var smtChanged = false

    constructor(context: Context): super(context){
        init(null)
    }
    constructor(context: Context, attrs : AttributeSet?): super(context, attrs){
        init(attrs)
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr : Int): super(context, attrs, defStyleAttr){
        init(attrs)
    }

    private fun init(attrs: AttributeSet?){
        isSaveEnabled = true

        if (attrs != null){
            val attribute = context.theme.obtainStyledAttributes(attrs,
                R.styleable.MultipleProgressBarItem,0,0)
            try{
                max = attribute.getInteger(R.styleable.MultipleProgressBarItem_mpb_max,max)
                min = attribute.getInteger(R.styleable.MultipleProgressBarItem_mpb_min,min)

                setProgress(attribute.getInteger(R.styleable.MultipleProgressBarItem_mpb_progress, progress), false)
                setSecondaryProgress(attribute.getInteger(R.styleable.MultipleProgressBarItem_mpb_secondaryProgress,secondaryProgress), false)

                progressColor = attribute.getStateList(R.styleable.MultipleProgressBarItem_mpb_progressColor,progressColor)
                secondaryProgressColor = attribute.getStateList(R.styleable.MultipleProgressBarItem_mpb_secondaryProgressColor,secondaryProgressColor)

                labelText = attribute.getString(R.styleable.MultipleProgressBarItem_mpb_labelText)?:labelText
                showLabel = attribute.getBoolean(R.styleable.MultipleProgressBarItem_mpb_showLabel, false)
                labelGravity = attribute.getInteger(R.styleable.MultipleProgressBarItem_mpb_labelGravity, labelGravity)
                labelColor = attribute.getStateList(R.styleable.MultipleProgressBarItem_mpb_labelColor,labelColor)

                progressText = attribute.getInteger(R.styleable.MultipleProgressBarItem_mpb_progressText,progressText)
                progressColor = attribute.getStateList(R.styleable.MultipleProgressBarItem_mpb_progressColor,progressColor)
                showProgressText = attribute.getBoolean(R.styleable.MultipleProgressBarItem_mpb_showProgressText,false)
                progressTextGravity = attribute.getInteger(R.styleable.MultipleProgressBarItem_mpb_ProgressTextGravity,progressTextGravity)

                orientation = attribute.getInteger(R.styleable.MultipleProgressBarItem_mpb_orientation, orientation)
            }finally {
                attribute.recycle()
            }
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        initPaint()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = View.MeasureSpec.getSize(widthMeasureSpec)
        val height = View.MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(width,height)
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superPars = super.onSaveInstanceState()
        val bundle = Bundle()
        bundle.putParcelable(SavedParams.superSP,superPars)
        bundle.putInt(SavedParams.max,max)
        bundle.putInt(SavedParams.min,min)
        bundle.putInt(SavedParams.progress,progress)
        bundle.putInt(SavedParams.secondaryProgress,progress)
        bundle.putInt(SavedParams.orientation, orientation)
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle){
            max = state.getInt(SavedParams.max)
            min = state.getInt(SavedParams.min)
            setProgress(state.getInt(SavedParams.progress),false)
            setSecondaryProgress(state.getInt(SavedParams.secondaryProgress),false)
            orientation = state.getInt(SavedParams.orientation)
            super.onRestoreInstanceState(state.getParcelable(SavedParams.superSP))
        }else {
            super.onRestoreInstanceState(state)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        smtChanged = true
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (mBitmap == null || smtChanged){
            drawBitmap()
        }

        canvas!!.drawBitmap(mBitmap!!,0f,0f,null)

    }

    private fun drawBitmap(){

        if (mBitmap == null || smtChanged){
            val width = Math.max(1, width)
            val height = Math.max(1,height)
            try {
                mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            }catch (e : Exception){

            }
        }

        val halfStroke = strokeWide/2

        rectF = RectF(0f + halfStroke,0f + halfStroke,width - halfStroke,height - halfStroke)

        mCanvas = Canvas(mBitmap!!)
        mCanvas!!.save()

        val canvas = mCanvas!!

        canvas.drawColor(0x00000000,PorterDuff.Mode.CLEAR)


        //draw secondaryProgress
        val secondaryPath = Path()
        secondaryPath.addArc(rectF, applyCorrection(getStartAngle().toFloat()),getExtraSecondProcessAngle().toFloat())

        paint.color = currentSecondaryProgressColor
        canvas.drawPath(secondaryPath,paint)

        //draw progress
        val progressPath = Path()
        progressPath.addArc(rectF, applyCorrection(getStartAngle().toFloat()),getExtraProcessAngle().toFloat())

        paint.color = currentProgressColor
        canvas.drawPath(progressPath,paint)

        //draw progress text
        if (showProgressText){
            drawProgressText(canvas)
        }

        //draw label
        if (showLabel){
            drawLabelText(canvas)
        }

        canvas.restore()
    }

    private fun drawLabelText(canvas: Canvas){
        val start = getStartAngle().toFloat()
        val end = max(getExtraSecondProcessAngle(), getExtraProcessAngle()).toFloat()

        val bound : Rect = Rect()
        labelPaint.getTextBounds(labelText, 0, labelText.length, bound)

        val halfStroke = strokeWide/2
        val stOffset = halfStroke - bound.height()/2
        val counterOffset = strokeWide - stOffset

        val diameter = width - counterOffset*2
        val circleLength = diameter * Math.PI

        val leftFree = circleLength * ((360 - end) / 360)
        val rightFree = circleLength * (end / 360)


        val requireWight = bound.width()

        val gravity = if (labelGravity == TEXT_GRAVITY_ADAPTIVE) {
            when {
                rightFree > requireWight -> {
                    TEXT_GRAVITY_RIGHT
                }
                leftFree > requireWight -> {
                    TEXT_GRAVITY_LEFT
                }
                else -> labelGravity
            }
        } else {
            labelGravity}

        val startAng : Float
        val endAng : Float
        val roundingOffset : Float

        when {
            requireWight == 0 -> {
                startAng = 0f
                endAng = 0f
                roundingOffset = 0f
            }
            gravity == TEXT_GRAVITY_LEFT && leftFree > requireWight -> {
                startAng = 360 - ((360 * requireWight) / circleLength).toFloat()
                endAng = 360f
                roundingOffset = ((360 * strokeWide.toFloat()/2) / circleLength).toFloat()
            }
            gravity == TEXT_GRAVITY_RIGHT && rightFree > requireWight -> {
                startAng = 0f
                endAng = ((360 * requireWight) / circleLength).toFloat()
                roundingOffset = 0f
            }
            else -> {
                startAng = 0f
                endAng = 0f
                roundingOffset = 0f
            }
        }



        //val halfStroke = strokeWide/2
        //val stOffset = halfStroke - bound.height()/2
        //val counterOffset = strokeWide - stOffset

        val rect = RectF(counterOffset,counterOffset,width - counterOffset,height - counterOffset)
        val counter = Path()

        counter.addArc(rect, applyCorrection(startAng-roundingOffset), applyCorrection(endAng-roundingOffset))

        labelPaint.color = currentLabelColor

        canvas.drawTextOnPath(labelText.toCharArray(), 0, labelText.length, counter, 0f,0f,labelPaint)
    }

    private fun drawProgressText(canvas: Canvas){
        val start = getStartAngle()
        val end = getExtraProcessAngle()

        val diameter = width - strokeWide
        val circleLength = diameter * Math.PI

        val leftFree = circleLength * (abs(end - start) / 360)
        val rightFree = circleLength * (abs(start - end) / 360)

        val tempText = "100"

        val bound : Rect = Rect()
        textPaint.getTextBounds(tempText, 0, tempText.length, bound)

        val requireWight = bound.width()

        val gravity = if (labelGravity == TEXT_GRAVITY_ADAPTIVE) {
            when {
                rightFree > requireWight -> {
                    TEXT_GRAVITY_RIGHT
                }
                leftFree > requireWight -> {
                    TEXT_GRAVITY_LEFT
                }
                else -> labelGravity
            }
        } else {
            labelGravity}

        val startAng : Int
        val endAng : Int

        when {
            requireWight == 0 -> {
                startAng = 0
                endAng = 0
            }
            labelGravity == TEXT_GRAVITY_LEFT && leftFree > requireWight -> {
                startAng = 360 - ((360 * requireWight) / circleLength) as Int
                endAng = 360
            }
            labelGravity == TEXT_GRAVITY_RIGHT && rightFree > requireWight -> {
                startAng = 0
                endAng = ((360 * requireWight) / circleLength) as Int
            }
            else -> {
                startAng = 0
                endAng = 0
            }
        }


        val halfStroke = strokeWide/2
        val rect = RectF(0f + halfStroke,0f + halfStroke,width - halfStroke,height - halfStroke)
        val counter = Path()

        counter.addArc(rect, startAng.toFloat(), endAng.toFloat())

        textPaint.color = currentProgressTextColor

        canvas.drawTextOnPath(tempText.toCharArray(), 0, tempText.length, counter, 0f,0f,textPaint)
    }



    override fun drawableStateChanged() {
        super.drawableStateChanged()
        updateColors()
    }

    private fun initPaint(){
        paint = Paint()
        paint.strokeJoin = Paint.Join.ROUND
        paint.style = Paint.Style.STROKE
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeWidth = strokeWide
        paint.isAntiAlias = true
        labelPaint = TextPaint()
        labelPaint.textSize = strokeWide
        labelPaint.isAntiAlias = true
        labelPaint.isSubpixelText = true
        textPaint = TextPaint()
        textPaint.textSize = strokeWide
        textPaint.isAntiAlias = true
        textPaint.isSubpixelText = true

    }

    private fun updateColors(){
        val newProcessColor = progressColor.getColorForState(drawableState,Color.TRANSPARENT)
        val newSecondaryProgressColor = secondaryProgressColor.getColorForState(drawableState,Color.TRANSPARENT)
        val newProgressTextColor = progressTextColor.getColorForState(drawableState,Color.TRANSPARENT)
        val newLabelColor = labelColor.getColorForState(drawableState,Color.TRANSPARENT)

        if (newProcessColor != currentProgressColor ||
                newSecondaryProgressColor != currentSecondaryProgressColor ||
                newProgressTextColor != currentProgressTextColor ||
                newLabelColor != currentLabelColor) {
            smtChanged = true
        }

        currentProgressColor = newProcessColor
        currentProgressTextColor = newProgressTextColor
        currentLabelColor = newLabelColor
        currentSecondaryProgressColor = newSecondaryProgressColor
    }

    private fun requestInvalidate(){
        smtChanged = true
        invalidate()
    }

    private fun getStartAngle(): Int{
        if (orientation == Orientation.RIGHT)
            return initialAngle
        else{
            return - initialAngle
        }
    }

    private fun getExtraProcessAngle(): Int{
        if (orientation == Orientation.RIGHT)
            return currentProcessAngle
        else{
            return -currentProcessAngle
        }
    }
    private fun getExtraSecondProcessAngle(): Int{
        if (orientation == Orientation.RIGHT)
            return currentSecondaryProgressAngle
        else{
            return -currentSecondaryProgressAngle
        }
    }

    private fun limitProgress(progress: Int) = Math.max(min,Math.min(max,progress))

    private fun calculateAngle(progress: Int) = ((progress.toDouble()/(max - min))*360).toInt()


    fun setProgressColor(@ColorInt color : Int){
        progressColor = ColorStateList.valueOf(color)
    }

    fun setProgressColorRes(@ColorRes id : Int){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            progressColor = context.resources.getColorStateList(id,context.theme)
        }else{
            val color = context.resources.getColor(id)
            setProgressColor(color)
        }
    }

    fun setSecondaryProgressColor(@ColorInt color : Int){
        secondaryProgressColor = ColorStateList.valueOf(color)
    }

    fun setSecondaryProgressColorRes(@ColorRes id : Int){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            secondaryProgressColor = context.resources.getColorStateList(id,context.theme)
        }else{
            val color = context.resources.getColor(id)
            setSecondaryProgressColor(color)
        }
    }

    fun setProgress(progress : Int){
        setProgress(progress,true)
    }
    fun setProgress(progress : Int, animate : Boolean){
        this.progress = limitProgress(progress)
        if (!animate){
            activeAnimation = null
            currentProcessAngle = calculateAngle(this.progress)
            requestInvalidate()
        }else{
            val animator = getValueAnimator(this,ProgressItem::currentProcessAngle,currentProcessAngle,calculateAngle(this.progress))
            activeAnimation = animator
            activeAnimation?.start()
        }
    }

    fun getProgress() = progress

    fun setSecondaryProgress(progress : Int){
        setSecondaryProgress(progress,true)
    }
    fun setSecondaryProgress(progress : Int, animate : Boolean){
        this.secondaryProgress = limitProgress(progress)
        if (!animate){
            activeAnimation = null
            currentSecondaryProgressAngle = calculateAngle(secondaryProgress)
            requestInvalidate()
        }else{
            val animator = getValueAnimator(this,ProgressItem::currentSecondaryProgressAngle,currentSecondaryProgressAngle,calculateAngle(secondaryProgress))
            activeAnimation = animator
            activeAnimation?.start()
        }
    }
    fun getSecondaryProgress() = secondaryProgress


}