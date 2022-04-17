package neilyich.correction.codes.serialization

import neilyich.field.Field
import neilyich.field.element.FieldElement

data class WordDescription<T>(
    val field: Field<out FieldElement>?,
    val content: List<T>
)
