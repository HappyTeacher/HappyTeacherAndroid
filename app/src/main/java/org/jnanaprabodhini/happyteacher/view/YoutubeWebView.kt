package org.jnanaprabodhini.happyteacher.view

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.webkit.*
import android.widget.ImageView
import android.widget.ProgressBar
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.extension.loadImageToFit
import org.jnanaprabodhini.happyteacher.extension.setVisibilityGone
import org.jnanaprabodhini.happyteacher.extension.setVisible


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

    fun initializeForYoutubeIdWithControlViews(videoId: String, thumbnailView: ImageView, playButton: View, progressBar: ProgressBar) {
        thumbnailView.setVisible()
        playButton.setVisible()

        thumbnailView.loadImageToFit("https://img.youtube.com/vi/$videoId/hqdefault.jpg")

        this.setVisibilityGone()

        playButton.setOnClickListener{
            initializeForYoutubeId(videoId, progressBar)

            thumbnailView.setVisibilityGone()
            playButton.setVisibilityGone()

            this.setVisible()
        }
    }

    private fun initializeForYoutubeId(videoId: String, progressBar: ProgressBar? = null) {
        val embedCode = "<iframe width='100%' height='100%' src=\"https://www.youtube.com/embed/$videoId?autoplay=1&theme=dark&color=white&autohide=1&fs=0&showinfo=0&rel=0\"frameborder=\"0\"></iframe>"

        progressBar?.setVisible()

        webViewClient = object: WebViewClient() {
            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                // TODO: Better error, translated:
                progressBar?.setVisibilityGone()
                showErrorInWebview(videoId)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                progressBar?.setVisibilityGone()
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

    fun showErrorInWebview(videoId: String) {
        val errorText = context.getString(R.string.unable_to_load_youtube_video)
        val linkText = context.getString(R.string.click_here_to_open_in_youtube)
        val htmlCode = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "    <head>\n" +
                "        <style>\n" +
                "        html, body { height: 100%; margin: 0; padding: 0; width: 100%; }\n" +
                "        body { display: table; }\n" +
                "        .centered { text-align: center; display: table-cell; vertical-align: middle; }\n" +
                "        </style>\n" +
                "    </head>\n" +
                "    <body>\n" +
                "    <div class=\"centered\">\n" +
                "       $errorText </br>" +
                "       $linkText" +
                "    </div>\n" +
                "    </body>\n" +
                "</html>"

        loadData(htmlCode, "text/html", "UTF-8")
    }

    fun launchYoutubeIntent(videoId: String) {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=$videoId")))
    }

}