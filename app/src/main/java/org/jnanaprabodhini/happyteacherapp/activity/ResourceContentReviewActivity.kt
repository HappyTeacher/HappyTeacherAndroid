package org.jnanaprabodhini.happyteacherapp.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.google.firebase.firestore.DocumentReference
import kotlinx.android.synthetic.main.activity_card_list_content_viewer.*
import org.jnanaprabodhini.happyteacherapp.R
import org.jnanaprabodhini.happyteacherapp.extension.setDrawableResource
import org.jnanaprabodhini.happyteacherapp.extension.setTooltip
import org.jnanaprabodhini.happyteacherapp.extension.setVisibilityGone
import org.jnanaprabodhini.happyteacherapp.extension.setVisible
import org.jnanaprabodhini.happyteacherapp.model.ResourceHeader
import org.jnanaprabodhini.happyteacherapp.util.ResourceStatus

/**
 * Created by grahamearley on 11/20/17.
 */
class ResourceContentReviewActivity: CommentableResourceContentViewerActivity() {

    companion object {
        fun launch(from: Activity, resourceRef: DocumentReference, resourceHeader: ResourceHeader) {
            val intent = Intent(from, ResourceContentReviewActivity::class.java)

            intent.apply {
                putExtra(CONTENT_REF_PATH, resourceRef.path)
                putExtra(HEADER, resourceHeader)
            }
            from.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupFab()
    }

    private fun setupFab() {
        primaryFab.setVisible()
        secondaryFab.setVisibilityGone()

        // TODO: change action based on feedback status (publish if no comments, send feedback if comments)
        primaryFab.setDrawableResource(R.drawable.ic_check_white_24dp)
        primaryFab.setColorFilter(R.color.grassGreen)
    }

}