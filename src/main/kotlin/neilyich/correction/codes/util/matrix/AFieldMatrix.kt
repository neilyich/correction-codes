package neilyich.correction.codes.util.matrix

import neilyich.field.Field
import neilyich.field.element.FieldElement

abstract class AFieldMatrix<Element: FieldElement>(protected val field: Field<Element>): Cloneable {
    abstract fun width(): Int
    abstract fun height(): Int
    abstract operator fun get(row: Int, col: Int): Element
    abstract operator fun get(row: Int): Iterable<Element>
    abstract fun with(row: Int, col: Int, value: Element): AFieldMatrix<Element>
    abstract operator fun plus(other: AFieldMatrix<Element>): AFieldMatrix<Element>
    abstract operator fun times(other: AFieldMatrix<Element>): AFieldMatrix<Element>
    abstract fun mult(coef: Element): AFieldMatrix<Element>
    abstract operator fun unaryMinus(): AFieldMatrix<Element>

    operator fun minus(other: AFieldMatrix<Element>): AFieldMatrix<Element> = this.plus(-other)

    public abstract override fun clone(): AFieldMatrix<Element>

    fun column(col: Int): Iterable<Element> = (0 until height()).map { this[it, col] }

    fun isZero(): Boolean {
        for (row in 0 until height()) {
            for (col in 0 until width()) {
                if (this[row, col].isZero()) {
                    return true
                }
            }
        }
        return false
    }

    protected fun checkBounds(row: Int, col: Int) {
        checkRowBounds(row)
        checkColBounds(col)
    }
    protected fun checkRowBounds(row: Int) {
        if (row >= height()) {
            throw IndexOutOfBoundsException("row($row) >= height(${height()})")
        }
    }
    protected fun checkColBounds(col: Int) {
        if (col >= width()) {
            throw IndexOutOfBoundsException("col($col) >= width(${width()})")
        }
    }
    protected fun checkPlusBounds(other: AFieldMatrix<Element>) {
        if (other.width() != width() || other.height() != height()) {
            throw IllegalArgumentException("unable to add matrix of different dimensions")
        }
    }
    protected fun checkMultiplyBounds(other: AFieldMatrix<Element>) {
        if (width() != other.height()) {
            throw IllegalArgumentException("unable to multiply matrix of different dimensions")
        }
    }

    override fun toString(): String {
        val builder = StringBuilder("[\n")
        for (row in 0 until height()) {
            val rowBuilder = StringBuilder("[")
            for (col in 0 until width()) {
                rowBuilder.append(this[row, col].toString()).append(", ")
            }
            builder.append(rowBuilder.removeSuffix(", ")).append("]\n")
        }
        builder.append("]")
        return builder.toString()
    }
}