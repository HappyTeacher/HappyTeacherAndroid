package org.jnanaprabodhini.happyteacherapp.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentReference
import org.jnanaprabodhini.happyteacherapp.R
import org.jnanaprabodhini.happyteacherapp.adapter.contentlist.CommentableResourceRecyclerAdapter
import org.jnanaprabodhini.happyteacherapp.adapter.contentlist.ResourceContentRecyclerAdapter
import org.jnanaprabodhini.happyteacherapp.model.ContentCard
import org.jnanaprabodhini.happyteacherapp.model.ResourceHeader
import org.jnanaprabodhini.happyteacherapp.util.ResourceStatus

/**
 * Created by grahamearley on 11/20/17.
 */
open class CommentableResourceContentViewerActivity: ResourceContentViewerActivity() {

    companion object {
        fun launch(from: Activity, resourceRef: DocumentReference, resourceHeader: ResourceHeader) {
            val intent = Intent(from, CommentableResourceContentViewerActivity::class.java)

            intent.apply {
                putExtra(CONTENT_REF_PATH, resourceRef.path)
                putExtra(HEADER, resourceHeader)
            }
            from.startActivity(intent)
        }
    }

    override val cardRecyclerAdapter: ResourceContentRecyclerAdapter by lazy {
        val options = FirestoreRecyclerOptions.Builder<ContentCard>()
                .setQuery(cardsRef.orderBy(getString(R.string.order_number)), ContentCard::class.java).build()

        CommentableResourceRecyclerAdapter(options, attachmentDestinationDirectory, header.subtopic, this, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        when (header.status) {
            ResourceStatus.AWAITING_REVIEW -> supportActionBar?.setSubtitle(R.string.awaiting_review)
            ResourceStatus.CHANGES_REQUESTED -> supportActionBar?.setSubtitle(R.string.changes_requested)
        }
    }
}