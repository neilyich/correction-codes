package neilyich.correction.codes.channel

import neilyich.correction.codes.core.Code
import neilyich.correction.codes.core.words.Word

interface Client {
    fun <C, WordC: Word<C>> send(word: Word<C>, code: Code<*, C, *, WordC>, url: String? = null): Word<C>
}