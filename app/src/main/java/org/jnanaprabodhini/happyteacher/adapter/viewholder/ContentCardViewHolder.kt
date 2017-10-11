package org.jnanaprabodhini.happyteacher.adapter.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.webkit.WebView
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.list_item_content_card.view.*
import org.jnanaprabodhini.happyteacher.view.DownloadBarView
import org.jnanaprabodhini.happyteacher.view.HorizontalPagerRecyclerView

/**
 * Created by grahamearley on 9/25/17.
 */
open class ContentCardViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val headerTextView: TextView = itemView.headerTextView
    val bodyTextView: TextView = itemView.bodyTextView

    val headerMediaFrame: FrameLayout = itemView.headerMediaFrame
    val headerImageView: ImageView = itemView.headerImageView
    val youtubeWebView: WebView = itemView.youtubePlayerWebView

    val imageGalleryRecyclerView: HorizontalPagerRecyclerView = itemView.imageGalleryRecyclerView

    val attachmentDownloadButton: DownloadBarView = itemView.attachmentDownloadBar
}