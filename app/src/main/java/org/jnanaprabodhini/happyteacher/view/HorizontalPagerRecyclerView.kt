package org.jnanaprabodhini.happyteacher.view

import android.content.Context
import android.support.v4.view.ViewCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.view_recycler_horizontal_pager.view.*
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.adapter.FirebaseDataObserverRecyclerAdapter
import org.jnanaprabodhini.happyteacher.adapter.viewholder.SubtopicHeaderViewHolder
import org.jnanaprabodhini.happyteacher.extension.setVisibilityGone
import org.jnanaprabodhini.happyteacher.model.SubtopicLessonHeader

/**
 * Created by grahamearley on 10/4/17.
 */
class HorizontalPagerRecyclerView(context: Context, attrs: AttributeSet): FrameLayout(context, attrs) {

    init {
        LayoutInflater.from(context).inflate(R.layout.view_recycler_horizontal_pager, this)
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        // todo - view extensions for this:
        ViewCompat.setElevation(backPager, context.resources.getDimensionPixelSize(R.dimen.cardview_default_elevation).toFloat())
        ViewCompat.setElevation(forwardPager, context.resources.getDimensionPixelSize(R.dimen.cardview_default_elevation).toFloat())
        backPager.setVisibilityGone()
    }

    fun setAdapter(adapter: RecyclerView.Adapter<*>) {
        recyclerView.adapter = adapter
    }

}