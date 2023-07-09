package ru.etu.visualkruskal

class Edge(var node1: Char, var node2: Char, var weight: Int, var state: EdgeState = EdgeState.NOT_SEEN) {
    fun printEdge() {
        if (state == EdgeState.INCLUDED)
            print("$node1 <-> $node2: weight = $weight\n")
    }

    override fun toString(): String {
        return when (state) {
            EdgeState.DISCARDED -> "Edge $node1 <-> $node2 is ${state.toString().lowercase()} due to cycle."
            else -> "Edge $node1 <-> $node2 is ${state.toString().lowercase()}."
        }
    }
}