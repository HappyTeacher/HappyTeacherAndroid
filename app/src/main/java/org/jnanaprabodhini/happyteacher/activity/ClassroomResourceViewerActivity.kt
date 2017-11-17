package org.jnanaprabodhini.happyteacher.activity

import android.app.Activity
import android.content.Intent
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.android.synthetic.main.activity_card_list_content_viewer.*
import kotlinx.android.synthetic.main.view_recycler_horizontal_pager.*
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.adapter.contentlist.ResourceContentRecyclerAdapter
import org.jnanaprabodhini.happyteacher.adapter.contentlist.ClassroomResourceRecyclerAdapter
import org.jnanaprabodhini.happyteacher.extension.setDrawableResource
import org.jnanaprabodhini.happyteacher.extension.setVisibilityGone
import org.jnanaprabodhini.happyteacher.extension.setVisible
import org.jnanaprabodhini.happyteacher.model.ResourceHeader
import org.jnanaprabodhini.happyteacher.model.ContentCard

class ClassroomResourceViewerActivity : ResourceContentViewerActivity() {

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

    override val cardRecyclerAdapter: ResourceContentRecyclerAdapter by lazy {
        val options = FirestoreRecyclerOptions.Builder<ContentCard>()
                .setQuery(cardsRef.orderBy(getString(R.string.order_number)), ContentCard::class.java).build()

        ClassroomResourceRecyclerAdapter(options, attachmentDestinationDirectory, header.subtopic, this, this)
    }

    override fun onError(e: FirebaseFirestoreException?) {
        recyclerView.setVisibilityGone()
        statusTextView.setVisible()
        statusTextView.setText(R.string.there_was_an_error_loading_this_classroom_resource)
    }
}