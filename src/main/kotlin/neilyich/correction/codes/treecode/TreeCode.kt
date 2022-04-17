package neilyich.correction.codes.treecode

import neilyich.correction.codes.core.Code
import neilyich.correction.codes.core.exceptions.DecodingException
import neilyich.correction.codes.core.words.SimpleWord
import neilyich.correction.codes.core.words.Word
import neilyich.correction.codes.core.words.toBinaryString
import neilyich.correction.codes.util.BinaryCodeTree
import java.lang.Exception

abstract class TreeCode<T: Comparable<T>>(
    val alphabetFrequency: Map<T, Double>,
): Code<T, Boolean, Word<T>, Word<Boolean>> {

    abstract val codeTree: BinaryCodeTree<T>

    override fun encode(info: Word<T>): Word<Boolean> {
        val encodedWord = info.content().stream().flatMap { codeTree.encode(it).stream() }.toList()
        return SimpleWord(encodedWord)
    }

    override fun decode(word: Word<Boolean>): Word<T> {
        try {
            return SimpleWord(codeTree.decode(word))
        } catch (e: Exception) {
            throw DecodingException(word, e.message, e)
        }
    }

    override fun infoWordLength(): Int {
        throw UnsupportedOperationException("$javaClass does not have fixed info word length")
    }

    override fun encodedWordLength(): Int {
        throw UnsupportedOperationException("$javaClass code does not have fixed encoded word length")
    }

    fun printInfo() {
        codeTree.print()
        println(alphabetFrequency.keys.sorted().associateWith { s ->
            codeTree.encode(s).toBinaryString()
        })
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TreeCode<*>) return false

        if (alphabetFrequency != other.alphabetFrequency) return false
        if (codeTree != other.codeTree) return false

        return true
    }

    override fun hashCode(): Int {
        var result = alphabetFrequency.hashCode()
        result = 31 * result + codeTree.hashCode()
        return result
    }
}