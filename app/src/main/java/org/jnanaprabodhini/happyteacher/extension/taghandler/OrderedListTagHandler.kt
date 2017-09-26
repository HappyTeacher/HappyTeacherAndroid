package org.jnanaprabodhini.happyteacher.extension.taghandler

import android.text.Editable
import android.text.Html
import org.xml.sax.XMLReader

/**
 * Created by grahamearley on 9/26/17.
 */
class OrderedListTagHandler(indentationLevel: Int = 0): ListTagHandler(indentationLevel) {

    override val TAG: String = "ol"
    var listNumber: Int = 1

    override fun handleListItem(opening: Boolean, tag: String, output: Editable?, xmlReader: XMLReader?) {
        if (opening) {
            output?.append(getIndentationString())
            output?.append("$listNumber. ")

            listNumber++
        } else {
            output?.append("\n")
        }
    }
}