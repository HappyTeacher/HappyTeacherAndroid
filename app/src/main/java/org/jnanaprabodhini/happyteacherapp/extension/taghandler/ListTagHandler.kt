package org.jnanaprabodhini.happyteacherapp.extension.taghandler

import android.text.Editable
import android.text.Html
import android.text.style.LeadingMarginSpan
import org.xml.sax.XMLReader

/**
 * An abstract base class for handling list HTML tags (<ul> and <ol>) in a TextView.
 */
abstract class ListTagHandler(private val indentationLevel: Int = 0): Html.TagHandler {

    /**
     * The tag that the implementation of this abstract class is
     *  designed to handle. ("ul", "ol")
     */
    abstract val tag: String

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
        if (this.hasListHandler()
                && activeListHandler?.hasListHandler() == false
                && !opening
                && tag == activeListHandler?.tag) {

            // If we're closing the list tag of the active list handler
            // (and not *its* active list handler), remove it.
            activeListHandler = null

        } else if (this.hasListHandler()) {
            // Delegate handling of this tag to the list handler!
            activeListHandler?.handleTag(opening, tag, output, xmlReader)
        } else {

            when (tag) {
                "li" -> handleListItem(opening, tag, output, xmlReader)
                "ul" -> {
                    // At the root level, there is no need for an extra newline.
                    //  At other levels, newline is required for spans.
                    if (this !is RootListTagHandler) output?.append("\n")
                    activeListHandler = UnorderedListTagHandler(indentationLevel + 1)
                }
                "ol" -> {
                    if (this !is RootListTagHandler) output?.append("\n")
                    activeListHandler = OrderedListTagHandler(indentationLevel + 1)
                }
            }
        }
    }

    private fun hasListHandler() = activeListHandler != null

    fun getIndentationSpan(): LeadingMarginSpan.Standard {
        val indentationAmount = indentationLevel * indentationMultiplier

        return LeadingMarginSpan.Standard(indentationAmount)
    }

    /**
     * Subclasses will use this function to display a list item from a tag.
     */
    open fun handleListItem(opening: Boolean, tag: String, output: Editable?, xmlReader: XMLReader?) {}
}