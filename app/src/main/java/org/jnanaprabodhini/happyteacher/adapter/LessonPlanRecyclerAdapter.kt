package org.jnanaprabodhini.happyteacher.adapter

import android.app.Activity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import org.jnanaprabodhini.happyteacher.R
import org.jnanaprabodhini.happyteacher.extension.*
import org.jnanaprabodhini.happyteacher.model.LessonCard
import org.jnanaprabodhini.happyteacher.adapter.viewholder.LessonCardViewHolder


/**
 * Created by grahamearley on 9/25/17.
 */
class LessonPlanRecyclerAdapter(val lessonCards: List<LessonCard>, val activity: Activity): RecyclerView.Adapter<LessonCardViewHolder>() {

    override fun getItemCount(): Int = lessonCards.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): LessonCardViewHolder {
        val cardView = LayoutInflater.from(parent?.context).inflate(R.layout.list_item_lesson_card, parent, false)
        return LessonCardViewHolder(cardView)
    }

    override fun onBindViewHolder(holder: LessonCardViewHolder?, position: Int) {
        val card = lessonCards[position]

        resetViewVisibility(holder)
        setupText(card, holder)

        if (card.youtubeId.isNotEmpty()) {
            setupYoutubePlayer(card.youtubeId, holder)
        } else if (card.imageUrls.isNotEmpty()) {
            setupImages(card.getCardImageUrls(), holder)
        }
    }

    private fun resetViewVisibility(holder: LessonCardViewHolder?) {
        holder?.headerMediaFrame?.setVisibilityGone()
        holder?.youtubeWebView?.setVisibilityGone()
        holder?.headerImageView?.setVisibilityGone()
        holder?.imageScrollArrowView?.setVisibilityGone()
        holder?.imageGalleryRecyclerView?.setVisibilityGone()
    }

    private fun setupText(card: LessonCard, holder: LessonCardViewHolder?) {
        if (card.header.isNotEmpty()) {
            holder?.headerTextView?.setVisible()
            holder?.headerTextView?.text = card.header
        } else {
            holder?.headerTextView?.setVisibilityGone()
        }

        if (card.body.isNotEmpty()) {
            holder?.bodyTextView?.setVisible()
            holder?.bodyTextView?.setHtmlText(card.body)
        } else {
            holder?.bodyTextView?.setVisibilityGone()
        }
    }

    private fun setupYoutubePlayer(youtubeId: String, holder: LessonCardViewHolder?) {
        holder?.headerMediaFrame?.setVisible()
        holder?.youtubeWebView?.setVisible()
        holder?.youtubeWebView?.loadYoutubeVideo(youtubeId)
    }

    private fun setupImages(imageUrls: List<String>, holder: LessonCardViewHolder?) {

        if (imageUrls.size == 1) {
            holder?.headerMediaFrame?.setVisible()
            holder?.headerImageView?.setVisible()
            holder?.headerImageView?.loadImage(imageUrls.first())
        } else {
            holder?.headerMediaFrame?.setVisible()
            setupImageGalleryRecycler(imageUrls, holder)
        }
    }

    private fun setupImageGalleryRecycler(imageUrls: List<String>, holder: LessonCardViewHolder?) {
        val recycler = holder?.imageGalleryRecyclerView
        recycler?.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        recycler?.adapter = ImageGalleryRecyclerAdapter(imageUrls, activity)

        recycler?.setVisible()

        // Show an arrow to indicate to the user that this is scrollable
        holder?.imageScrollArrowView?.setVisible()
        holder?.imageScrollArrowView?.setOnClickListener {
            // Scroll right by half of a single images width:
            val dx = activity.resources.getDimensionPixelOffset(R.dimen.card_image_gallery_width) / 2
            val dy = 0
            recycler?.smoothScrollBy(dx, dy)
        }
        recycler?.onHorizontalScroll {
            // Once a user scrolls, hide the arrow
            holder.imageScrollArrowView.setVisibilityGone()
        }
    }
}

