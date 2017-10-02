package org.jnanaprabodhini.happyteacher.extension.taghandler

import android.text.Editable
import android.text.Html
import android.util.Log
import org.xml.sax.XMLReader

/**
 * Created by grahamearley on 9/26/17.
 */
abstract class ListTagHandler(val indentationLevel: Int = 0) : Html.TagHandler {

    abstract val TAG: String

    var activeListHandler: ListTagHandler? = null

    override fun handleTag(opening: Boolean, tag: String?, output: Editable?, xmlReader: XMLReader?) {
        if (activeListHandler != null && activeListHandler?.activeListHandler == null
                && !opening && tag == activeListHandler!!.TAG) {

            // If we're closing the list tag on the active list handler (and not on its active list handler), remove it.
            activeListHandler = null

            // There will be a newline added for a nested list last item
            //  and for the closing item surrounding the nested list.
            //  We only want one newline. (See root handler for diff handling)
            retractNewLine(output)

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

    fun getIndentationString(): String {
        val builder = StringBuilder()
        for (i in 0..indentationLevel) {
            builder.append("   ")
        }

        return builder.toString()
    }

    abstract fun handleListItem(opening: Boolean, tag: String, output: Editable?, xmlReader: XMLReader?)

    fun retractNewLine(output: Editable?) {
        val outputWithNewLineRemoved = output?.removeSuffix("\n")
        output?.clear()
        output?.append(outputWithNewLineRemoved)
    }
}