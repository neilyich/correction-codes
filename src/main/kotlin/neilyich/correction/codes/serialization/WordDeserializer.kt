package neilyich.correction.codes.serialization

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import neilyich.correction.codes.core.words.FieldWord
import neilyich.correction.codes.core.words.SimpleWord
import neilyich.correction.codes.core.words.Word
import neilyich.field.Field
import neilyich.field.element.FieldElement

class WordDeserializer: StdDeserializer<Word<*>>(Word::class.java) {
    override fun deserialize(jsonParser: JsonParser?, deserializationContext: DeserializationContext?): Word<*> {
        val wordDescription = deserializationContext?.readValue(jsonParser, WordDescription::class.java) ?: throw NullPointerException()
        if (wordDescription.field != null) {
            wordDescription.content as List<String>
            return FieldWord(wordDescription.field as Field<FieldElement>, wordDescription.content.map { wordDescription.field.fromString(it) }.toMutableList())
        }
        return SimpleWord(wordDescription.content)
    }
}