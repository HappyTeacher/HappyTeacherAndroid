package org.jnanaprabodhini.happyteacher.view

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.util.AttributeSet
import android.webkit.*
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.extension.loadImageToFit
import org.jnanaprabodhini.happyteacher.extension.setVisibilityGone
import org.jnanaprabodhini.happyteacher.extension.setVisible
import org.jnanaprabodhini.happyteacher.extension.showToast


/**
 * Created by grahamearley on 10/12/17.
 */
class YoutubeWebView(context: Context, attrs: AttributeSet): WebView(context, attrs) {

    init {
        settings.javaScriptEnabled = true
        settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN

        isVerticalScrollBarEnabled = false
        isHorizontalScrollBarEnabled = false
    }

    fun initializeForYoutubeIdWithControlViews(videoId: String, thumbnailView: ImageView, loadButton: TextView, progressBar: ProgressBar) {
        thumbnailView.setVisible()
        loadButton.setVisible()
        loadButton.setText(R.string.load_Video)

        thumbnailView.loadImageToFit("https://img.youtube.com/vi/$videoId/hqdefault.jpg")

        this.setVisibilityGone()

        loadButton.setOnClickListener{
            initializeForYoutubeId(videoId, loadButton, progressBar)
            this.setVisible()
        }
    }

    private fun initializeForYoutubeId(videoId: String, loadButton: TextView, progressBar: ProgressBar) {
        val embedCode = "<iframe width='100%' height='100%' src=\"https://www.youtube.com/embed/$videoId?&theme=dark&color=white&autohide=1&fs=0&showinfo=0&rel=0\"frameborder=\"0\"></iframe>"

        progressBar.setVisible()

        webViewClient = object: WebViewClient() {
            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                // TODO: Better error, translated:
                progressBar.setVisibilityGone()
                onError(videoId, loadButton)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                progressBar.setVisibilityGone()
            }

            /**
             * If the WebView tries to load a page that is not a "data" url (i.e. the embed data we're loading),
             *   then load the Youtube video in the Youtube app or browser.
             *
             *   This prevents the webview from displaying anything else from the internet.
             */
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                if (url?.substring(0, 4) != "data") {
                    launchYoutubeIntent(videoId)
                    loadData(embedCode, "text/html", "UTF-8")
                } else {
                    super.onPageStarted(view, url, favicon)
                }
            }
        }

        loadData(embedCode, "text/html", "UTF-8")
    }

    fun onError(videoId: String, loadButton: TextView) {
        context.showToast(R.string.unable_to_load_youtube_video)
        this.setVisibilityGone()

        loadButton.setVisible()

        val linkText = context.getString(R.string.open_in_youtube)
        loadButton.text = linkText

        loadButton.setOnClickListener{ launchYoutubeIntent(videoId) }
    }

    fun launchYoutubeIntent(videoId: String) {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=$videoId")))
    }

}