package neilyich.correction.codes.serialization

import neilyich.field.Field
import neilyich.field.element.FieldElement
import neilyich.field.element.PrimeFieldElement
import neilyich.field.polynomial.AFieldPolynomial

data class CodeDescription(
    val type: CodeName,
    val alphabetFrequency: Map<String, Double>?, // huffman and shannon fano
    val mod: AFieldPolynomial<PrimeFieldElement>?, // crc
    val field: Field<out FieldElement>?, // hamming
    val m: Int? // hamming
)
