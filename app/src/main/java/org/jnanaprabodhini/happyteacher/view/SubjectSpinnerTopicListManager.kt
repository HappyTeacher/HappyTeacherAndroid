package org.jnanaprabodhini.happyteacher.view

import android.support.annotation.LayoutRes
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
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
 * A class that manages a pair of spinners (containing child/parents subjects)
 *  and the list that is populated depending on the selection from the bottommost
 *  spinner.
 *
 *  This is used for displaying a list of Topics controlled by two subject spinners.
 */
class SubjectSpinnerTopicListManager(private val parentSpinner: Spinner,
                                     private val childSpinner: Spinner,
                                     val progressBar: ProgressBar,
                                     val activity: HappyTeacherActivity,
                                     private val onSpinnerSelectionsComplete: (String) -> Unit) {

    var parentSpinnerSelectionIndex = 0
    var childSpinnerSelectionIndex = 0

    fun initializeSpinners() {
        setupParentSpinner()
    }

    private fun setupParentSpinner() {
        // Parent subjects have no parents, so pass null as value for parentSubject:
        setupSpinner(parentSpinner, R.layout.spinner_item, null, parentSpinnerSelectionIndex)
        parentSpinnerSelectionIndex = 0
    }

    /**
     *  Set up one of the two spinners (the parent spinner or the child spinner).
     */
    private fun setupSpinner(spinner: Spinner, @LayoutRes spinnerLayout: Int, parentSubjectId: String?, selectionIndex: Int) {
        val subjectQuery = activity.firestoreLocalized.collection(activity.getString(R.string.subjects)).whereEqualTo(activity.getString(R.string.parent_subject), parentSubjectId)

        val spinnerDataObserver = object: FirebaseDataObserver {
            override fun onDataNonEmpty() {
                progressBar.setVisibilityGone()
                spinner.setVisible()
            }
            // todo: add on empty, error, etc.
        }

        val adapter = object: FirestoreObserverListAdapter<Subject>(subjectQuery, Subject::class.java, spinnerLayout, spinnerDataObserver, activity) {
            override fun populateView(view: View, model: Subject, position: Int) {
                (view as TextView).text = model.name
            }
        }

        adapter.startListening()
        spinner.adapter = adapter

        spinner.selectIndexWhenPopulated(selectionIndex)

        spinner.onItemSelected { position ->
            val subject = adapter.getItem(position)
            val selectedSubjectKey = adapter.getItemKey(position)

            if (subject.hasChildren) {
                setupSpinner(childSpinner, R.layout.spinner_item_child, selectedSubjectKey, childSpinnerSelectionIndex)
                childSpinnerSelectionIndex = 0
            } else if (!subject.hasChildren && spinner == childSpinner) {
                onSpinnerSelectionsComplete(selectedSubjectKey)
            } else {
                onSpinnerSelectionsComplete(selectedSubjectKey)
                childSpinner.setVisibilityGone()
            }
        }
    }
}