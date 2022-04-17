package neilyich.correction.codes.hamming

import neilyich.correction.codes.core.LinearCode
import neilyich.correction.codes.core.words.FieldWord
import neilyich.correction.codes.serialization.CodeName
import neilyich.field.Field
import neilyich.field.element.FieldElement
import neilyich.util.FieldPolynomialUtils
import neilyich.util.matrix.AFieldMatrix
import neilyich.util.matrix.FieldMatrix
import neilyich.util.matrix.VFieldVector
import neilyich.util.matrix.identityMatrix
import neilyich.util.pow
import kotlin.math.pow

class HammingCode<Element: FieldElement>(
    val field: Field<Element>,
    val m: Int
): LinearCode<Element>() {
    private val n: Int = (field.size().pow(m) - 1) / (field.size() - 1)
    private val k: Int = n - m
    private val checkIndexes: Set<Int> = getCheckIndexes()
    private val checkIndexesOrdered: List<Int> = checkIndexes.sorted()

    override fun encode(info: FieldWord<Element>): FieldWord<Element> {
        checkInfoWord(info)
        val extendedWord = extendInfoWord(info)
        var currentCodeVector = extendedWord.toVVector()
        val syndrome = H() * currentCodeVector
        checkIndexesOrdered.forEachIndexed { i, position ->
            currentCodeVector = currentCodeVector.with(position, 0, field.inverseAdd(syndrome[syndrome.height() - 1 - i, 0]))
        }
        return FieldWord(field, currentCodeVector.column(0).toList())
    }

    override fun decode(word: FieldWord<Element>): FieldWord<Element> {
        checkEncodedWord(word)
        val syndrome = H() * word.toVVector()
        val firstNotZeroIndex = syndrome.column(0).indexOfFirst { !it.isZero() }
        val codeWord: FieldWord<Element>
        if (firstNotZeroIndex == -1) {
            codeWord = word
        } else {
            val notZeroElement = syndrome[firstNotZeroIndex, 0]
            val errorValue = field.inverseMult(notZeroElement)
            val column = syndrome.mult(errorValue)
            val degree = m - 1 - firstNotZeroIndex
            var errorPosition = (field.size().pow(degree) - 1) / (field.size() - 1)
            var k = 1
            for (i in 0 until degree) {
                errorPosition += k * (field.discreteLogarithm(column[m - 1 - i, 0])?.let { it + 1 } ?: 0)
                k *= field.size()
            }
            val wordVector = VFieldVector(field, word.content())
            val correctedWordVector =
                wordVector.with(errorPosition, 0, field.sub(wordVector[errorPosition, 0], errorValue))
            codeWord = FieldWord(field, correctedWordVector.column(0).toList())
        }
        return FieldWord(field, codeWord.content().filterIndexed { index, _ -> index !in checkIndexes })
    }

    private fun extendInfoWord(info: FieldWord<Element>): FieldWord<Element> {
        var i = 0
        var j = 0
        val content = mutableListOf<Element>()
        while (i < info.length()) {
            if (j++ in checkIndexes) {
                content.add(field.zero())
            } else {
                content.add(info[i++])
            }
        }
        return FieldWord(field, content.toList())
    }

    private fun getCheckIndexes(): Set<Int> {
        var index = 1
        var k = field.size()
        val indexes = mutableSetOf(0)
        for (i in 1 until m) {
            indexes.add(index)
            index += k
            k *= field.size()
        }
        return indexes
    }

    override fun n(): Int = n

    override fun k(): Int = k

    override fun d(): Int = 3

    override fun createH(): AFieldMatrix<Element> {
        val content = (1..m).map { mutableListOf<Element>() }
        FieldPolynomialUtils.forAllPolynomials(field, 0 until m) { pol ->
            for (i in 0 until m) {
                content[i].add(pol[m - 1 - i])
            }
        }
        return FieldMatrix(field, content)
    }

    override fun createG(): AFieldMatrix<Element> {
        val checkIndexes = getCheckIndexes()
        val checkIndexesSorted = checkIndexes.toList().sorted()
        val replacements = mutableSetOf<Pair<Int, Int>>()
        var currentCheckIndexIndex = 0
        var h = H()
        for (i in 0 until m) {
            val currentCheckIndex = checkIndexesSorted[currentCheckIndexIndex]
            if (currentCheckIndex == n - 1 - i) {
                currentCheckIndexIndex++
                continue
            }
            replacements.add(currentCheckIndex to n - 1 - i)
            h = h.swapCols(currentCheckIndex, n - 1 - i)
            currentCheckIndexIndex++
        }
        println(h)
        val (a, _) = h.splitRight(k)
        var g = identityMatrix(field, k).concatRight((-a).transposed())
        println(g)
        for ((i, j) in replacements) {
            g = g.swapCols(i, j)
        }
        return g
    }

    override fun name(): CodeName {
        return CodeName.HAMMING
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is HammingCode<*>) return false

        if (field != other.field) return false
        if (m != other.m) return false

        return true
    }

    override fun hashCode(): Int {
        var result = field.hashCode()
        result = 31 * result + m
        return result
    }
}