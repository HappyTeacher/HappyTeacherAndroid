package org.jnanaprabodhini.happyteacher.adapter

import android.app.Activity
import com.google.firebase.database.Query
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.adapter.helper.FirebaseDataObserver

class ClassroomResourceHeaderRecyclerAdapter(topicName: String, lessonHeaderQuery: Query, activity: Activity, firebaseDataObserver: FirebaseDataObserver): SubtopicLessonHeaderRecyclerAdapter(topicName, lessonHeaderQuery, activity, firebaseDataObserver) {
    override val headerDataTypeKey: String = activity.getString(R.string.classroom_resources_key)
}