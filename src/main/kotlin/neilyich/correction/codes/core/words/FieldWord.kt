package neilyich.correction.codes.core.words

import neilyich.field.Field
import neilyich.field.element.FieldElement

class FieldWord<Element: FieldElement>(
    private val field: Field<Element>,
    private val content: List<Element>
): Word<Element> {

    override fun length(): Int = content.size

    override fun get(i: Int): Element = content[i]

    override fun content(): List<Element> = content

    fun field(): Field<Element> = field

    override fun toString(): String {
        val builder = StringBuilder("[")
        for (c in content) {
            builder.append(c.toString()).append(", ")
        }
        return builder.removeSuffix(", ").toString() + "]"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FieldWord<*>) return false

        if (field != other.field) return false
        if (content != other.content) return false

        return true
    }

    override fun hashCode(): Int {
        var result = field.hashCode()
        result = 31 * result + content.hashCode()
        return result
    }
}
