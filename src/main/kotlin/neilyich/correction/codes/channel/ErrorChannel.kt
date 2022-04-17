package neilyich.correction.codes.channel

import neilyich.correction.codes.core.Code
import neilyich.correction.codes.core.words.Word

class ErrorChannel<C, WordC: Word<C>>(
    private val code: Code<*, C, *, WordC>,
    private val restClient: RestClient
) {
    fun take(word: Word<C>): WordTransmission<C, WordC> {
        return WordTransmission(restClient, word, code)
    }
}