package neilyich.correction.codes.serialization

import com.fasterxml.jackson.databind.module.SimpleModule
import neilyich.correction.codes.core.Code
import neilyich.correction.codes.core.words.Word

class CodesModule: SimpleModule() {
    init {
        addSerializer(WordSerializer())
        addSerializer(CodeSerializer())
        addDeserializer(Word::class.java, WordDeserializer())
        addDeserializer(Code::class.java, CodeDeserializer())
    }
}