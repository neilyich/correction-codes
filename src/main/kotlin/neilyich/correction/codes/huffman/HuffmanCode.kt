package neilyich.correction.codes.huffman

import neilyich.correction.codes.serialization.CodeName
import neilyich.correction.codes.treecode.TreeCode

class HuffmanCode<T: Comparable<T>>(alphabetFrequency: Map<T, Double>): TreeCode<T>(alphabetFrequency) {
    override val codeTree = HuffmanCodeTree(alphabetFrequency)
    override fun name(): CodeName {
        return CodeName.HUFFMAN
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is HuffmanCode<*>) return false

        if (codeTree != other.codeTree) return false

        return true
    }

    override fun hashCode(): Int {
        return codeTree.hashCode()
    }
}