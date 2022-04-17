package neilyich.correction.codes.util

import neilyich.correction.codes.core.exceptions.DecodingException

abstract class BinaryCodeTree<T> {
    data class Node<T>(
        val left: Node<T>?,
        val right: Node<T>?,
        val value: T?
    )

    protected abstract val head: Node<T>

    private fun decodeNext(iterator: Iterator<Boolean>): T {
        var currentNode = head
        while (currentNode.left != null || currentNode.right != null) {
            if (!iterator.hasNext()) {
                throw RuntimeException("unable to get next symbol of sequence")
            }
            currentNode = if (iterator.next()) {
                currentNode.right!!
            } else {
                currentNode.left!!
            }
        }
        return currentNode.value!!
    }

    fun decode(sequence: Iterable<Boolean>): List<T> {
        val result = mutableListOf<T>()
        val iterator = sequence.iterator()
        while (iterator.hasNext()) {
            result.add(decodeNext(iterator))
        }
        return result
    }

    fun print(headNode: Node<T> = head) {
        val emptyNode = Node<T>(null, null, null)
        val stack = ArrayDeque<Pair<Node<T>, Int>>()
        stack.addLast(headNode to 0)
        var prevLevel = 0
        var levelHasNotEmptyNode = false
        while (stack.isNotEmpty()) {
            val pair = stack.removeFirst()
            if (prevLevel < pair.second) {
                println()
                if (!levelHasNotEmptyNode) {
                    return
                }
                levelHasNotEmptyNode = false
            }
            prevLevel = pair.second
            val node = pair.first
            if (node === emptyNode) {
                print("-")
            } else {
                levelHasNotEmptyNode = true
                print(node.value ?: "*")
            }
            print(" ")
            stack.addLast((pair.first.left ?: emptyNode) to pair.second + 1)
            stack.addLast((pair.first.right ?: emptyNode) to pair.second + 1)
        }
    }

    abstract fun encode(symbol: T): List<Boolean>

    override fun hashCode(): Int {
        return head.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BinaryCodeTree<*>) return false

        if (head != other.head) return false

        return true
    }
}