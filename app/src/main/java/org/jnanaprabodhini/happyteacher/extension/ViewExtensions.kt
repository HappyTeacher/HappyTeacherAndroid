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
import android.support.annotation.ColorRes
import android.support.annotation.DimenRes
import android.support.v4.content.res.ResourcesCompat
import android.support.v4.view.ViewCompat
import android.support.v7.widget.LinearLayoutManager


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

fun View.setElevation(@DimenRes elevationDimenRes: Int) {
    ViewCompat.setElevation(this, context.resources.getDimensionPixelSize(elevationDimenRes).toFloat())
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
    adapter?.registerDataSetObserver(object: DataSetObserver() {
        override fun onChanged() {
            if (count > 0 && count >= index) {
                setSelection(index)
                adapter?.unregisterDataSetObserver(this)
            }
        }
    })
}

fun TextView.setDrawableLeft(@DrawableRes drawableId: Int) {
    val drawable = AppCompatResources.getDrawable(context, drawableId)
    this.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
}

fun TextView.setDrawableRight(@DrawableRes drawableId: Int) {
    val drawable = AppCompatResources.getDrawable(context, drawableId)
    this.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
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

fun ImageView.setDrawableResource(@DrawableRes drawableRes: Int) {
    this.setImageDrawable(ResourcesCompat.getDrawable(resources, drawableRes, null))
}

fun RecyclerView.canScrollLeftHorizontally(): Boolean {
    return canScrollHorizontally(-1)
}

fun RecyclerView.canScrollRightHorizontally(): Boolean {
    return canScrollHorizontally(1)
}

fun LinearLayoutManager.isFirstVisiblePositionCompletelyVisible(): Boolean {
    val firstVisiblePosition = findFirstVisibleItemPosition()
    val firstCompletelyVisiblePosition = findFirstCompletelyVisibleItemPosition()

    return firstVisiblePosition == firstCompletelyVisiblePosition
}

fun LinearLayoutManager.isLastVisiblePositionCompletelyVisible(): Boolean {
    val lastVisiblePosition = findLastVisibleItemPosition()
    val lastCompletelyVisiblePosition = findLastCompletelyVisibleItemPosition()

    return lastVisiblePosition == lastCompletelyVisiblePosition
}