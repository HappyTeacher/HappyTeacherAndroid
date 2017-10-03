package org.jnanaprabodhini.happyteacher.view

import android.content.Context
import android.support.annotation.DrawableRes
import android.support.v4.content.res.ResourcesCompat
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.view_download_bar.view.*
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.adapter.helper.AttachmentDownloadManager
import org.jnanaprabodhini.happyteacher.extension.setOneTimeOnClickListener
import org.jnanaprabodhini.happyteacher.extension.setOneTimeOnLongClickListener
import org.jnanaprabodhini.happyteacher.extension.setVisibilityGone
import org.jnanaprabodhini.happyteacher.extension.setVisible

/**
 * Created by grahamearley on 9/29/17.
 */
class DownloadBarView(context: Context, attributeSet: AttributeSet): FrameLayout(context, attributeSet) {

    var downloadManager: AttachmentDownloadManager? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.view_download_bar, this)
    }

    fun setAttachmentDownloadManager(downloadManager: AttachmentDownloadManager) {
        downloadManager.bindView(this)
        this.downloadManager = downloadManager
    }

    fun setText(text: String) {
        textView.text = text
    }

    fun getText(): CharSequence = textView.text

    fun setLoadingWithText(text: String) {
        resetView()
        indeterminateProgressBar.setVisible()
        setText(text)
    }

    fun setDownloadIconWithText(text: String) {
        setIconDrawable(R.drawable.ic_file_download_white_24dp)
        setText(text)
    }

    fun setCancelIconWithText(text: String) {
        setIconDrawable(R.drawable.ic_close_white_24dp)
        setText(text)
    }

    fun setFolderIconWithText(text: String) {
        setIconDrawable(R.drawable.ic_folder_white_24dp)
        setText(text)
    }

    fun setErrorWithText(errorText: String) {
        setIconDrawable(R.drawable.ic_error_white_24dp)
        setText(errorText)
        rootFrame.setBackgroundColor(R.color.lightGray)
    }

    fun setProgress(percent: Double) {
        val progressWidth = width * percent

        val layoutParams = progressBarBackground.layoutParams
        layoutParams.width = progressWidth.toInt()

        progressBarBackground.layoutParams = layoutParams
    }

    fun resetProgress() {
        setProgress(0.0)
    }

    fun setProgressComplete() {
        setProgress(1.0)
    }

    fun setOneTimeOnClickListener(onClick: () -> Unit) {
        rootFrame.setOneTimeOnClickListener(onClick)
    }

    fun setOneTimeOnLongClickListener(onLongClick: () -> Unit) {
        rootFrame.setOneTimeOnLongClickListener(onLongClick)
    }

    override fun setOnClickListener(l: OnClickListener?) {
        rootFrame.setOnClickListener(l)
    }

    private fun resetBackgroundColor() {
        rootFrame.background = ResourcesCompat.getDrawable(context.resources, R.drawable.ripple_blue, null)
    }

    private fun setIconDrawable(@DrawableRes drawable: Int) {
        resetView()
        icon.setVisible()
        icon.setImageDrawable(ResourcesCompat.getDrawable(context.resources, drawable, null))
    }

    private fun resetView() {
        resetBackgroundColor()
        setText("")
        indeterminateProgressBar.setVisibilityGone()
        icon.setVisibilityGone()
    }
}