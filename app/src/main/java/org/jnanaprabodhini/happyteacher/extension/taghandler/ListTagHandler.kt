package org.jnanaprabodhini.happyteacher.extension.taghandler

import android.text.Editable
import android.text.Html
import android.text.style.LeadingMarginSpan
import org.xml.sax.XMLReader

/**
 * An abstract class for handling list HTML tags (<ul> and <ol>) in a TextView.
 */
abstract class ListTagHandler(val indentationLevel: Int = 0) : Html.TagHandler {

    /**
     * TAG represents the tag that the implementation of this abstract class is
     *  designed to handle. ("ul", "ol")
     */
    abstract val TAG: String

    /**
     * The activeListHandler is an implementation of ListTagHandler
     *  that is handling the tags at the current level of nesting in
     *  the input HTML text.
     */
    var activeListHandler: ListTagHandler? = null

    private val indentationMultiplier = 15

    /**
     * Determine which tag handler should handle the most recently read tag.
     */
    override fun handleTag(opening: Boolean, tag: String?, output: Editable?, xmlReader: XMLReader?) {
        if (activeListHandler != null && activeListHandler?.activeListHandler == null
                && !opening && tag == activeListHandler!!.TAG) {

            // If we're closing the list tag on the active list handler
            // (and not on its active list handler), remove it.
            activeListHandler = null

        } else if (activeListHandler != null) {
            // Let the <ul> or <ol> list handler handle this tag!
            activeListHandler!!.handleTag(opening, tag, output, xmlReader)
        } else if (tag == "ul") {
            output?.append("\n")
            // Set <ul> handler as active
            activeListHandler = UnorderedListTagHandler(indentationLevel + 1)
        } else if (tag == "ol") {
            output?.append("\n")
            // Set <ol> handler as active
            activeListHandler = OrderedListTagHandler(indentationLevel + 1)
        } else if (tag == "li") {
            handleListItem(opening, tag, output, xmlReader)
        }
    }

    fun getIndentationSpan(): LeadingMarginSpan.Standard {
        val firstLineIndentation = indentationLevel * indentationMultiplier
        val remainingLinesIndentation = firstLineIndentation + indentationMultiplier

        return LeadingMarginSpan.Standard(firstLineIndentation, remainingLinesIndentation)
    }

    /**
     * Subclasses will use this function to display a list item from a tag.
     */
    abstract fun handleListItem(opening: Boolean, tag: String, output: Editable?, xmlReader: XMLReader?)

}