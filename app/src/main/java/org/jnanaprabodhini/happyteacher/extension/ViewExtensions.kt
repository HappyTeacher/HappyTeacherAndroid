package org.jnanaprabodhini.happyteacher.extension

import android.database.DataSetObserver
import android.os.Build
import android.support.annotation.ColorRes
import android.support.annotation.DimenRes
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.support.v4.content.res.ResourcesCompat
import android.support.v4.view.ViewCompat
import android.support.v7.content.res.AppCompatResources
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.TooltipCompat
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import com.commonsware.cwac.anddown.AndDown
import com.squareup.picasso.Picasso
import org.jnanaprabodhini.happyteacher.R
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

fun View.setInvisible() {
    this.visibility = View.INVISIBLE
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

fun View.showSnackbarWithAction(message: String, actionName: String, action: (View) -> Unit) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG).setAction(actionName, action).show()
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

fun View.setBackgroundDrawable(@DrawableRes drawableId: Int) {
    val drawable = AppCompatResources.getDrawable(context, drawableId)
    this.background = drawable
}

fun View.setBackgroundColorRes(@ColorRes colorId: Int) {
    val color = ResourcesCompat.getColor(context.resources, colorId, null)
    this.setBackgroundColor(color)
}

fun TextView.setTextColorRes(@ColorRes colorId: Int) {
    val color = ResourcesCompat.getColor(context.resources, colorId, null)
    this.setTextColor(color)
}

fun TextView.setHtmlAndMarkdownText(formattedString: String) {
    this.movementMethod = LinkMovementMethod.getInstance()
    val tagHandler = RootListTagHandler()

    val markdownParser = AndDown()
    val htmlFromMarkdown = markdownParser.markdownToHtml(formattedString)

    val spannedText = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(htmlFromMarkdown, Html.FROM_HTML_MODE_COMPACT, null, tagHandler)
    } else {
        Html.fromHtml(htmlFromMarkdown, null, tagHandler)
    }

    this.text = spannedText.trim()
}

fun ImageView.loadImageToFit(imageUrl: String) {
    Picasso.with(context)
            .load(imageUrl)
            .placeholder(R.drawable.ic_image_primary_32dp)
            .error(R.drawable.ic_error_gray_32dp)
            .fit()
            .centerCrop()
            .into(this)
}

fun ImageView.loadImageToFitWithNoPlaceholders(imageUrl: String) {
    Picasso.with(context)
            .load(imageUrl)
            .fit()
            .centerCrop()
            .into(this)
}

fun ImageView.loadFullImageWithNoPlaceholder(imageUrl: String) {
    Picasso.with(context)
            .load(imageUrl)
            .error(R.drawable.ic_error_gray_32dp)
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

fun Animation.onFinish(onFinish: () -> Unit) {
    this.setAnimationListener(object: Animation.AnimationListener {
        override fun onAnimationRepeat(p0: Animation?) {}
        override fun onAnimationEnd(animation: Animation?) {
            onFinish()
        }
        override fun onAnimationStart(p0: Animation?) {}
    })
}

fun View.setTooltip(tooltipText: String) {
    TooltipCompat.setTooltipText(this, tooltipText)
}