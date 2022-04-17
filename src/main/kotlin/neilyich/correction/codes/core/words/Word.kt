package neilyich.correction.codes.core.words

interface Word<C>: Iterable<C> {
    fun length(): Int
    fun content(): List<C>
    operator fun get(i: Int): C
    fun with(i: Int, errorValue: C): Word<C>
    fun with(i: Int, errorValue: C, compute: (oldValue: C, error: C) -> C): Word<C>
    override fun iterator(): Iterator<C> {
        return content().iterator()
    }
}
