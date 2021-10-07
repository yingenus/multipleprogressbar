
## MultipleProgressBar

round progress bar for android with many elements

## Kotlin example 

```kotlin
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

```xml

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
![](https://github.com/yingenus/multipleprogressbar/blob/main/IMG_calv80.gif)

![](https://github.com/yingenus/multipleprogressbar/blob/main/IMG_9cum7d.gif)

## Gradle

```groovy
repositories {
        maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.yingenus:multipleprogressbar:v1.0'
}

```


## Attributes

MultipleProgressBar attributes :

|name|format|description|
|:---:|:---:|:---:|
|mpb_animate|boolean|rotation animation|
|mpb_animationDuration|integer|duration of a turnover|
|mpb_progressSize|dimension|progress width|
|mpb_dividerSize|dimension|distance between elements|

MultipleProgressBarItem attributes:

|name|format|description|
|:---:|:---:|:---:|
|mpb_progressColor|color|progress bar color|
|mpb_secondaryProgressColor|color|second progress indicator color|
|mpb_min|integer|minimum progress|
|mpb_max|integer|maximum progress|
|mpb_labelText|string|title text|
|mpb_showLabel|boolean|show title|
|mpb_labelGravity|enum|title position IN- inside the progress bar, OUT- outside the progress bar ,adaptive- any|
|mpb_labelColor|color|title color|
|mpb_progressText|enum|percent-percentage progress, raf- in original values|
|mpb_progressTextColor|color|progress indicator color|
|mpb_showProgressText|boolean|show progress indicator|
|mpb_progressTextGravity|enum|position IN- inside the progress bar, OUT- outside the progress bar ,adaptive- any|
|mpb_secondaryProgressTextColor|color|second progress indicator color|
|mpb_showSecondaryProgressText|boolean|show second progress indicator|
|mpb_progress|integer|progress|
|mpb_secondaryProgress|integer|second progress |
|mpb_orientation|integer|left - progress changes from left to right, right- progress changes from right to left|
