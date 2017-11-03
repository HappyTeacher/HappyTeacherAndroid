package org.jnanaprabodhini.happyteacher.adapter.viewholder

import android.app.Activity
import android.view.View
import com.google.firebase.firestore.CollectionReference
import org.jnanaprabodhini.happyteacher.activity.CardListContentViewerActivity
import org.jnanaprabodhini.happyteacher.activity.ClassroomResourceViewerActivity
import org.jnanaprabodhini.happyteacher.model.CardListContentHeader

class ClassroomResourcesHeaderViewHolder(itemView: View): CardListHeaderViewHolder(itemView) {
    override fun launchContentViewerActivity(activity: Activity, cardRef: CollectionReference, cardListContentHeaderModel: CardListContentHeader?, topicName: String) {
        ClassroomResourceViewerActivity.launch(activity, cardRef, cardListContentHeaderModel ?: CardListContentHeader(), topicName)
    }
}