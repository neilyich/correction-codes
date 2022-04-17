package neilyich.correction.codes.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import neilyich.correction.codes.core.words.FieldWord
import neilyich.correction.codes.core.words.SimpleWord
import neilyich.correction.codes.core.words.Word
import neilyich.field.element.FieldElement

class WordSerializer: StdSerializer<Word<*>>(Word::class.java) {
    override fun serialize(word: Word<*>?, jsonGenerator: JsonGenerator?, serializerProvider: SerializerProvider?) {
        when (word) {
            is FieldWord<*> -> {
                serializerProvider?.defaultSerializeValue(WordDescription(word.field(), word.content().map { it.toString() }), jsonGenerator)
            }
            is SimpleWord<*> -> {
                serializerProvider?.defaultSerializeValue(WordDescription(null, word.content()), jsonGenerator)
            }
            else -> {
                serializerProvider?.defaultSerializeValue(word, jsonGenerator)
            }
        }
    }
}