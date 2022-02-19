package neilyich.correction.codes.util.matrix

import neilyich.field.Field
import neilyich.field.element.FieldElement

class HFieldVector<Element: FieldElement>(
    field: Field<Element>,
    private val content: List<Element>
): AFieldMatrix<Element>(field) {

    override fun width(): Int = content.size

    override fun height(): Int = 1

    override fun get(row: Int, col: Int): Element {
        checkBounds(row, col)
        return content[col]
    }

    override fun get(row: Int): Iterable<Element> {
        checkRowBounds(row)
        return content
    }

    override fun with(row: Int, col: Int, value: Element): HFieldVector<Element> {
        checkBounds(row, col)
        val newContent = content.toMutableList()
        newContent[col] = value
        return HFieldVector(field, newContent.toList())
    }

    override fun plus(other: AFieldMatrix<Element>): HFieldVector<Element> {
        checkPlusBounds(other)
        return HFieldVector(field, content.mapIndexed { col, el ->
            field.add(el, other[0, col])
        })
    }

    override fun times(other: AFieldMatrix<Element>): AFieldMatrix<Element> {
        checkMultiplyBounds(other)
        return FieldMatrix(field, listOf(content)) * other
    }

    override fun unaryMinus(): HFieldVector<Element> {
        return HFieldVector(field, content.map { field.inverseAdd(it) })
    }

    override fun clone(): HFieldVector<Element> = this

    override fun mult(coef: Element): HFieldVector<Element> {
        return HFieldVector(field, content.map { field.mult(it, coef) })
    }
}