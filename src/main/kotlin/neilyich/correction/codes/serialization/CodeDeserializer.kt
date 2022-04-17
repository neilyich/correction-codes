package neilyich.correction.codes.serialization

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import neilyich.correction.codes.core.Code
import neilyich.correction.codes.crc.CrcCode
import neilyich.correction.codes.hamming.HammingCode
import neilyich.correction.codes.huffman.HuffmanCode
import neilyich.correction.codes.shannonfano.ShannonFanoCode

class CodeDeserializer: StdDeserializer<Code<*, *, *, *>>(Code::class.java) {
    override fun deserialize(jsonParser: JsonParser?, deserializationContext: DeserializationContext?): Code<*, *, *, *> {
        val codeDescription = deserializationContext?.readValue(jsonParser, CodeDescription::class.java) ?: throw NullPointerException()
        return when (codeDescription.type) {
            CodeName.CRC -> {
                CrcCode(codeDescription.mod!!)
            }
            CodeName.HAMMING -> {
                HammingCode(codeDescription.field!!, codeDescription.m!!)
            }
            CodeName.HUFFMAN -> {
                HuffmanCode(codeDescription.alphabetFrequency!!)
            }
            CodeName.SHANNON_FANO -> {
                ShannonFanoCode(codeDescription.alphabetFrequency!!)
            }
        }
    }
}