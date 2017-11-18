package org.jnanaprabodhini.happyteacherapp.adapter

import android.support.v4.view.PagerAdapter
import android.view.View
import android.support.v4.view.ViewPager
import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.ImageView
import kotlinx.android.synthetic.main.image_item_fullscreen.view.*
import org.jnanaprabodhini.happyteacherapp.R
import org.jnanaprabodhini.happyteacherapp.extension.loadFullImageWithNoPlaceholder

/**
 * A view pager adapter for loading images in the full screen gallery view.
 */
class GalleryViewPagerAdapter(val imageUrls: Array<String>): PagerAdapter() {

    override fun getCount(): Int = imageUrls.size

    override fun isViewFromObject(view: View, keyObject: Any): Boolean = view == keyObject as ImageView

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val imagePage = LayoutInflater.from(container.context).inflate(R.layout.image_item_fullscreen, container, false)
        val imageUrl = imageUrls[position]

        imagePage.fullscreenImageView.loadFullImageWithNoPlaceholder(imageUrl)
        container.addView(imagePage)

        return imagePage
    }

    override fun destroyItem(container: ViewGroup, position: Int, keyObject: Any) {
        (container as ViewPager).removeView(keyObject as ImageView)
    }

    override fun getPageTitle(position: Int): CharSequence {
        if (count > 1) {
            return "${position + 1} / $count"
        } else {
            return ""
        }
    }
}