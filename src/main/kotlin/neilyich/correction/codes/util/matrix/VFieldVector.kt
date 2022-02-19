package neilyich.correction.codes.util.matrix

import neilyich.field.Field
import neilyich.field.element.FieldElement

class VFieldVector<Element: FieldElement>(
    field: Field<Element>,
    private val content: List<Element>
): AFieldMatrix<Element>(field) {

    override fun width(): Int = 1

    override fun height(): Int = content.size

    override fun get(row: Int, col: Int): Element {
        checkBounds(row, col)
        return content[row]
    }

    override fun get(row: Int): Iterable<Element> {
        checkRowBounds(row)
        return listOf(content[row])
    }

    override fun with(row: Int, col: Int, value: Element): VFieldVector<Element> {
        checkBounds(row, col)
        val newContent = content.toMutableList()
        newContent[row] = value
        return VFieldVector(field, newContent.toList())
    }

    override fun plus(other: AFieldMatrix<Element>): VFieldVector<Element> {
        checkPlusBounds(other)
        return VFieldVector(field, content.mapIndexed { i, el ->
            field.add(el, other[i, 0])
        })
    }

    override fun times(other: AFieldMatrix<Element>): AFieldMatrix<Element> {
        checkMultiplyBounds(other)
        return FieldMatrix(field, content.map { listOf(it) }) * other
    }

    override fun unaryMinus(): VFieldVector<Element> {
        return VFieldVector(field, content.map { field.inverseAdd(it) })
    }

    override fun clone(): VFieldVector<Element> = this

    override fun mult(coef: Element): VFieldVector<Element> {
        return VFieldVector(field, content.map { field.mult(it, coef) })
    }
}