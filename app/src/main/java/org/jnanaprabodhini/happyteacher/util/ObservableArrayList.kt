package org.jnanaprabodhini.happyteacher.util

/**
 * An ArrayList that calls a given function when items are added.
 */
class ObservableArrayList<T>(private val onAdd: (T) -> Unit): ArrayList<T>() {
    override fun add(index: Int, element: T) {
        onAdd(element)
        super.add(index, element)
    }

    override fun add(element: T): Boolean {
        onAdd(element)
        return super.add(element)
    }

    override fun addAll(elements: Collection<T>): Boolean {
        elements.forEach(onAdd)
        return super.addAll(elements)
    }

    override fun addAll(index: Int, elements: Collection<T>): Boolean {
        elements.forEach(onAdd)
        return super.addAll(index, elements)
    }
}