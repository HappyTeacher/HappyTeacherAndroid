package org.jnanaprabodhini.happyteacher.extension.taghandler

import android.text.Editable
import android.text.Html
import org.xml.sax.XMLReader

/**
 * Created by grahamearley on 9/26/17.
 */
class RootListTagHandler : ListTagHandler() {

    override val TAG: String = ""

    override fun handleTag(opening: Boolean, tag: String?, output: Editable?, xmlReader: XMLReader?) {
        super.handleTag(opening, tag, output, xmlReader)

        if (!opening && (tag == "ol" || tag == "ul")
                && activeListHandler == null) {
            output?.append("\n")
        }
    }

    override fun handleListItem(opening: Boolean, tag: String, output: Editable?, xmlReader: XMLReader?) {
        // Root level should not receive <li> tags outside of <ul> or <ol>, but if we do, do nothing.
    }
}