package neilyich.correction.codes.shannonfano

import neilyich.correction.codes.util.BinaryCodeTree
import kotlin.collections.ArrayDeque

class ShannonFanoCodeTree<T: Comparable<T>>(alphabetFrequency: Map<T, Double>) : BinaryCodeTree<T>() {

    override val head: Node<T>
    private val encodeMapping: MutableMap<T, List<Boolean>> = mutableMapOf()

    init {
        val data = createFrequencyList(alphabetFrequency)
        head = createCodeTree(data)
    }

    private fun createFrequencyList(alphabetFrequency: Map<T, Double>): Pair<List<T>, List<Double>> {
        var currentSum = 0.0
        val sortedSymbols = alphabetFrequency.keys.sortedBy { it }
        val frequenciesSum = mutableListOf<Double>()
        for (t in sortedSymbols) {
            val f = alphabetFrequency[t]!!
            currentSum += f
            frequenciesSum.add(currentSum)
        }
        return sortedSymbols to frequenciesSum
    }

    private fun createCodeTree(
        data: Pair<List<T>, List<Double>>,
        splitValue: Double = calcSplitValue(data.second),
        startIndex: Int = 0,
        lastIndex: Int = data.first.size,
        currentEncoding: List<Boolean> = listOf()
    ): Node<T> {
        val symbols = data.first
        if (startIndex == lastIndex - 1) {
            encodeMapping[symbols[startIndex]] = currentEncoding
            return Node(null, null, symbols[startIndex])
        }
        if (startIndex == lastIndex - 2) {
            encodeMapping[symbols[startIndex]] = currentEncoding + false
            encodeMapping[symbols[startIndex + 1]] = currentEncoding + true
            return Node(
                left = Node(null, null, symbols[startIndex]),
                right = Node(null, null, symbols[startIndex + 1]),
                value = null
            )
        }
        val frequencyList = data.second
        val (i, cmp) = binarySearch(frequencyList, splitValue, startIndex, lastIndex)
        var splitIndex = if (cmp > 0) {
            i
        } else {
            i + 1
        }
        if (splitIndex == startIndex) {
            splitIndex++
        }
        val leftChild = if (startIndex < splitIndex) {
            createCodeTree(data, calcSplitValue(frequencyList, startIndex, splitIndex), startIndex, splitIndex, currentEncoding + false)
        } else {
            null
        }
        val rightChild = if (splitIndex < lastIndex) {
            createCodeTree(data, calcSplitValue(frequencyList, splitIndex, lastIndex), splitIndex, lastIndex, currentEncoding + true)
        } else {
            null
        }
        val value = if (leftChild == null && rightChild == null) {
            encodeMapping[symbols[splitIndex]] = currentEncoding
            symbols[splitIndex]
        } else {
            null
        }
        return Node(leftChild, rightChild, value)
    }

    private fun binarySearch(sortedList: List<Double>, value: Double, startIndex: Int, lastIndex: Int): Pair<Int, Int> {
        if (startIndex >= lastIndex) {
            throw IllegalArgumentException("startIndex must be less than lastIndex")
        }
        var r = lastIndex
        var l = startIndex
        var i = l
        var cmp: Int
        while (l < r) {
            i = (l + r) / 2
            cmp = sortedList[i].compareTo(value)
            if (cmp < 0) {
                l = i + 1
            } else if (cmp > 0) {
                r = i
            } else {
                return i to 0
            }
        }
        return i to sortedList[i].compareTo(value)
    }

    private fun calcSplitValue(frequencyList: List<Double>, startIndex: Int = 0, lastIndex: Int = frequencyList.size): Double {
        val minValue = if (startIndex == 0) {
            0.0
        } else {
            frequencyList[startIndex - 1]
        }
        return (frequencyList[lastIndex - 1] + minValue) / 2
    }

    override fun encode(symbol: T): List<Boolean> {
        return encodeMapping[symbol] ?: throw IllegalArgumentException("unknown symbol to encode: $symbol")
    }
}