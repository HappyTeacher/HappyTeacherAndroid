package org.jnanaprabodhini.happyteacher.view

import android.support.annotation.LayoutRes
import android.view.View
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import com.google.firebase.firestore.Query
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.activity.base.HappyTeacherActivity
import org.jnanaprabodhini.happyteacher.adapter.firestore.FirestoreObserverListAdapter
import org.jnanaprabodhini.happyteacher.adapter.helper.FirebaseDataObserver
import org.jnanaprabodhini.happyteacher.extension.onItemSelected
import org.jnanaprabodhini.happyteacher.extension.selectIndexWhenPopulated
import org.jnanaprabodhini.happyteacher.extension.setVisibilityGone
import org.jnanaprabodhini.happyteacher.extension.setVisible
import org.jnanaprabodhini.happyteacher.model.Subject

/**
 * A class that manages the view logic of a pair of spinners (containing child/parents subjects).
 *
 *  This is used for displaying a list of Topics controlled by two subject spinners.
 */
class SubjectSpinnerManager(val activity: HappyTeacherActivity) {

    var parentSpinnerSelectionIndex = 0
    var childSpinnerSelectionIndex = 0

    private var parentSpinner: Spinner? = null
    private var childSpinner: Spinner? = null
    private var progressBar: ProgressBar? = null
    private var onSpinnerSelectionsComplete: (String) -> Unit = {}

    fun initializeSpinners(parentSpinner: Spinner, childSpinner: Spinner, progressBar: ProgressBar, onSpinnerSelectionsComplete: (String) -> Unit) {
        this.parentSpinner = parentSpinner
        this.childSpinner = childSpinner
        this.progressBar = progressBar
        this.onSpinnerSelectionsComplete = onSpinnerSelectionsComplete

        setupSpinners()
    }

    private fun setupSpinners() {
        setupParentSpinner()
    }

    private fun setupParentSpinner() {
        val parentAdapter = getParentSubjectAdapter()
        parentAdapter.startListening()
        parentSpinner?.adapter = parentAdapter

        parentSpinner?.selectIndexWhenPopulated(parentSpinnerSelectionIndex)

        parentSpinner?.onItemSelected { position ->
            parentSpinnerSelectionIndex = position

            val subject = parentAdapter.getItem(position)
            val selectedSubjectKey = parentAdapter.getItemKey(position)

            if (subject.hasChildren) {
                setupChildSpinner(selectedSubjectKey)
            } else {
                onSpinnerSelectionsComplete(selectedSubjectKey)
                childSpinner?.setVisibilityGone()
            }
        }
    }

    private fun setupChildSpinner(parentSubjectKey: String) {
        val childAdapter = getChildSubjectAdapter(parentSubjectKey)
        childAdapter.startListening()
        childSpinner?.adapter = childAdapter

        childSpinner?.selectIndexWhenPopulated(childSpinnerSelectionIndex)

        childSpinner?.onItemSelected { position ->
            childSpinnerSelectionIndex = position

            val selectedSubjectKey = childAdapter.getItemKey(position)
            onSpinnerSelectionsComplete(selectedSubjectKey)
        }
    }

    private fun getDataObserverForSpinner(spinner: Spinner?): FirebaseDataObserver {
        return object: FirebaseDataObserver {
            override fun onDataNonEmpty() {
                progressBar?.setVisibilityGone()
                spinner?.setVisible()
            }
            // todo: add on empty, error, etc.
        }
    }

    private fun getParentSubjectAdapter(): FirestoreObserverListAdapter<Subject> {
        val subjectQuery = activity.firestoreLocalized.collection(activity.getString(R.string.subjects))
                .whereEqualTo(activity.getString(R.string.parent_subject), null)
        val dataObserver = getDataObserverForSpinner(parentSpinner)

        return getSpinnerAdapterForQuery(subjectQuery, dataObserver, R.layout.spinner_item)
    }

    private fun getChildSubjectAdapter(parentSubject: String): FirestoreObserverListAdapter<Subject> {
        val subjectQuery = activity.firestoreLocalized.collection(activity.getString(R.string.subjects))
                .whereEqualTo(activity.getString(R.string.parent_subject), parentSubject)
        val dataObserver = getDataObserverForSpinner(childSpinner)

        return getSpinnerAdapterForQuery(subjectQuery, dataObserver, R.layout.spinner_item_child)
    }

    private fun getSpinnerAdapterForQuery(subjectQuery: Query, dataObserver: FirebaseDataObserver, @LayoutRes spinnerLayout: Int): FirestoreObserverListAdapter<Subject> {
        return object: FirestoreObserverListAdapter<Subject>(subjectQuery, Subject::class.java, spinnerLayout, dataObserver, activity) {
            override fun populateView(view: View, model: Subject, position: Int) {
                (view as TextView).text = model.name
            }
        }
    }
}