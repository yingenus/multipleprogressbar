package com.example.multipleprogressbar

import android.content.res.ColorStateList
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.core.view.children
import com.yingenus.multipleprogressbar.MultipleProgressBar
import com.yingenus.multipleprogressbar.ProgressItem

class MainActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener, View.OnClickListener{

    private val progressBar : MultipleProgressBar by lazy { findViewById<MultipleProgressBar>(R.id.test_multipleLineProgressBar) }

    private val addedTags = mutableListOf<String>()

    private val progressSizeEd : EditText by lazy { findViewById<EditText>(R.id.editTextNumber) }
    private val dividerSizeEd : EditText by lazy { findViewById<EditText>(R.id.editTextNumber2) }
    private val progressEd : EditText by lazy { findViewById<EditText>(R.id.editTextNumber3) }
    private val secProgressEd : EditText by lazy { findViewById<EditText>(R.id.editTextNumber4) }

    lateinit var colors : Array<Int>
    lateinit var secondColors : Array<Int>
    lateinit var colorSelectors : Array<ColorStateList>
    lateinit var secondColorSelectors : Array<ColorStateList>



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<SeekBar>(R.id.seekBar4).setOnSeekBarChangeListener(this)
        findViewById<SeekBar>(R.id.seekBar3).setOnSeekBarChangeListener(this)
        findViewById<SeekBar>(R.id.seekBar2).setOnSeekBarChangeListener(this)
        findViewById<SeekBar>(R.id.seekBar).setOnSeekBarChangeListener(this)
        findViewById<SeekBar>(R.id.seekBar5).setOnSeekBarChangeListener(this)

        findViewById<Button>(R.id.button_start).setOnClickListener(this)
        findViewById<Button>(R.id.button_add).setOnClickListener(this)
        findViewById<Button>(R.id.button_del).setOnClickListener(this)

        findViewById<Switch>(R.id.enableSwitch1).setOnCheckedChangeListener {  _ , checked->
            val childs = progressBar.children
            for (child in childs){
                if (child is ProgressItem){
                    child.isEnabled = checked
                }
            }
        }

        findViewById<Switch>(R.id.showSwitch1).setOnCheckedChangeListener { _, checked ->
            val childs = progressBar.children
            for (child in childs){
                if (child is ProgressItem){
                    child.showLabel = checked
                    child.showSecondaryProgressText = checked
                    child.showProgressText = checked
                }
            }
        }

