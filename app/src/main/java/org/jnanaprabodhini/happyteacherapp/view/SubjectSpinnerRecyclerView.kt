package org.jnanaprabodhini.happyteacherapp.view

import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView

/**
 * Created by grahamearley on 11/25/17.
 */
interface SubjectSpinnerRecyclerView {
    val parentSpinner: Spinner
    val childSpinner: Spinner
    val statusText: TextView
    val progressBar: ProgressBar
}