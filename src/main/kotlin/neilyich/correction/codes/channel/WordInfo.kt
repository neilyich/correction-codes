package neilyich.correction.codes.channel

import neilyich.correction.codes.core.Code
import neilyich.correction.codes.core.words.Word

data class WordInfo<C, WordC: Word<C>>(
    val word: Word<C>,
    val code: Code<*, C, *, WordC>
)
