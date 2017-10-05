package org.jnanaprabodhini.happyteacher.view

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.view_recycler_horizontal_pager.view.*
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.extension.*

/**
 * A horizontal RecyclerView wrapper that has forward and backward pager buttons
 *  for scrolling content.
 */
class HorizontalPagerRecyclerView(context: Context, attrs: AttributeSet): FrameLayout(context, attrs) {

    val onScrollListener = object: RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
            when (newState) {
                RecyclerView.SCROLL_STATE_IDLE -> showRelevantPagers()
                RecyclerView.SCROLL_STATE_DRAGGING -> hidePagers()
            }
        }
    }

    val onChildAttachListener = object: RecyclerView.OnChildAttachStateChangeListener {
        override fun onChildViewDetachedFromWindow(view: View?) { showRelevantPagers() }
        override fun onChildViewAttachedToWindow(view: View?) { showRelevantPagers() }
    }

    val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

    init {
        LayoutInflater.from(context).inflate(R.layout.view_recycler_horizontal_pager, this)
        recyclerView.layoutManager = layoutManager

        backwardPager.setElevation(R.dimen.pager_button_elevation)
        forwardPager.setElevation(R.dimen.pager_button_elevation)

        setPagerClickListeners()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        recyclerView.addOnChildAttachStateChangeListener(onChildAttachListener)
        recyclerView.addOnScrollListener(onScrollListener)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        recyclerView.removeOnChildAttachStateChangeListener(onChildAttachListener)
        recyclerView.removeOnScrollListener(onScrollListener)
    }

    fun setAdapter(adapter: RecyclerView.Adapter<*>) {
        recyclerView.adapter = adapter
    }

    fun showRelevantPagers() {
        if (recyclerView.canScrollLeftHorizontally()) {
            backwardPager.setVisible()
        } else {
            backwardPager.setVisibilityGone()
        }

        if (recyclerView.canScrollRightHorizontally()) {
            forwardPager.setVisible()
        } else {
            forwardPager.setVisibilityGone()
        }
    }

    fun hidePagers() {
        backwardPager.setVisibilityGone()
        forwardPager.setVisibilityGone()
    }

    fun setPagerClickListeners() {
        backwardPager.setOnClickListener {
            val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
            val firstCompletelyVisiblePosition = layoutManager.findFirstCompletelyVisibleItemPosition()

            if (layoutManager.isFirstVisiblePositionCompletelyVisible()) {
                attemptScrollToPosition(firstCompletelyVisiblePosition - 1)
            } else {
                attemptScrollToPosition(firstVisiblePosition)
            }
        }

        forwardPager.setOnClickListener {
            val lastVisiblePosition = layoutManager.findLastVisibleItemPosition()
            val lastCompletelyVisiblePosition = layoutManager.findLastCompletelyVisibleItemPosition()

            if (layoutManager.isLastVisiblePositionCompletelyVisible()) {
                attemptScrollToPosition(lastCompletelyVisiblePosition + 1)
            } else {
                attemptScrollToPosition(lastVisiblePosition)
            }
        }
    }

    private fun attemptScrollToPosition(position: Int) {
        if (position < recyclerView.adapter?.itemCount ?: 0) {
            recyclerView.smoothScrollToPosition(position)
        }
    }

}