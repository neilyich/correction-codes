package neilyich.correction.codes.hamming

import neilyich.correction.codes.core.LinearFieldCode
import neilyich.correction.codes.core.words.FieldWord
import neilyich.correction.codes.util.matrix.AFieldMatrix
import neilyich.correction.codes.util.matrix.FieldMatrix
import neilyich.correction.codes.util.matrix.VFieldVector
import neilyich.field.Field
import neilyich.field.element.FieldElement
import neilyich.util.FieldPolynomialUtils
import kotlin.math.pow

class HammingCode<Element: FieldElement>(
    private val field: Field<Element>,
    private val m: Int
): LinearFieldCode<Element>() {
    private val n: Int = (field.size().toDouble().pow(m).toInt() - 1) / (field.size() - 1)
    private val k: Int = n - m
    private val checkIndexes: Set<Int> = getCheckIndexes()
    private val checkIndexesOrdered: List<Int> = checkIndexes.sorted()

    override fun encode(info: FieldWord<Element>): FieldWord<Element> {
        checkInfoWord(info)
        val extendedWord = extendInfoWord(info)
        var currentCodeVector = VFieldVector(field, extendedWord.content())
        val syndrome = H() * currentCodeVector
        checkIndexesOrdered.forEachIndexed { i, position ->
            currentCodeVector = currentCodeVector.with(position, 0, field.inverseAdd(syndrome[i, 0]))
        }
        return FieldWord(field, currentCodeVector.column(0).toList())
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

    override fun decode(word: FieldWord<Element>): FieldWord<Element> {
        checkEncodedWord(word)
        val syndrome = H() * VFieldVector(field, word.content())
        val firstNotZeroIndex = syndrome.column(0).indexOfFirst { !it.isZero() }
        val codeWord: FieldWord<Element>
        if (firstNotZeroIndex == -1) {
            codeWord = word
        } else {
            val notZeroElement = syndrome[firstNotZeroIndex, 0]
            val errorValue = field.inverseMult(notZeroElement)
            val column = syndrome.mult(errorValue)
            val degree = m - 1 - firstNotZeroIndex
            var errorPosition = (field.size().toDouble().pow(degree).toInt() - 1) / (field.size() - 1)
            var k = 1
            for (i in 0 until degree) {
                errorPosition += k * (column[m - 1 - i, 0].discreteLogarithm()?.let { it + 1 } ?: 0)
                k *= field.size()
            }
            val wordVector = VFieldVector(field, word.content())
            val correctedWordVector =
                wordVector.with(errorPosition, 0, field.sub(wordVector[errorPosition, 0], errorValue))
            codeWord = FieldWord(field, correctedWordVector.column(0).toList())
        }
        return FieldWord(field, codeWord.content().filterIndexed { index, _ -> index !in checkIndexes })
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

    override fun dualCode(): LinearFieldCode<Element> {
        TODO("Not yet implemented")
    }

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
        TODO("Not yet implemented")
    }
}