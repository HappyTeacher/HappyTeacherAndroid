package org.jnanaprabodhini.happyteacherapp.activity

import android.app.Activity
import android.content.Intent
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.android.synthetic.main.activity_card_list_content_viewer.*
import org.jnanaprabodhini.happyteacherapp.R
import org.jnanaprabodhini.happyteacherapp.adapter.contentlist.ResourceRecyclerAdapter
import org.jnanaprabodhini.happyteacherapp.adapter.contentlist.ClassroomResourceRecyclerAdapter
import org.jnanaprabodhini.happyteacherapp.extension.setVisibilityGone
import org.jnanaprabodhini.happyteacherapp.extension.setVisible
import org.jnanaprabodhini.happyteacherapp.model.ResourceHeader
import org.jnanaprabodhini.happyteacherapp.model.ContentCard

class ClassroomResourceViewerActivity : ResourceViewerActivity() {

    companion object {
        fun launch(from: Activity, classroomResourceRef: DocumentReference, resourceHeader: ResourceHeader) {
            val classroomResourcesViewerIntent = Intent(from, ClassroomResourceViewerActivity::class.java)

            classroomResourcesViewerIntent.apply {
                putExtra(CONTENT_REF_PATH, classroomResourceRef.path)
                putExtra(HEADER, resourceHeader)
            }
            from.startActivity(classroomResourcesViewerIntent)
        }
    }

    override val cardRecyclerAdapter: ResourceRecyclerAdapter by lazy {
        val options = FirestoreRecyclerOptions.Builder<ContentCard>()
                .setQuery(cardsRef.orderBy(getString(R.string.order_number)), ContentCard::class.java).build()

        ClassroomResourceRecyclerAdapter(options, attachmentDestinationDirectory, header.subtopic, this, this)
    }

    override fun onError(e: FirebaseFirestoreException?) {
        cardRecyclerView.setVisibilityGone()
        statusTextView.setVisible()
        statusTextView.setText(R.string.there_was_an_error_loading_this_classroom_resource)
    }

    override fun onDataEmpty() {
        cardRecyclerView.setVisibilityGone()
        statusTextView.setText(R.string.this_classroom_resource_is_empty)
    }

    override fun onDataNonEmpty() {
        cardRecyclerView.setVisible()
        statusTextView.setVisibilityGone()
    }
}