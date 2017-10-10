package org.jnanaprabodhini.happyteacher.adapter.contentlist

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.activity.parent.HappyTeacherActivity
import org.jnanaprabodhini.happyteacher.adapter.viewholder.ContentCardViewHolder
import org.jnanaprabodhini.happyteacher.model.ContentCard
import java.io.File

class ClassroomResourcesRecyclerAdapter(contentCardMap: Map<String, ContentCard>, attachmentDestinationDirectory: File, topicName: String, topicId: String, subtopicId: String, activity: HappyTeacherActivity): CardListContentRecyclerAdapter(contentCardMap, attachmentDestinationDirectory, topicName, topicId, subtopicId, activity) {
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        if (holder is ContentCardViewHolder) {
            onBindLessonCardViewHolder(holder, position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        val cardView = LayoutInflater.from(parent?.context).inflate(R.layout.list_item_lesson_card, parent, false)
        return ContentCardViewHolder(cardView)
    }

}