package org.jnanaprabodhini.happyteacher.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_subtopic_submissions_list.*
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.activity.base.HappyTeacherActivity
import org.jnanaprabodhini.happyteacher.adapter.firestore.LessonHeaderRecyclerAdapter
import org.jnanaprabodhini.happyteacher.adapter.helper.FirebaseDataObserver
import org.jnanaprabodhini.happyteacher.extension.setVisibilityGone
import org.jnanaprabodhini.happyteacher.extension.setVisible
import org.jnanaprabodhini.happyteacher.model.ResourceHeader

class SubtopicSubmissionsListActivity : HappyTeacherActivity(), FirebaseDataObserver {

    companion object IntentExtraHelper {
        fun launch(from: Activity, subtopicId: String) {
            val subtopicSubmissionsIntent = Intent(from, SubtopicSubmissionsListActivity::class.java)

            subtopicSubmissionsIntent.apply {
                putExtra(SubtopicSubmissionsListActivity.SUBTOPIC_KEY, subtopicId)
            }

            from.startActivity(subtopicSubmissionsIntent)
        }

        private const val SUBTOPIC_KEY: String = "SUBTOPIC_KEY"
        fun Intent.getSubtopicKey(): String = getStringExtra(SUBTOPIC_KEY)
    }

    private val subtopicKey: String by lazy {
        intent.getSubtopicKey()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subtopic_submissions_list)

        initializeRecyclerViewForSubtopic()
    }

    private fun initializeRecyclerViewForSubtopic() {

        val submissionHeadersQuery = firestoreLocalized.collection(getString(R.string.resources))
                .whereEqualTo(getString(R.string.resource_type), getString(R.string.lesson))
                .whereEqualTo(getString(R.string.subtopic), subtopicKey)
                .whereEqualTo(getString(R.string.status), getString(R.string.status_published))
                .orderBy(getString(R.string.is_featured), Query.Direction.ASCENDING)

        val adapterOptions = FirestoreRecyclerOptions.Builder<ResourceHeader>()
                .setQuery(submissionHeadersQuery, ResourceHeader::class.java).build()

        val shouldShowSubmissionsCount = false
        val adapter = LessonHeaderRecyclerAdapter(shouldShowSubmissionsCount, adapterOptions, this, this)
        adapter.startListening()

        submissionRecyclerView.layoutManager = LinearLayoutManager(this)
        submissionRecyclerView.adapter = adapter
    }

    override fun onRequestNewData() {
        submissionRecyclerView.setVisibilityGone()
        statusTextView.setVisibilityGone()

        progressBar.setVisible()
    }

    override fun onDataLoaded() {
        submissionRecyclerView.setVisible()

        statusTextView.setVisibilityGone()
        progressBar.setVisibilityGone()
    }

    override fun onDataEmpty() {
        submissionRecyclerView.setVisibilityGone()
        progressBar.setVisibilityGone()

        statusTextView.setVisible()
        statusTextView.setText(R.string.there_are_no_other_lessons)
    }

    override fun onError(e: FirebaseFirestoreException?) {
        submissionRecyclerView.setVisibilityGone()
        progressBar.setVisibilityGone()

        statusTextView.setVisible()
        statusTextView.setText(R.string.there_was_an_error_loading_the_other_lessons)
    }
}
