package org.jnanaprabodhini.happyteacher.adapter.contentlist

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.activity.base.HappyTeacherActivity
import org.jnanaprabodhini.happyteacher.adapter.helper.FirebaseDataObserver
import org.jnanaprabodhini.happyteacher.adapter.viewholder.ContentCardViewHolder
import org.jnanaprabodhini.happyteacher.model.ContentCard
import java.io.File

class EditableLessonRecyclerAdapter(options: FirestoreRecyclerOptions<ContentCard>, attachmentDestinationDirectory: File, subtopicId: String, activity: HappyTeacherActivity, dataObserver: FirebaseDataObserver):
        CardListContentRecyclerAdapter(options, attachmentDestinationDirectory, subtopicId, activity, dataObserver) {
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int, model: ContentCard?) {
        if (holder is ContentCardViewHolder) {
            onBindContentCardViewHolder(holder, model)

            val cardRef = snapshots.getSnapshot(position).reference
            holder.setupEditButtons(activity, cardRef)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        val cardView = LayoutInflater.from(parent?.context).inflate(R.layout.list_item_content_card, parent, false)
        return ContentCardViewHolder(cardView)
    }

}