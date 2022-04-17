package neilyich.correction.codes.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import neilyich.correction.codes.core.Code
import neilyich.correction.codes.crc.CrcCode
import neilyich.correction.codes.hamming.HammingCode
import neilyich.correction.codes.treecode.TreeCode

class CodeSerializer: StdSerializer<Code<*, *, *, *>>(Code::class.java) {
    override fun serialize(code: Code<*, *, *, *>?, jsonGenerator: JsonGenerator?, serializerProvider: SerializerProvider?) {
        when (code) {
            is TreeCode<*> -> {
                serializerProvider?.defaultSerializeValue(CodeDescription(code.name(), code.alphabetFrequency.mapKeys { it.key.toString() }, null, null, null), jsonGenerator)
            }
            is HammingCode<*> -> {
                serializerProvider?.defaultSerializeValue(CodeDescription(code.name(), null, null, code.field, code.m), jsonGenerator)
            }
            is CrcCode -> {
                serializerProvider?.defaultSerializeValue(CodeDescription(code.name(), null, code.mod, null, null), jsonGenerator)
            }
        }
    }
}