        progressSizeEd.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                if(!s.isNullOrEmpty())
                    progressBar.progressSize = dp2pix(applicationContext,s.toString().toInt()).toFloat()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })
        dividerSizeEd.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                if(!s.isNullOrEmpty())
                    progressBar.dividerSize = dp2pix(applicationContext,s.toString().toInt()).toFloat()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })
        progressEd.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                if(!s.isNullOrEmpty()){
                    val progress = s.toString().toInt()
                    progressBar.getProgressItemById(R.id.item1)!!.setProgress(progress)
                    progressBar.getProgressItemById(R.id.item2)!!.setProgress(progress)
                    for(tag in addedTags)
                        progressBar.getProgressItemByTag(tag)?.setProgress(progress)
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })
        secProgressEd.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                if(!s.isNullOrEmpty()){
                    val progress = s.toString().toInt()
                    progressBar.getProgressItemById(R.id.item1)!!.setSecondaryProgress(progress)
                    progressBar.getProgressItemById(R.id.item2)!!.setSecondaryProgress(progress)
                    for(tag in addedTags)
                        progressBar.getProgressItemByTag(tag)?.setSecondaryProgress(progress)
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })




        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val colors = mutableListOf<ColorStateList>()
            val second = mutableListOf<ColorStateList>()
            colors.add(applicationContext.getColorStateList(R.color.color1))
            second.add(applicationContext.getColorStateList(R.color.colorsecond1))
            colors.add(applicationContext.getColorStateList(R.color.color2))
            second.add(applicationContext.getColorStateList(R.color.colorsecond2))
            colors.add(applicationContext.getColorStateList(R.color.color3))
            second.add(applicationContext.getColorStateList(R.color.colorsecond3))
            colors.add(applicationContext.getColorStateList(R.color.color4))
            second.add(applicationContext.getColorStateList(R.color.colorsecond4))
            colors.add(applicationContext.getColorStateList(R.color.color5))
            second.add(applicationContext.getColorStateList(R.color.colorsecond5))
            colorSelectors = colors.toTypedArray()
            secondColorSelectors = second.toTypedArray()
        }else{
            val colorsList = mutableListOf<Int>()
            val secondList = mutableListOf<Int>()
            colorsList.add(applicationContext.resources.getColor(R.color.indicator_1))
            secondList.add(applicationContext.resources.getColor(R.color.indicator_11))
            colorsList.add(applicationContext.resources.getColor(R.color.indicator_2))
            secondList.add(applicationContext.resources.getColor(R.color.indicator_21))
            colorsList.add(applicationContext.resources.getColor(R.color.indicator_3))
            secondList.add(applicationContext.resources.getColor(R.color.indicator_31))
            colorsList.add(applicationContext.resources.getColor(R.color.indicator_4))
            secondList.add(applicationContext.resources.getColor(R.color.indicator_41))
            colorsList.add(applicationContext.resources.getColor(R.color.indicator_5))
            secondList.add(applicationContext.resources.getColor(R.color.indicator_51))
            colors = colorsList.toTypedArray()
            secondColors = secondList.toTypedArray()
        }

    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.button_start)
            progressBar.animate = !progressBar.animate
        if (v?.id == R.id.button_add)
            addItem()
        if (v?.id == R.id.button_del)
            deleteItem()
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        when(seekBar?.id){
            R.id.seekBar ->{
                progressBar.progressSize = dp2pix(applicationContext,progress).toFloat()
            }
            R.id.seekBar2 ->{
                progressBar.dividerSize = dp2pix(applicationContext,progress).toFloat()
            }
            R.id.seekBar3 ->{
                progressBar.getProgressItemById(R.id.item1)!!.setProgress(progress,false)
                progressBar.getProgressItemById(R.id.item2)!!.setProgress(progress,false)
                for(tag in addedTags)
                    progressBar.getProgressItemByTag(tag)?.setProgress(progress,false)
            }
            R.id.seekBar4 ->{
                progressBar.getProgressItemById(R.id.item1)!!.setSecondaryProgress(progress,false)
                progressBar.getProgressItemById(R.id.item2)!!.setSecondaryProgress(progress,false)
                for(tag in addedTags)
                    progressBar.getProgressItemByTag(tag)?.setSecondaryProgress(progress,false)
            }
            R.id.seekBar5 ->{
                progressBar.animationDuration = progress
            }
        }
    }



    override fun onStartTrackingTouch(seekBar: SeekBar?) {}
    override fun onStopTrackingTouch(seekBar: SeekBar?) {}

    private fun addItem(){
        val tag = "TAG ${addedTags.size+1}"
        addedTags.add(tag)
        progressBar.addProgressItem(getProgressItem(),tag)
    }


    private fun deleteItem(){
        if (addedTags.isNotEmpty())
            progressBar.removeItemByTag(addedTags.removeAt(addedTags.lastIndex))
    }




    private fun getProgressItem(): ProgressItem {
        val pr = ProgressItem(applicationContext)
        val lastItem = progressBar.childCount
        val newIndex = lastItem % 5
        pr.setProgress(50,false)
        pr.setProgressTextColorRes(R.color.textcolor)
        pr.setSecondaryProgressTextColorRes(R.color.textcolor)
        pr.setLabelColorRes(R.color.textcolor)
        pr.labelText = "test"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (colorSelectors.size > newIndex && secondColorSelectors.size > newIndex){
                pr.progressColor = colorSelectors[newIndex]
                pr.secondaryProgressColor = secondColorSelectors[newIndex]
            }
        }else{
            if (colors.size > newIndex && secondColors.size > newIndex){
                pr.setProgressColor(colors[newIndex])
                pr.setSecondaryProgressColor(secondColors[newIndex])
            }
        }
        return pr
    }
}