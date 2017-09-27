package org.jnanaprabodhini.happyteacher.extension

import android.database.DataSetObserver
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.support.v7.content.res.AppCompatResources
import android.text.Html
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.webkit.WebView
import android.widget.*
import com.google.android.youtube.player.*
import com.squareup.picasso.Picasso
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.YOUTUBE_API_KEY
import org.jnanaprabodhini.happyteacher.extension.taghandler.RootListTagHandler

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

fun ImageView.loadImage(imageUrl: String) {
    Picasso.with(context)
            .load(imageUrl)
            .placeholder(R.drawable.ripple_accent_gray)
            .error(R.drawable.primary_button_pill)
            .fit()
            .centerCrop()
            .into(this)
}

fun ImageView.loadYoutubeThumbnail(id: String) {
    val imageUrl = "https://img.youtube.com/vi/$id/hqdefault.jpg"
    loadImage(imageUrl)
}