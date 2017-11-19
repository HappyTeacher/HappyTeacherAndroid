package org.jnanaprabodhini.happyteacherapp.adapter.viewholder

import android.support.annotation.ColorRes
import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.list_item_contribution_header_card.view.*
import org.jnanaprabodhini.happyteacherapp.R
import org.jnanaprabodhini.happyteacherapp.extension.setBackgroundColorRes
import org.jnanaprabodhini.happyteacherapp.extension.setVisible
import org.jnanaprabodhini.happyteacherapp.util.ResourceType

/**
 * A parent viewholder for resource headers. Used to centralize
 *  logic for coloring the view's resourceType color bar.
 */
abstract class BaseResourceHeaderViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    abstract val resourceColorBar: View

    fun setTextColorForResourceType(type: String?) {
        when (type) {
            ResourceType.LESSON -> setColorBarForLessonResource()
            ResourceType.CLASSROOM_RESOURCE -> setColorBarForClassroomResource()
        }
    }

    protected fun setColorBarForLessonResource() {
        showColorBarWithColor(R.color.deepLightBlue)
    }

    protected fun setColorBarForClassroomResource() {
        showColorBarWithColor(R.color.grassGreen)
    }

    private fun showColorBarWithColor(@ColorRes colorId: Int) {
        resourceColorBar.setVisible()
        resourceColorBar.setBackgroundColorRes(colorId)
    }
}