package org.jnanaprabodhini.happyteacher.extension.taghandler

import android.text.Editable
import android.util.Log
import org.xml.sax.XMLReader

/**
 * A ListTagHandler for unordered lists.
 */
class UnorderedListTagHandler(indentationLevel: Int = 0): ListTagHandler(indentationLevel) {

    override val TAG: String = "ul"

    override fun handleListItem(opening: Boolean, tag: String, output: Editable?, xmlReader: XMLReader?) {
        if (opening) {
            output?.append(getIndentationString())
            output?.append("• ")
        } else {
            output?.append("\n")
        }
    }
}