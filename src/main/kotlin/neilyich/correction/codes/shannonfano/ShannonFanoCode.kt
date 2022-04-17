package neilyich.correction.codes.shannonfano

import neilyich.correction.codes.serialization.CodeName
import neilyich.correction.codes.treecode.TreeCode

class ShannonFanoCode<T: Comparable<T>>(alphabetFrequency: Map<T, Double>): TreeCode<T>(alphabetFrequency) {
    override val codeTree = ShannonFanoCodeTree(alphabetFrequency)
    override fun name(): CodeName {
        return CodeName.SHANNON_FANO
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ShannonFanoCode<*>) return false
        if (!super.equals(other)) return false

        if (codeTree != other.codeTree) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + codeTree.hashCode()
        return result
    }
}