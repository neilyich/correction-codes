package neilyich.correction.codes.util.matrix

import neilyich.field.Field
import neilyich.field.element.FieldElement

class ZeroFieldMatrix<Element: FieldElement>(
    field: Field<Element>,
    private val height: Int,
    private val width: Int
): AFieldMatrix<Element>(field) {

    private val zero = field.zero()

    override fun width(): Int = width

    override fun height(): Int = height

    override fun get(row: Int, col: Int): Element {
        checkBounds(row, col)
        return zero
    }

    override fun get(row: Int): Iterable<Element> {
        checkRowBounds(row)
        return (1..width).map { zero }.toList()
    }

    override fun with(row: Int, col: Int, value: Element): AFieldMatrix<Element> {
        return FieldMatrix(field, (0 until height).map { r ->
            if (row == r) {
                (0 until width).map { c ->
                    if (col == c) value else zero
                }
            }
            else {
                (0 until width).map {
                    zero
                }
            }
        })
    }

    override fun plus(other: AFieldMatrix<Element>): AFieldMatrix<Element> {
        checkPlusBounds(other)
        return other.clone()
    }

    override fun times(other: AFieldMatrix<Element>): AFieldMatrix<Element> {
        checkMultiplyBounds(other)
        return this
    }

    override fun unaryMinus(): AFieldMatrix<Element> = this

    override fun clone(): AFieldMatrix<Element> = this

    override fun mult(coef: Element): AFieldMatrix<Element> = this

}