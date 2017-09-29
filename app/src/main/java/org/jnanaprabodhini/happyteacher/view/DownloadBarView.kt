package org.jnanaprabodhini.happyteacher.view

import android.content.Context
import android.support.v4.content.res.ResourcesCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.view_download_bar.view.*
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.extension.setVisibilityGone
import org.jnanaprabodhini.happyteacher.extension.setVisible

/**
 * Created by grahamearley on 9/29/17.
 */
class DownloadBarView(context: Context, attributeSet: AttributeSet): FrameLayout(context, attributeSet) {

    init {
        LayoutInflater.from(context).inflate(R.layout.view_download_bar, this)
        DrawableCompat.setTint(indeterminateProgressBar.indeterminateDrawable, R.color.lightGray)
    }

    fun setText(text: String) { //todo: resId
        textView.text = text
    }

    fun setLoading() {
        resetView()
        indeterminateProgressBar.setVisible()
    }

    fun setDownloadIcon() {
        resetView()
        icon.setVisible()
        icon.setImageDrawable(ResourcesCompat.getDrawable(context.resources, R.drawable.ic_file_download_white_24dp, null))
    }

    fun setCancelIcon() {
        resetView()
        icon.setVisible()
        icon.setImageDrawable(ResourcesCompat.getDrawable(context.resources, R.drawable.ic_close_white_24dp, null))
    }

    fun setFolderIcon() {
        resetView()
        icon.setVisible()
        icon.setImageDrawable(ResourcesCompat.getDrawable(context.resources, R.drawable.ic_folder_white_24dp, null))
    }

    fun setErrorWithText(errorText: String) {
        resetView()
        icon.setVisible()
        icon.setImageDrawable(ResourcesCompat.getDrawable(context.resources, R.drawable.ic_error_white_24dp, null))
        rootFrame.setBackgroundColor(R.color.lightGray)
        setText(errorText)
    }

    fun resetBackgroundColor() {
        rootFrame.background = ResourcesCompat.getDrawable(context.resources, R.drawable.ripple_blue, null)
    }

    fun setIconOnClickListener(onClick: () -> Unit) {
        icon.setOnClickListener { onClick() }
    }

    fun setProgress(percent: Double) {
        val progressWidth = width * percent
        progressBarBackground.layoutParams.width = progressWidth.toInt()
    }

    fun resetProgress() {
        progressBarBackground.layoutParams.width = 0
    }

    private fun resetView() {
        resetBackgroundColor()
        indeterminateProgressBar.setVisibilityGone()
        icon.setVisibilityGone()
    }

    override fun setOnClickListener(l: OnClickListener?) {
        rootFrame.setOnClickListener(l)
    }
}