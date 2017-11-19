package org.jnanaprabodhini.happyteacherapp.adapter.viewholder

import android.app.Activity
import android.view.View
import com.google.firebase.firestore.DocumentReference
import org.jnanaprabodhini.happyteacherapp.activity.ClassroomResourceViewerActivity
import org.jnanaprabodhini.happyteacherapp.model.ResourceHeader

class ClassroomResourcesHeaderViewHolder(itemView: View): ResourceHeaderViewHolder(itemView) {

    init {
        setColorBarForClassroomResource()
    }

    override fun launchContentViewerActivity(activity: Activity, contentDocumentRef: DocumentReference, resourceHeaderModel: ResourceHeader?) {
        ClassroomResourceViewerActivity.launch(activity, contentDocumentRef, resourceHeaderModel ?: ResourceHeader())
    }
}