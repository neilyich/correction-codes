package neilyich.correction.codes.core.exceptions

import neilyich.correction.codes.core.words.Word

class EncodingException(
    private val word: Word<*>,
    override val message: String? = null,
    override val cause: Throwable? = null
): RuntimeException(message) {
    fun failedInfoWord(): Word<*> = word
}