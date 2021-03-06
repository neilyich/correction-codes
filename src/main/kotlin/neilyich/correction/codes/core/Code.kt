package neilyich.correction.codes.core

import neilyich.correction.codes.core.exceptions.DecodingException
import neilyich.correction.codes.core.exceptions.EncodingException
import neilyich.correction.codes.core.words.Word
import neilyich.correction.codes.serialization.CodeName

interface Code<IC, EC, Info: Word<IC>, EncodedWord: Word<EC>> {
    @Throws(EncodingException::class)
    fun encode(info: Info): EncodedWord

    @Throws(DecodingException::class)
    fun decode(word: EncodedWord): Info

    fun infoWordLength(): Int
    fun encodedWordLength(): Int

    fun name(): CodeName
}