package org.jnanaprabodhini.happyteacher.adapter.viewholder

import android.app.Activity
import android.view.View
import com.google.firebase.firestore.DocumentReference
import org.jnanaprabodhini.happyteacher.activity.ClassroomResourceViewerActivity
import org.jnanaprabodhini.happyteacher.model.ResourceHeader

class ClassroomResourcesHeaderViewHolder(itemView: View): ResourceHeaderViewHolder(itemView) {
    override fun launchContentViewerActivity(activity: Activity, contentDocumentRef: DocumentReference, resourceHeaderModel: ResourceHeader?) {
        ClassroomResourceViewerActivity.launch(activity, contentDocumentRef, resourceHeaderModel ?: ResourceHeader())
    }
}