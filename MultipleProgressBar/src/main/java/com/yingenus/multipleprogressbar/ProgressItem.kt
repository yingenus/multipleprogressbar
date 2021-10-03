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
import androidx.core.animation.doOnCancel
import java.lang.RuntimeException


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
            textChanged = true
            requestInvalidate()
        }
    var showLabel = false
        set(value) {
            field = value
            textChanged = true
            requestInvalidate()
        }
    var labelGravity = TEXT_GRAVITY_ADAPTIVE
        set(value) {
            field = max(min(value,2),0)
            textChanged = true
            requestInvalidate()
        }
    var progressText = TEXT_PERCENT
        set(value) {
            field = max(min(value,1),0)
            textChanged = true
            requestInvalidate()
        }
    var showProgressText = false
        set(value) {
            field = value
            textChanged = true
            requestLayout()
        }
    var showSecondaryProgressText = false
        set(value) {
            field = value
            textChanged = true
            requestLayout()
        }
    var progressTextGravity = TEXT_GRAVITY_ADAPTIVE
        set(value) {
            field = max(min(value,2),0)
            textChanged = true
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
            sizeChanged = true
            initPaint()
            requestInvalidate()
        }
    internal var initialAngle : Int = 0
        set(value) {
            field = value
            smtChanged = true
       }

    internal var TAG : String? = null

    internal var currentProgress : Float = 0f
        set(value){
            field = value
            currentProcessAngle = calculateAngle(value)
        }

    internal var currentSecondaryProgress : Float = 0f
        set(value) {
            field = value
            currentSecondaryProgressAngle = calculateAngle(value)
        }

    internal var currentProcessAngle : Float = 0f
        set(value){
            field = value
            crProgressChanged = true
            //smtChanged = true
        }
    internal var currentSecondaryProgressAngle : Float = 0f
        set(value){
            field = value
            crProgressChanged = true
            //smtChanged = true
        }

    internal var orientationChangedObserver : OrientationChangedObserver? = null

    private var rotateAnimation : Animator? = null
        set(value){
            if (field != null)
                field?.cancel()
            field = value
        }
    private var progressAnimation : Animator ? = null
        set(value){
            if (field != null)
                field?.cancel()
            field = value
        }
    private var secondProgressAnimation : Animator? = null
        set(value){
            if (field != null)
                field?.cancel()
            field = value
        }


    var progressColor = ColorStateList.valueOf(Color.BLUE)
        set(value) {
            if (field != value){
                field = value
                updateColors()
                requestInvalidate()
            }else{
                field = value
            }
        }
    var secondaryProgressColor = ColorStateList.valueOf(Color.TRANSPARENT)
        set(value) {
            if (field != value){
                field = value
                updateColors()
                requestInvalidate()
            }else{
                field = value
            }
        }
    var progressTextColor = ColorStateList.valueOf(Color.BLACK)
        set(value) {
            if (field != value){
                field = value
                updateColors()
                requestInvalidate()
            }else{
                field = value
            }
        }
    var labelColor = ColorStateList.valueOf(Color.BLACK)
        set(value) {
            if (field != value){
                field = value
                updateColors()
                requestInvalidate()
            }else{
                field = value
            }
        }

    private var currentProgressColor = Color.BLUE
    private var currentSecondaryProgressColor = Color.TRANSPARENT
    private var currentProgressTextColor = Color.BLACK
    private var currentSecondaryProgressTextColor = Color.BLACK
    private var currentLabelColor = Color.BLACK

    private lateinit var paint : Paint
    private lateinit var textPaint : Paint
    private lateinit var rectF: RectF
    private val textDrawer = TextDrawer()
    private var mBitmap : Bitmap? = null
    private var mCanvas : Canvas? = null
    private var smtChanged = false
    private var sizeChanged = false
    private var crProgressChanged = false
    private var textChanged = false

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
                showProgressText = attribute.getBoolean(R.styleable.MultipleProgressBarItem_mpb_showProgressText,false)
                showSecondaryProgressText = attribute.getBoolean(R.styleable.MultipleProgressBarItem_mpb_showSecondaryProgressText,false)
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

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (rotateAnimation != null){
            rotateAnimation?.cancel()
            rotateAnimation = null
        }
        if (progressAnimation != null){
            progressAnimation?.cancel()
            progressAnimation = null
        }
        if (secondProgressAnimation != null){
            secondProgressAnimation?.cancel()
            secondProgressAnimation = null
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        sizeChanged = true
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (mBitmap == null || smtChanged || textChanged || crProgressChanged || sizeChanged){
            drawBitmap()

            sizeChanged = false
            smtChanged = false
            textChanged = false
            crProgressChanged = false
        }

        canvas!!.drawBitmap(mBitmap!!,0f,0f,null)

    }

    private fun drawBitmap(){

        if (mBitmap == null || sizeChanged){
            val width = Math.max(1, width)
            val height = Math.max(1,height)
            mBitmap?.recycle()
            mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        }else if(smtChanged){
            mBitmap!!.eraseColor(Color.TRANSPARENT)
        }

        val halfStroke = strokeWide/2

        rectF = RectF(0f + halfStroke,0f + halfStroke,width - halfStroke,height - halfStroke)

        mCanvas = Canvas(mBitmap!!)
        mCanvas!!.save()

        val canvas = mCanvas!!

        canvas.drawColor(0x00000000,PorterDuff.Mode.CLEAR)


        val startAngle = applyCorrection(getStartAngle().toFloat())


        //draw secondaryProgress
        val secondAngle =  getExtraSecondProcessAngle()
        val secondaryPath = Path()
        if (secondAngle >= 360f || secondAngle <= -360f)
            secondaryPath.addOval(rectF, Path.Direction.CW)
        else
            secondaryPath.addArc(rectF, startAngle, secondAngle)

        paint.color = currentSecondaryProgressColor
        canvas.drawPath(secondaryPath,paint)

        //draw progress
        val progressAngle = getExtraProcessAngle()
        val progressPath = Path()
        if (progressAngle >= 360f || progressAngle <= -360f)
            progressPath.addOval(rectF, Path.Direction.CW)
        else
            progressPath.addArc(rectF, startAngle, progressAngle)

        paint.color = currentProgressColor
        canvas.drawPath(progressPath,paint)

        if (showLabel || showProgressText || showSecondaryProgressText){
            textDrawer.drawTexts(canvas)
        }

        canvas.restore()
    }

    private sealed class TextPosition{
        class Position (val start : Float, val end : Float): TextPosition()
        object NotShowing : TextPosition()
    }

    private sealed class ShownText(val position: TextPosition, val prefer : Boolean){
        class Label(position: TextPosition, prefer : Boolean): ShownText(position, prefer)
        class Progress(position: TextPosition, prefer : Boolean): ShownText(position, prefer)
        class SProgress(position: TextPosition, prefer : Boolean): ShownText(position, prefer)
    }

    private inner class TextDrawer{
        private var labelPosition : TextPosition? = null
        private var progressPosition : TextPosition? = null
        private var sProgressPosition : TextPosition? = null



        fun drawTexts( canvas : Canvas ){
            if (labelPosition == null || progressPosition == null || sProgressPosition == null || sizeChanged || textChanged || crProgressChanged){
                calculatePositions()
            }

            if (labelPosition != TextPosition.NotShowing ||
                    progressPosition != TextPosition.NotShowing ||
                    sProgressPosition != TextPosition.NotShowing){

                val halfStroke = strokeWide/2
                val defTextBound : Rect = Rect()
                textPaint.getTextBounds("text", 0, labelText.length, defTextBound)

                val stOffset = halfStroke - defTextBound.height()/2
                val counterOffset = strokeWide - stOffset

                val rect_270_90 = RectF(stOffset,stOffset,width - stOffset,height - stOffset)
                val rect_90_270 = RectF(counterOffset,counterOffset,width - counterOffset,height - counterOffset)

                fun draw(position : TextPosition.Position,@ColorInt color: Int, text : String){
                    val counter = Path()
                    val start = getStartAngle()+position.start
                    val end =   getStartAngle()+position.end
                    val center = applyCorrection((end - start)/2 + start)

                    if (center <= 0 || center > 180){
                        counter.addArc(rect_90_270, applyCorrection(start), position.end - position.start)
                    }else{
                        counter.addArc(rect_270_90, applyCorrection(end), position.start - position.end)
                    }
                    textPaint.color = color
                    canvas.drawTextOnPath(text.toCharArray(), 0, text.length, counter, 0f,0f,textPaint)
                }

                if( labelPosition is TextPosition.Position){
                    draw(labelPosition as TextPosition.Position, currentLabelColor, labelText)
                }
                if( progressPosition is TextPosition.Position){
                    draw(progressPosition as TextPosition.Position, currentProgressTextColor, getProgressText())
                }
                if( sProgressPosition is TextPosition.Position){
                    draw(sProgressPosition as TextPosition.Position, currentSecondaryProgressTextColor, getSecondProgressText())
                }
            }

        }

        private fun calculatePositions(){
            val halfStroke = strokeWide/2
            val defTextBound : Rect = Rect()
            textPaint.getTextBounds("text", 0, labelText.length, defTextBound)

            val stOffset = halfStroke - defTextBound.height()/2
            val counterOffset = strokeWide - stOffset

            val diameter = width - counterOffset*2
            val circleLength = diameter * Math.PI.toFloat()


            fun suggestPositions( text : String, leftFree : Float, rightFree : Float, gravity : Int, showLabel : Boolean, zeroPosition : Float) : List<TextPosition>{
                if (showLabel){

                    val bound : Rect = Rect()
                    textPaint.getTextBounds(text, 0, text.length, bound)

                    val requireWight = bound.width()

                    fun calculate(suggestGravity : Int) =  when {
                        requireWight == 0 -> {
                            TextPosition.NotShowing
                        }
                        suggestGravity == TEXT_GRAVITY_LEFT && leftFree > requireWight -> {
                            val roundingOffset = ((360 * (strokeWide/2)) / circleLength)
                            val endAng = ((360 * requireWight) / circleLength)
                            if( orientation == ORIENTATION_RIGHT){
                                val endAng = - endAng
                                TextPosition.Position(zeroPosition-roundingOffset + endAng, zeroPosition)
                            }else{
                                val end = zeroPosition + endAng + roundingOffset
                                if (end >= 360) TextPosition.NotShowing
                                else TextPosition.Position(zeroPosition, end)
                            }
                        }
                        suggestGravity == TEXT_GRAVITY_RIGHT && rightFree > requireWight -> {
                            val roundingOffset = if(zeroPosition != 0f)((360 * (strokeWide/2)) / circleLength) else 0f
                            val endAng = ((360 * requireWight) / circleLength)
                            if (orientation == ORIENTATION_RIGHT){
                                val end = zeroPosition + endAng + roundingOffset
                                if (end >= 360) TextPosition.NotShowing
                                else TextPosition.Position(zeroPosition+roundingOffset, end)
                            }else{
                                val endAng = - endAng
                                TextPosition.Position(zeroPosition + endAng, zeroPosition)
                            }
                        }
                        else -> {
                            TextPosition.NotShowing
                        }
                    }

                    return if (gravity == TEXT_GRAVITY_ADAPTIVE) {
                        listOf(calculate(TEXT_GRAVITY_RIGHT),calculate(TEXT_GRAVITY_LEFT))
                    } else {
                        listOf(calculate(gravity))
                    }

                }else{
                    return listOf(TextPosition.NotShowing)
                }
            }

            val positions = mutableListOf<ShownText>()
            positions.addAll(suggestPositions(
                    text = labelText,
                    leftFree = circleLength * ((360 - (max(currentProcessAngle,currentSecondaryProgressAngle).toFloat())) / 360),
                    rightFree = circleLength * ( max(currentProcessAngle,currentSecondaryProgressAngle).toFloat() / 360),
                    gravity = labelGravity,
                    showLabel = showLabel,
                    zeroPosition = 0f).mapIndexed { index, textPosition -> ShownText.Label(textPosition, (index == 0 && textPosition != TextPosition.NotShowing)) })
            positions.addAll(suggestPositions(
                    text = getProgressText(),
                    leftFree = circleLength * ((currentProcessAngle.toFloat()) / 360),
                    rightFree = circleLength * ((360 - currentProcessAngle.toFloat()) / 360),
                    gravity = progressTextGravity,
                    showLabel = showProgressText,
                    zeroPosition = currentProcessAngle.toFloat())
                    .mapIndexed { index, textPosition -> ShownText.Progress(textPosition, (index == 0 && textPosition != TextPosition.NotShowing)) })
            positions.addAll(suggestPositions(
                    text = getSecondProgressText(),
                    leftFree = if(currentSecondaryProgressAngle > currentProcessAngle)
                        circleLength * ((currentSecondaryProgressAngle - currentProcessAngle).toFloat() / 360)
                    else
                        0f,
                    rightFree = if(currentSecondaryProgressAngle > currentProcessAngle)
                        circleLength * ((360 - currentSecondaryProgressAngle.toFloat()) / 360)
                    else
                        0f,
                    gravity = progressTextGravity,
                    showLabel = showSecondaryProgressText,
                    zeroPosition = currentSecondaryProgressAngle.toFloat())
                    .mapIndexed { index, textPosition -> ShownText.SProgress(textPosition, (index == 0 && textPosition != TextPosition.NotShowing)) })

            val composed = composeText(positions)

            val labels = composed.filterIsInstance<ShownText.Label>()
            labelPosition = if (labels.isNotEmpty()){
                val prefer = labels.find { it.prefer }
                prefer?.position ?: labels[0].position
            }else{
                TextPosition.NotShowing
            }
            val progress = composed.filterIsInstance<ShownText.Progress>()
            progressPosition = if (progress.isNotEmpty()){
                val prefer = progress.find { it.prefer }
                prefer?.position ?: progress[0].position
            }else{
                TextPosition.NotShowing
            }
            val sProgress = composed.filterIsInstance<ShownText.SProgress>()
            sProgressPosition = if (sProgress.isNotEmpty()){
                val prefer = sProgress.find { it.prefer }
                prefer?.position ?: sProgress[0].position
            }else{
                TextPosition.NotShowing
            }


        }

        fun composeText( texts : List<ShownText>): List<ShownText>{
            val items = texts.filter { it.position != TextPosition.NotShowing }

            if (items.size > 1){
                items.forEachIndexed { index, item ->
                    val itemPosition = item.position as TextPosition.Position
                    for ( i in 0.until(index)){
                        val conflictPosition = items[i].position as TextPosition.Position
                        if ( (conflictPosition.end > itemPosition.end && itemPosition.end > conflictPosition.start) ||
                                (conflictPosition.end > itemPosition.start && itemPosition.start > conflictPosition.start)  ){
                                    val variant1 = composeText(items.toMutableList().apply { remove(item) })
                                    val variant2 = composeText(items.toMutableList().apply { remove(items[i]) })

                                    val size1 = variant1.groupBy { it::class.java.name }.keys.size
                                    val size2 = variant2.groupBy { it::class.java.name }.keys.size

                                    return if (size1 > size2){
                                        variant1
                                    } else if (size2 > size1){
                                        variant2
                                    }else{
                                        val pref1 = variant1.fold(0){ass, item -> if (item.prefer) ass + 1 else ass}
                                        val pref2 = variant2.fold(0){ass, item -> if (item.prefer) ass + 1 else ass}
                                        if (pref1 >= pref2) variant1
                                        else variant2
                            }
                        }
                    }
                }
            }
            return items
        }
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()
        updateColors()
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)

        if (visibility != View.VISIBLE){
            rotateAnimation?.cancel()
            progressAnimation?.cancel()
            progressAnimation = null
            secondProgressAnimation?.cancel()
            secondProgressAnimation = null
        }else{

            rotateAnimation?.start()
        }
    }

    private fun initPaint(){
        paint = Paint()
        paint.strokeJoin = Paint.Join.ROUND
        paint.style = Paint.Style.STROKE
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeWidth = strokeWide
        paint.isAntiAlias = true
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
        return initialAngle
    }

    private fun getExtraProcessAngle(): Float{
        if (orientation == Orientation.RIGHT)
            return currentProcessAngle
        else{
            return -currentProcessAngle
        }
    }
    private fun getExtraSecondProcessAngle(): Float{
        if (orientation == Orientation.RIGHT)
            return currentSecondaryProgressAngle
        else{
            return -currentSecondaryProgressAngle
        }
    }

    private fun limitProgress(progress: Int) = Math.max(min,Math.min(max,progress))

    private fun calculateAngle(progress: Float) =
            if (max == min ) 360f
            else  ((progress)/(max - min))*360

    private fun getProgressText(): String{
        return if (progressText == TEXT_PERCENT){
            "${(if (min == max) "100" else ((progress.toFloat()/(max - min)) * 100).toInt().toString())}%"
        }else if (progressText == TEXT_RAF){
            progress.toString()
        }else{
            throw RuntimeException( "invalid state of progressText")
        }
    }

    private fun getSecondProgressText(): String{
        return if (progressText == TEXT_PERCENT){
           "${(if (min == max) "100" else ((secondaryProgress.toFloat()/(max - min)) * 100).toInt().toString())}%"
        }else if (progressText == TEXT_RAF){
            secondaryProgress.toString()
        }else{
            throw RuntimeException( "invalid state of progressText")
        }
    }

    internal fun cancelRotateAnimation(){
        if (rotateAnimation != null && rotateAnimation!!.isRunning){
            rotateAnimation!!.doOnCancel {
                initialAngle = 0
                invalidate()
            }
            rotateAnimation!!.cancel()
        }
        rotateAnimation = null
    }

    internal fun runRotateAnimation(duration : Long, parentWidth : Int){
        rotateAnimation = getRotatedAnimator( orientation == Orientation.RIGHT, this)
        rotateAnimation!!.duration = ringDuration(parentWidth,width, duration)
        rotateAnimation!!.start()
    }

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
            progressAnimation = null
            currentProgress = progress.toFloat()
            requestInvalidate()
        }else{
            val animator = getValueAnimator(this,ProgressItem::currentProgress,currentProgress,this.progress.toFloat())
            progressAnimation = animator
            progressAnimation?.start()
        }
    }

    fun getProgress() = progress

    fun setSecondaryProgress(progress : Int){
        setSecondaryProgress(progress,true)
    }
    fun setSecondaryProgress(progress : Int, animate : Boolean){
        this.secondaryProgress = limitProgress(progress)
        if (!animate){
            secondProgressAnimation = null
            currentSecondaryProgress = progress.toFloat()
            requestInvalidate()
        }else{
            val animator = getValueAnimator(this,ProgressItem::currentSecondaryProgress,currentSecondaryProgress,this.secondaryProgress.toFloat())
            secondProgressAnimation = animator
            secondProgressAnimation?.start()
        }
    }
    fun getSecondaryProgress() = secondaryProgress


}