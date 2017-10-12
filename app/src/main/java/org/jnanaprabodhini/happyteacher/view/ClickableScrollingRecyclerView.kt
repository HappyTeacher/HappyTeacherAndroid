package org.jnanaprabodhini.happyteacher.view

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.MotionEvent


/**
 * This RecyclerView subclass is a workaround for a
 *  bug in the support library.
 *
 *  Without it, a RecyclerView that has been scrolled only
 *  allow clicks on its children a couple seconds after the
 *  scroll is complete (at least for nested recycler views).
 *
 *  🙏
 *  Many thanks to this kind StackOverflow user for this fix:
 *  https://stackoverflow.com/q/46452465/5054197
 */
class ClickableScrollingRecyclerView(context: Context, attrs: AttributeSet): RecyclerView(context, attrs) {

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        val requestCancelDisallowInterceptTouchEvent = scrollState == RecyclerView.SCROLL_STATE_SETTLING
        val consumed = super.onInterceptTouchEvent(event)
        val action = event.actionMasked

        if (action == MotionEvent.ACTION_DOWN && requestCancelDisallowInterceptTouchEvent) {
            parent.requestDisallowInterceptTouchEvent(false)
            // Stop scroll to enable child view get the touch event
            stopScroll()

            // Don't consume the event
            return false
        }

        return consumed
    }
}