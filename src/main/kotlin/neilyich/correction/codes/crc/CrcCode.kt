package neilyich.correction.codes.crc

import neilyich.correction.codes.core.FieldCode
import neilyich.correction.codes.core.exceptions.DecodingException
import neilyich.correction.codes.core.words.FieldWord
import neilyich.correction.codes.serialization.CodeName
import neilyich.field.element.PrimeFieldElement
import neilyich.field.polynomial.AFieldPolynomial
import neilyich.field.polynomial.FieldPolynomial

fun AFieldPolynomial<PrimeFieldElement>.toStringVector(): String {
    return coefsList().joinToString(separator = "")
}

class CrcCode(
    val mod: AFieldPolynomial<PrimeFieldElement>
): FieldCode<PrimeFieldElement, PrimeFieldElement> {
    init {
        if (mod.field.size() != 2) {
            throw IllegalArgumentException("CRC code must operate only with binary polynomials")
        }
    }

    override fun encode(info: FieldWord<PrimeFieldElement>): FieldWord<PrimeFieldElement> {
        val pol = FieldPolynomial(info.field(), info.content(), mod.literal)
        val polShift = pol.shift(mod.degree())
        val r = polShift % mod
        return FieldWord(mod.field, (polShift + r).coefsList())
    }

    override fun decode(word: FieldWord<PrimeFieldElement>): FieldWord<PrimeFieldElement> {
        val pol = FieldPolynomial(mod.field, word.content(), mod.literal)
        val r = pol % mod
        if (r.isZero()) {
            val coefsList = pol.coefsList()
            return FieldWord(mod.field, coefsList.subList(mod.degree(), coefsList.size))
        }
        throw DecodingException(word, "unable to decode $word (message was corrupted)")
    }

    override fun infoWordLength(): Int {
        throw UnsupportedOperationException("info word length can be any number")
    }

    override fun encodedWordLength(): Int {
        throw UnsupportedOperationException("encoded word length can be any number")
    }

    override fun name(): CodeName {
        return CodeName.CRC
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CrcCode) return false

        if (mod != other.mod) return false

        return true
    }

    override fun hashCode(): Int {
        return mod.hashCode()
    }


}