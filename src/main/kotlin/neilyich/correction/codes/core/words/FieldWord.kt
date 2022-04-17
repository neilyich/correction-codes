package neilyich.correction.codes.core.words

import neilyich.field.Field
import neilyich.field.element.FieldElement
import neilyich.util.matrix.HFieldVector
import neilyich.util.matrix.VFieldVector

class FieldWord<Element: FieldElement>(
    private val field: Field<Element>,
    private val content: List<Element>
): Word<Element> {

    override fun length(): Int = content.size

    override fun get(i: Int): Element = content[i]

    override fun content(): List<Element> = content

    fun field(): Field<Element> = field

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

    fun toVVector(): VFieldVector<Element> = VFieldVector(field, content)

    fun toHVector(): HFieldVector<Element> = HFieldVector(field, content)

    override fun with(i: Int, errorValue: Element): Word<Element> {
        return with(i, errorValue) { oldValue, error -> field.add(oldValue, error) }
    }

    override fun with(i: Int, errorValue: Element, compute: (oldValue: Element, error: Element) -> Element): Word<Element> {
        val newContent = content.toMutableList()
        newContent[i] = compute(content[i], errorValue)
        return FieldWord(field, newContent)
    }

    override fun toString(): String = content().toString()
}
