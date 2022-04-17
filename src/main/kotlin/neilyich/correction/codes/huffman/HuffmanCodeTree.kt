package neilyich.correction.codes.huffman

import neilyich.correction.codes.util.BinaryCodeTree
import java.util.*

class HuffmanCodeTree<T: Comparable<T>>(alphabetFrequency: Map<T, Double>): BinaryCodeTree<T>() {
    private val encodeMapping: MutableMap<T, LinkedList<Boolean>> = alphabetFrequency.mapValues { LinkedList<Boolean>() }.toMutableMap()
    override val head: Node<T> = createCodeTree(alphabetFrequency)

    override fun encode(symbol: T): List<Boolean> {
        return encodeMapping[symbol] ?: throw IllegalArgumentException("unknown symbol to encode: $symbol")
    }

    private fun createCodeTree(alphabetFrequency: Map<T, Double>): Node<T> {
        val heap = createFrequencyHeap(alphabetFrequency)
        while (heap.size > 1) {
            val firstMin = heap.remove()
            for (s in firstMin.third) {
                encodeMapping[s]!!.add(0, false)
            }
            val secondMin = heap.remove()
            for (s in secondMin.third) {
                encodeMapping[s]!!.add(0, true)
            }
            val newNode = Node(firstMin.first, secondMin.first, null)
            heap.add(Triple(newNode, firstMin.second + secondMin.second, firstMin.third + secondMin.third))
        }
        return heap.remove().first
    }

    private fun createFrequencyHeap(alphabetFrequency: Map<T, Double>): PriorityQueue<Triple<Node<T>, Double, List<T>>> {
        val heap = PriorityQueue<Triple<Node<T>, Double, List<T>>>(compareBy { it.second })
        heap.addAll(alphabetFrequency.map { Triple(Node(null, null, it.key), it.value, listOf(it.key)) }.toList())
        return heap
    }
}