package org.jnanaprabodhini.happyteacher.adapter

import android.support.v4.view.PagerAdapter
import android.view.View
import android.widget.RelativeLayout
import android.support.v4.view.ViewPager
import android.view.ViewGroup
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.image_item_fullscreen.view.*
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.extension.loadImage
import org.jnanaprabodhini.happyteacher.extension.loadImageWithNoCrop


/**
 * Created by grahamearley on 9/28/17.
 */
class GalleryViewPagerAdapter(val imageUrls: Array<String>): PagerAdapter() {

    override fun getCount(): Int = imageUrls.size

    override fun isViewFromObject(view: View, keyObject: Any): Boolean = view == keyObject as FrameLayout

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val imagePage = LayoutInflater.from(container.context).inflate(R.layout.image_item_fullscreen, container, false)
        val imageUrl = imageUrls[position]

        imagePage.fullscreenImageView.loadImageWithNoCrop(imageUrl)
        container.addView(imagePage)

        return imagePage
    }

    override fun destroyItem(container: ViewGroup, position: Int, keyObject: Any) {
        (container as ViewPager).removeView(keyObject as FrameLayout)
    }
}