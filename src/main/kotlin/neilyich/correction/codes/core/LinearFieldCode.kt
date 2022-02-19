package neilyich.correction.codes.core

import neilyich.correction.codes.core.words.FieldWord
import neilyich.correction.codes.util.matrix.AFieldMatrix
import neilyich.field.element.FieldElement

abstract class LinearFieldCode<Element: FieldElement>: FieldCode<Element, Element> {
    abstract fun n(): Int
    abstract fun k(): Int
    abstract fun d(): Int

    final override fun encodedWordLength(): Int = k()
    final override fun infoWordLength(): Int = n()

    protected fun checkInfoWord(word: FieldWord<Element>) {
        if (word.length() != k()) {
            throw IllegalArgumentException("unable to encode word with length=${word.length()} (k=${k()}")
        }
    }
    protected fun checkEncodedWord(word: FieldWord<Element>) {
        if (word.length() != n()) {
            throw IllegalArgumentException("unable to decode word with length=${word.length()} (n=${n()}")
        }
    }

    private val H: AFieldMatrix<Element> by lazy { createH() }
    protected abstract fun createH(): AFieldMatrix<Element>
    fun H(): AFieldMatrix<Element> = H

    private val G: AFieldMatrix<Element> by lazy { createG() }
    protected abstract fun createG(): AFieldMatrix<Element>
    fun G(): AFieldMatrix<Element> = G

    abstract fun dualCode(): LinearFieldCode<Element>
}