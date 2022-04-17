package neilyich.correction.codes.channel

import neilyich.correction.codes.core.Code
import neilyich.correction.codes.core.words.Word

class WordTransmission<C, WordC: Word<C>>(
    private val restClient: RestClient,
    var currentWord: Word<C>,
    private val code: Code<*, C, *, WordC>
) {

    fun withError(i: Int, errorValue: C, compute: (oldValue: C, error: C) -> C): WordTransmission<C, WordC> {
        currentWord = currentWord.with(i, errorValue, compute)
        return this
    }

    fun sendAndGetDecoded(): Word<C> {
        return restClient.send(currentWord, code)
    }
}