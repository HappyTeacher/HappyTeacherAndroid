package org.jnanaprabodhini.happyteacher.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.extension.setHtmlText
import org.jnanaprabodhini.happyteacher.model.LessonCard
import org.jnanaprabodhini.happyteacher.viewholder.LessonCardViewHolder


/**
 * Created by grahamearley on 9/25/17.
 */
class LessonPlanRecyclerAdapter(val lessonCards: List<LessonCard>): RecyclerView.Adapter<LessonCardViewHolder>() {

    override fun getItemCount(): Int = lessonCards.size

    override fun onBindViewHolder(holder: LessonCardViewHolder?, position: Int) {
        // todo: reset all views to invisible here before doing anything

        val card = lessonCards[position]
        holder?.headerTextView?.text = card.header
        holder?.bodyTextView?.setHtmlText(card.body)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): LessonCardViewHolder {
        val cardView = LayoutInflater.from(parent?.context).inflate(R.layout.list_item_lesson_card, parent, false)
        return LessonCardViewHolder(cardView)
    }
}