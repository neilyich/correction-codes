package neilyich.correction.codes.core.words

interface Word<C> {
    fun length(): Int
    fun content(): List<C>
    operator fun get(i: Int): C
}