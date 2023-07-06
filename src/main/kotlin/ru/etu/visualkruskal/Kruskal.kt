package ru.etu.visualkruskal

import kotlin.collections.ArrayList

class Kruskal {
    private var edges = ArrayList<Edge>()
    private var components = ArrayList<String>()
    private var resEdges = ArrayList<Edge>()
    private var nodes = HashMap<Char, ArrayList<Char>>()

    fun addEdge(node1: Char, node2: Char, weight: Int): String{
        if(node1 in 'a'..'z' && node2 in 'a'..'z') {
            val connectedNodes1 = nodes[node1]
            val connectedNodes2 = nodes[node2]
            if(connectedNodes1 != null && connectedNodes2 != null){
                if(node1 in connectedNodes2)
                    return "Such an edge already exists"
                else{
                    connectedNodes1.add(node2)
                    connectedNodes2.add(node1)
                }
            }
            if(connectedNodes1 == null && connectedNodes2 != null){
                connectedNodes2.add(node1)
                nodes[node1] = ArrayList()
                nodes[node1]!!.add(node2)
            }
            if(connectedNodes1 != null && connectedNodes2 == null){
                connectedNodes1.add(node2)
                nodes[node2] = ArrayList()
                nodes[node2]!!.add(node1)
            }
            if(connectedNodes1 == null && connectedNodes2 == null){
                nodes[node1] = ArrayList()
                nodes[node2] = ArrayList()
                nodes[node1]!!.add(node2)
                nodes[node2]!!.add(node1)
            }

            if (node1 < node2)
                edges.add(Edge(node1, node2, weight))
            else
                edges.add(Edge(node2, node1, weight))
            return "Edge added"
        }
        return "Node name must be a latin letter"
    }

    fun delEdge(node1: Char, node2: Char): String{
        val tempNode1: Char
        val tempNode2: Char
        if(node1 > node2){
            tempNode1 = node2
            tempNode2 = node1
        }
        else{
            tempNode1 = node1
            tempNode2 = node2
        }
        for(edge in edges){
            if(tempNode1 == edge.node1 && tempNode2 == edge.node2){
                edges.remove(edge)
                if(nodes[tempNode1]!!.size != 1 && nodes[tempNode2]!!.size != 1) {
                    nodes[tempNode1]!!.remove(tempNode2)
                    nodes[tempNode2]!!.remove(tempNode1)
                }
                else if(nodes[tempNode1]!!.size == 1){
                    nodes[tempNode2]!!.remove(tempNode1)
                    nodes.remove(tempNode1)
                }
                else{
                    nodes[tempNode1]!!.remove(tempNode2)
                    nodes.remove(tempNode2)
                }
                return "Edge removed"
            }
        }
        return "There is no such edge in the graph"
    }

    fun addNode(nodeName: Char): String{
        if(nodeName in 'a'..'z') {
            val connectedNodes = nodes[nodeName]
            if(connectedNodes == null) {
                nodes[nodeName] = ArrayList<Char>()
            }
            else return "Such a node already exists"
            return "Node added"
        }
        return "Node name must be a latin letter"
    }

    fun delNode(nodeName: Char): String{
        val connectedNodes = nodes[nodeName]
        if(connectedNodes != null) {
            for(node in connectedNodes) {
                nodes[node]!!.remove(nodeName)
            }

            val connectedEdges = ArrayList<Edge>()
            for (edge in edges) {
                if (edge.node1 == nodeName || edge.node2 == nodeName) {
                    connectedEdges.add(edge)
                }
            }

            for (edge in connectedEdges) {
                edges.remove(edge)
            }

            nodes.remove(nodeName)
            return "Node deleted"
        }
        return "There is no such a node"
    }

    fun createGraph(listOfEdges: List<String>){
        for(edge in listOfEdges){
            val node1: Char = edge.split(" ")[0].first()
            val node2: Char = edge.split(" ")[1].first()
            val weight: Int = edge.split(" ")[2].toInt()
            addEdge(node1, node2, weight)
        }
    }

    fun getGraphByStep(step: Int): ArrayList<Edge>{
        val newEdges = ArrayList<Edge>()
        for(i in 0 until step){
            newEdges.add(edges[i])
        }
        for(i in step until edges.size){
            newEdges.add(Edge(edges[i].node1, edges[i].node2, edges[i].weight, EdgeState.NOT_SEEN))
        }
        return newEdges
    }

    fun doAlgorithm(): ArrayList<Edge>{
        if(!isGraphConnected()) return edges
        edges.sortBy { it -> it.weight }

        for(key in 'a'..'z'){
            val value = nodes[key]
            if(value != null){
                components.add(key.toString())
            }
        }

        var comp1: String
        var comp2: String
        for(i in 0..edges.size){
            comp1 = getComp(edges[i].node1)
            comp2 = getComp(edges[i].node2)
           if(comp1 != comp2 || comp1.isEmpty()) {
                edges[i].state = EdgeState.INCLUDED
                resEdges.add(edges[i])
                uniteComps(comp1, comp2)
                if(resEdges.size == nodes.size - 1)
                    break
           }
            else{
                edges[i].state = EdgeState.DISCARDED
           }
        }
        return edges
    }

    fun getNodes(): HashMap<Char, ArrayList<Char>>{
        return nodes
    }

    fun getEdges(): ArrayList<Edge>{
        return edges
    }

    private fun getComp(node: Char): String{
        for(comp in components) {
            if (node in comp)
                return comp
        }
        return ""
    }

    private fun uniteComps(comp1: String, comp2: String){
        components.remove(comp2)
        components.remove(comp1)
        components.add(comp1+comp2)
    }

    fun clear_graph(){
        edges.clear()
        nodes.clear()
        components.clear()
        resEdges.clear()
    }

    fun isGraphConnected(): Boolean{
        if(nodes.size == 0) return false
        val visitedNodes = ArrayList<Char>()
        var firstNode: Char = 'a'
        for(key in 'a'..'z'){
            val value = nodes[key]
            if(value != null){
                firstNode = key
                break
            }
        }

        if(bfc(firstNode, visitedNodes) == nodes.size) return true
        return false
    }

    private fun bfc(node: Char, visitedNodes: ArrayList<Char>): Int{
        var countOfVisited = 1
        visitedNodes.add(node)
        val connectedNodes = nodes[node]!!
        for(tempNode in connectedNodes){
            if(tempNode !in visitedNodes)
                countOfVisited += bfc(tempNode, visitedNodes)
        }
        return countOfVisited
    }

    fun startAgain(){
        components.clear()
        resEdges.clear()
        for(edge in edges){
            edge.state = EdgeState.NOT_SEEN
        }
    }

    fun getWeight(): Int{
        var weight = 0
        for(edge in edges){
            if(edge.state == EdgeState.INCLUDED)
                weight += edge.weight
        }
        return weight
    }
}
