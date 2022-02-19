package neilyich.correction.codes.util.matrix

import neilyich.field.Field
import neilyich.field.element.FieldElement

class FieldMatrix<Element: FieldElement>(
    field: Field<Element>,
    private val content: List<List<Element>>
): AFieldMatrix<Element>(field), Cloneable {
    private val width: Int
    private val height: Int

    init {
        val h = content.size
        val w = if (content.isEmpty()) {
            0
        } else {
            content[0].size
        }
        for (row in content) {
            if (row.size != w) {
                throw IllegalArgumentException("rows of matrix must have same length")
            }
        }
        height = h
        width = w
    }

    override fun width(): Int = width

    override fun height(): Int = height

    override fun get(row: Int, col: Int): Element {
        checkBounds(row, col)
        return content[row][col]
    }

    override fun with(row: Int, col: Int, value: Element): AFieldMatrix<Element> {
        checkBounds(row, col)
        val newContent = content.toMutableList()
        val newRow = newContent[row].toMutableList()
        newRow[col] = value
        newContent[row] = newRow.toList()
        return FieldMatrix(field, newContent.toList())
    }

    override fun plus(other: AFieldMatrix<Element>): AFieldMatrix<Element> {
        checkPlusBounds(other)
        val newContent = content.mapIndexed { i, row ->
            row.mapIndexed { j, el ->
                field.add(el, other[i, j])
            }
        }
        return FieldMatrix(field, newContent)
    }

    override fun times(other: AFieldMatrix<Element>): AFieldMatrix<Element> {
        checkMultiplyBounds(other)
        val newContent = mutableListOf<List<Element>>()
        for (row in 0 until height) {
            val newRow = mutableListOf<Element>()
            for (col in 0 until other.width()) {
                var current = field.zero()
                for (i in 0 until width) {
                    current = field.add(current, field.mult(this[row, i], other[i, col]))
                }
                newRow.add(current)
            }
            newContent.add(newRow.toList())
        }
        return FieldMatrix(field, newContent.toList())
    }

    override fun unaryMinus(): AFieldMatrix<Element> {
        return FieldMatrix(field,
            content.map { row ->
                row.map { el ->
                    field.inverseAdd(el)
                }.toMutableList()
            }.toList()
        )
    }

    override fun clone(): FieldMatrix<Element> {
        return FieldMatrix(field, content.map { it.toList() })
    }

    override fun get(row: Int): Iterable<Element> {
        checkRowBounds(row)
        return content[row]
    }

    override fun mult(coef: Element): AFieldMatrix<Element> {
        return FieldMatrix(field, content.map { row -> row.map { field.mult(it, coef) } })
    }
}