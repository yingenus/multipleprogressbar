
## MultipleProgressBar

round progress bar with many elements

## Kotlin example 

```groovy
	val multipleProgressBar = MultipleProgressBar(applicationContext!!).apply {
            animate = true
            animationDuration = 6000
        }

        multipleProgressBar.addProgressItem(ProgressItem(applicationContext)
                .apply {
                    showProgressText = true
                    showLabel = true
                    orientation = ProgressItem.ORIENTATION_LEFT
                }, "ITEM1")

        multipleProgressBar.getProgressItemByTag("ITEM1")
                ?.apply {
                    setProgress(50,false)
                    setSecondaryProgress(70,false)
                }

```

## XML example

```groovy

<com.yingenus.multipleprogressbar.MultipleProgressBar
    android:id="@+id/test_multipleLineProgressBar"
    android:layout_width="250dp"
    android:layout_height="250dp"
    app:mpb_dividerSize="10dp"
    app:mpb_progressSize="20dp"
    app:mpb_animate="true"
    app:mpb_animationDuration="6000">

    <com.yingenus.multipleprogressbar.ProgressItem
        android:id="@+id/item1"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:mpb_orientation="right"
        app:mpb_labelText="test1"
        app:mpb_progress="25"
        app:mpb_progressColor="@color/color1"
        app:mpb_secondaryProgress="80"
        app:mpb_secondaryProgressColor="@color/colorSecond1"
        />

    <com.yingenus.multipleprogressbar.ProgressItem
        android:id="@+id/item2"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:mpb_labelText="test2"
        app:mpb_orientation="left"
        app:mpb_progress="40"
        app:mpb_progressColor="@color/color2"
        app:mpb_secondaryProgress="50"
        app:mpb_secondaryProgressColor="@color/colorSecond2" />
</com.yingenus.multipleprogressbar.MultipleProgressBar>

```

## Demo
! [] (https://github.com/yingenus/multipleprogressbar/blob/main/IMG_calv80.gif)

! [] (https://github.com/yingenus/multipleprogressbar/blob/main/IMG_9cum7d.gif)

## Gradle

```groovy

dependencies {
    implementation 'com.github.yingenus:MultipleProgressBar:1.0.0'
}

```


## Attributes

MultipleProgressBar attributes :

|name|format|description|
|:---:|:---:|:---:|
|mpb_animate|boolean||
|mpb_animationDuration|integer||
|mpb_progressSize|dimension||
|mpb_dividerSize|dimension||

MultipleProgressBarItem attributes:

|name|format|description|
|:---:|:---:|:---:|
|mpb_progressColor|color||
|mpb_secondaryProgressColor|color||
|mpb_min|integer||
|mpb_max|integer||
|mpb_labelText|string||
|mpb_showLabel|boolean||
|mpb_labelGravity|enum||
|mpb_labelColor|color||
|mpb_progressText|enum||
|mpb_progressTextColor|color||
|mpb_showProgressText|boolean||
|mpb_progressTextGravity|enum||
|mpb_secondaryProgressTextColor|color||
|mpb_showSecondaryProgressText|boolean||
|mpb_progress|integer||
|mpb_secondaryProgress|integer||
|mpb_orientation|integer||