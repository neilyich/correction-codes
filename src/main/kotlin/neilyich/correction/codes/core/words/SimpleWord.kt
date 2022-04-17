package neilyich.correction.codes.core.words

class SimpleWord<C>(private val content: List<C>): Word<C> {
    override fun length(): Int {
        return content.size
    }

    override fun content(): List<C> {
        return content
    }

    override fun get(i: Int): C {
        return content[i]
    }

    override fun with(i: Int, errorValue: C): Word<C> {
        return with(i, errorValue) { _, error -> error }
    }

    override fun with(i: Int, errorValue: C, compute: (oldValue: C, error: C) -> C): Word<C> {
        val newContent = content.toMutableList()
        newContent[i] = compute(content[i], errorValue)
        return SimpleWord(newContent)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SimpleWord<*>) return false

        if (content != other.content) return false

        return true
    }

    override fun hashCode(): Int {
        return content.hashCode()
    }

    override fun toString(): String = content().toString()
}

fun <C> wordOf(vararg symbols: C): Word<C> {
    return SimpleWord(symbols.toList())
}

fun wordOf(str: String): Word<String> {
    return SimpleWord(str.toList().map { it.toString() })
}

fun Iterable<Boolean>.toBinaryString(): String {
    return joinToString("") { if (it) "1" else "0" }
}
