package org.jnanaprabodhini.happyteacher.extension

import android.database.DataSetObserver
import android.graphics.Bitmap
import android.os.Build
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.support.v7.content.res.AppCompatResources
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.View
import android.view.animation.AnimationUtils
import android.webkit.*
import android.widget.*
import com.squareup.picasso.Picasso
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.extension.taghandler.RootListTagHandler
import android.content.Intent
import android.net.Uri


/**
 * Extension functions for Views.
 */

fun View.setVisibilityGone() {
    this.visibility = View.GONE
}

fun View.setVisible() {
    this.visibility = View.VISIBLE
}

fun View.isGone(): Boolean = this.visibility == View.GONE
fun View.isVisible(): Boolean = this.visibility == View.VISIBLE

fun View.jiggle() {
    val jiggleAnimation = AnimationUtils.loadAnimation(this.context, R.anim.jiggle)
    this.startAnimation(jiggleAnimation)
}

fun View.showSnackbar(@StringRes stringId: Int) {
    val message = this.context.getString(stringId)
    this.showSnackbar(message)
}

fun View.showSnackbar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG).show()
}

fun View.setOneTimeOnClickListener(l: View.OnClickListener) {
    this.setOnClickListener{ view ->
        setOnClickListener(null)
        l.onClick(view)
    }
}

fun View.setOneTimeOnClickListener(onClick: () -> Unit) {
    this.setOneTimeOnClickListener(android.view.View.OnClickListener { onClick() })
}

fun View.setOneTimeOnLongClickListener(l: View.OnLongClickListener) {
    this.setOnLongClickListener { view ->
        setOnLongClickListener(null)
        l.onLongClick(view)
    }
}

fun View.setOneTimeOnLongClickListener(onLongClick: () -> Unit) {
    this.setOneTimeOnLongClickListener(android.view.View.OnLongClickListener { onLongClick(); true })
}

fun Spinner.onItemSelected(onItemSelected: (Int) -> Unit) {
    this.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {}

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            onItemSelected(position)
        }
    }
}

fun Spinner.items(): List<Any> = (0..count - 1).map { adapter.getItem(it) }

/**
 * Select the given index in the spinner when the spinner's
 *  item count reaches that level.
 *
 *  Used for re-selecting a previous selection when a spinner
 *   is reset (across configuration changes).
 */
fun Spinner.selectIndexWhenPopulated(index: Int) {
    if (adapter == null) return

    adapter.registerDataSetObserver(object: DataSetObserver() {
        override fun onChanged() {
            if (count >= index) {
                setSelection(index)
                adapter.unregisterDataSetObserver(this)
            }
        }
    })
}

fun TextView.setDrawableLeft(@DrawableRes drawableId: Int) {
    val drawable = AppCompatResources.getDrawable(context, drawableId)
    this.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
}

fun TextView.setHtmlText(htmlString: String) {
    this.movementMethod = LinkMovementMethod.getInstance()

    val tagHandler = RootListTagHandler()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        this.text = Html.fromHtml(htmlString, Html.FROM_HTML_MODE_COMPACT, null, tagHandler)
    } else {
        this.text = Html.fromHtml(htmlString, null, tagHandler)
    }

    // Ensure no newline at beginning:
    this.text = this.text.removePrefix("\n")
}

fun ImageView.loadImageToFit(imageUrl: String) {
    Picasso.with(context)
            .load(imageUrl)
            .placeholder(R.drawable.ripple_accent_gray)
            .error(R.drawable.white_ripple_pill)
            .fit()
            .centerCrop()
            .into(this)
}

fun ImageView.loadImageWithNoPlaceholder(imageUrl: String) {
    Picasso.with(context)
            .load(imageUrl)
            .error(R.drawable.white_ripple_pill)
            .fit()
            .centerInside()
            .into(this)
}

fun WebView.loadYoutubeVideo(youtubeId: String) {
    settings.javaScriptEnabled = true
    settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN

    isVerticalScrollBarEnabled = false
    isHorizontalScrollBarEnabled = false

    val embedCode = "<iframe width='100%' height='100%' src=\"https://www.youtube.com/embed/$youtubeId?&theme=dark&color=white&autohide=1&fs=0&showinfo=0&rel=0\"frameborder=\"0\"></iframe>"

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
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=$youtubeId")))
                loadData(embedCode, "text/html", "UTF-8")
            } else {
                super.onPageStarted(view, url, favicon)
            }
        }
    }

    loadData(embedCode, "text/html", "UTF-8")
}

fun RecyclerView.onHorizontalScroll(onHorizontalScroll: () -> Unit) {
    addOnScrollListener(object: RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
            if (dx > 0) onHorizontalScroll()
        }
    })
}