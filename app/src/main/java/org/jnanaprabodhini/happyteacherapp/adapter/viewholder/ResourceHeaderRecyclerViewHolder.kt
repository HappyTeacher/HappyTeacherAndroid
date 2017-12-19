package org.jnanaprabodhini.happyteacherapp.adapter.viewholder

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.list_item_content_header_recycler.view.*
import org.jnanaprabodhini.happyteacherapp.R
import org.jnanaprabodhini.happyteacherapp.activity.ContributeActivity
import org.jnanaprabodhini.happyteacherapp.activity.SettingsActivity
import org.jnanaprabodhini.happyteacherapp.activity.SubtopicWriteChoiceActivity
import org.jnanaprabodhini.happyteacherapp.activity.base.BottomNavigationActivity
import org.jnanaprabodhini.happyteacherapp.activity.base.HappyTeacherActivity
import org.jnanaprabodhini.happyteacherapp.extension.hasCompleteContributorProfile
import org.jnanaprabodhini.happyteacherapp.extension.setVisibilityGone
import org.jnanaprabodhini.happyteacherapp.extension.setVisible
import org.jnanaprabodhini.happyteacherapp.extension.showToast
import org.jnanaprabodhini.happyteacherapp.view.HorizontalPagerRecyclerView

class ResourceHeaderRecyclerViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val titleTextView: TextView = itemView.titleTextView
    val horizontalRecyclerView: HorizontalPagerRecyclerView = itemView.horizontalRecyclerView
    val statusTextView: TextView = itemView.statusTextView
    val contributeButton: Button = itemView.contributeButton
    val progressBar: View = itemView.progressBar

    fun hideEmptyViews() {
        statusTextView.setVisibilityGone()
        contributeButton.setVisibilityGone()
    }

    fun showEmptyViewWithContributeButton(resourceType: String, topicId: String, activity: HappyTeacherActivity) {
        statusTextView.setVisible()
        contributeButton.setVisible()

        contributeButton.setOnClickListener {
            val user = FirebaseAuth.getInstance().currentUser

            if (user?.hasCompleteContributorProfile(activity) == true) {
                SubtopicWriteChoiceActivity.launch(activity, resourceType, topicId)
            } else {
                ContributeActivity.launch(activity)
            }
        }
    }
}