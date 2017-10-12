package org.jnanaprabodhini.happyteacher.view

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import android.webkit.*
import android.widget.ImageView
import android.widget.ProgressBar
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

    fun initializeForYoutubeId(id: String) {
        val embedCode = "<iframe width='100%' height='100%' src=\"https://www.youtube.com/embed/$id?autoplay=1&theme=dark&color=white&autohide=1&fs=0&showinfo=0&rel=0\"frameborder=\"0\"></iframe>"

        webViewClient = object: WebViewClient() {
            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                // TODO: Better error, translated:
                loadData("Error loading the page.", "text/html", "UTF-8")
            }

            /**
             * If the WebView tries to load a page that is not a "data" url (i.e. the embed data we're loading),
             *   then load the Youtube video in the Youtube app or browser.
             *
             *   This prevents the webview from displaying anything else from the internet.
             */
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                if (url?.substring(0, 4) != "data") {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=$id")))
                    loadData(embedCode, "text/html", "UTF-8")
                } else {
                    super.onPageStarted(view, url, favicon)
                }
            }
        }

        loadData(embedCode, "text/html", "UTF-8")
    }

}