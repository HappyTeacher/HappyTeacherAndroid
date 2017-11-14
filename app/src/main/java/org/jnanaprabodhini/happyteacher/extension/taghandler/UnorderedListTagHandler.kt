package org.jnanaprabodhini.happyteacher.extension.taghandler

import android.text.Editable
import android.text.Spannable
import org.xml.sax.XMLReader

/**
 * A ListTagHandler for unordered lists.
 */
class UnorderedListTagHandler(indentationLevel: Int = 0): ListTagHandler(indentationLevel) {

    override val tag: String = "ul"
    private var stringStart = 0
    private val bulletPrefix = if (indentationLevel % 2 == 0) "- " else "• "

    override fun handleListItem(opening: Boolean, tag: String, output: Editable?, xmlReader: XMLReader?) {
        if (opening) {
            stringStart = output?.length ?: 0
            output?.append(bulletPrefix)
        } else {
            if (output?.get(output.lastIndex) != '\n') output?.append('\n')

            output?.setSpan(getIndentationSpan(), stringStart, output.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }
}