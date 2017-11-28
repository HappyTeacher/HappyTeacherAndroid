package org.jnanaprabodhini.happyteacherapp.view.manager

import android.support.v7.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import org.jnanaprabodhini.happyteacherapp.R
import org.jnanaprabodhini.happyteacherapp.activity.base.HappyTeacherActivity
import org.jnanaprabodhini.happyteacherapp.adapter.helper.FirebaseDataObserver
import org.jnanaprabodhini.happyteacherapp.model.Topic

/**
 * A class that manages the view logic of a list of topics, where
 *  each topic contains a list of resources (e.g. lessons, classroom resources).
 */
abstract class TopicListManager(protected val topicListRecycler: RecyclerView, val activity: HappyTeacherActivity, val dataObserver: FirebaseDataObserver) {

    protected val firestoreLocalized = activity.firestoreLocalized

    abstract fun updateListOfTopicsForSubject(subjectKey: String)

    open fun queryForTopicsWithSubject(subjectKey: String): Query {
        return firestoreLocalized.collection(activity.getString(R.string.topics))
                .whereEqualTo(activity.getString(R.string.subject), subjectKey)
    }

    protected fun getTopicAdapterOptionsForSubject(subjectKey: String): FirestoreRecyclerOptions<Topic> {
        val topicQuery = queryForTopicsWithSubject(subjectKey)

        return FirestoreRecyclerOptions.Builder<Topic>()
                .setQuery(topicQuery, Topic::class.java).build()
    }
}


