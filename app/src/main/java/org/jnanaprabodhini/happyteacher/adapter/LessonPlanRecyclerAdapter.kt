package org.jnanaprabodhini.happyteacher.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.model.LessonCard
import org.jnanaprabodhini.happyteacher.viewholder.LessonCardViewHolder


/**
 * Created by grahamearley on 9/25/17.
 */
class LessonPlanRecyclerAdapter(var lessonCards: List<LessonCard>): RecyclerView.Adapter<LessonCardViewHolder>() {

    init {
        // In Firebase, the cards are keyed by numbers (to make a list).
        //  If the cards are not perfectly numbered from 0 upward, Firebase
        //  will insert null items at the missing indices.

        // Filter out these null items in case there is an input error in the backend:
        lessonCards = lessonCards.filter { it != null }
    }

    override fun getItemCount(): Int = lessonCards.size

    override fun onBindViewHolder(holder: LessonCardViewHolder?, position: Int) {
        val card = lessonCards[position]
        holder?.headerTextView?.text = card.header
        holder?.bodyTextView?.text =  card.body
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): LessonCardViewHolder {
        val cardView = LayoutInflater.from(parent?.context).inflate(R.layout.list_item_lesson_card, parent, false)
        return LessonCardViewHolder(cardView)
    }
}