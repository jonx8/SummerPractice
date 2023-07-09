package ru.etu.visualkruskal

import javafx.scene.shape.Line
import kotlin.math.cos
import kotlin.math.sin

const val graphCenterX = 290.0
const val graphCenterY = 265.0
const val drawVerticesRadius = 231.0

class KruskalWrapper(private val kruskal: Kruskal, private var state: AlgorithmState = AlgorithmState.START) {
    private var stepNumber = 0
    private val drawableVertices = ArrayList<DrawVertex>()
    private val drawableEdges = ArrayList<DrawEdge>()
    private var totalStepsNumber = 0

    init {
        setVerticesCoordinates()
        setEdgesCoordinates()
    }

    fun getGraphFromFile(inputList: List<String>): Boolean {
        if (kruskal.createGraph(inputList)) {
            drawableEdges.clear()
            drawableVertices.clear()
            setVerticesCoordinates()
            setEdgesCoordinates()
            return true
        }
        return false
    }

    private fun findVertexDrawing(symbol: Char): DrawVertex? {
        for (j in drawableVertices.indices) {
            if (drawableVertices[j].getName() == symbol) {
                return drawableVertices[j]
            }
        }
        return null
    }

    private fun setVerticesCoordinates() {
        val nodes = kruskal.getNodes()
        val angle = 360.0 / nodes.size

        for (i in 0 until nodes.size) {
            val x = graphCenterX + drawVerticesRadius * cos(Math.toRadians(angle * i))
            val y = graphCenterY + drawVerticesRadius * sin(Math.toRadians(angle * i))
            drawableVertices.add(DrawVertex(nodes.keys.elementAt(i), x, y))
        }
    }

    private fun setEdgesCoordinates() {
        val sortedEdges = kruskal.getEdges()
        sortedEdges.sortBy { it.weight }
        for (i in sortedEdges.indices) {
            drawableEdges.add(DrawEdge(sortedEdges[i]))
            setOneEdgeCoordinates(drawableEdges[i])
        }
    }

    private fun setOneEdgeCoordinates(drawEdge: DrawEdge) {
        val firstVertex = findVertexDrawing(drawEdge.getEdge().node1)
        val secondVertex = findVertexDrawing(drawEdge.getEdge().node2)

        val x1 = firstVertex!!.getCircle().centerX
        val y1 = firstVertex.getCircle().centerY

        val x2 = secondVertex!!.getCircle().centerX
        val y2 = secondVertex.getCircle().centerY

        changeEdgeStart(drawEdge.getLine(), x1, y1)
        changeEdgeEnd(drawEdge.getLine(), x2, y2)
        changeWeightText(drawEdge)
    }

    private fun changeEdgeStart(line: Line, x: Double, y: Double) {
        line.startX = x
        line.startY = y
    }

    private fun changeEdgeEnd(line: Line, x: Double, y: Double) {
        line.endX = x
        line.endY = y
    }

    private fun changeWeightText(edge: DrawEdge) {
        edge.getWeightText().x =
            weightAlignment + edge.getLine().startX + ((edge.getLine().endX - edge.getLine().startX) / 2)
        edge.getWeightText().y =
            -weightAlignment + edge.getLine().startY + ((edge.getLine().endY - edge.getLine().startY) / 2)
    }

    fun addInputEdge(inputList: ArrayList<String>) {

    }

    fun clearGraphDrawables() {

    }

    private fun doSteps() {
        state = when (stepNumber) {
            0 -> AlgorithmState.START
            totalStepsNumber -> AlgorithmState.FINISHED
            else -> AlgorithmState.IN_PROGRESS
        }
        if (kruskal.getEdges().size > 0) {
            val newEdges = kruskal.getGraphByStep(stepNumber)
            for (i in drawableEdges.indices) {
                drawableEdges[i].setEdge(newEdges[i])
                drawableEdges[i].changeAppearance()
            }
        }
    }

    fun stepBack(): String {
        if (stepNumber < 1) return "Wrong step number"
        stepNumber -= 1
        doSteps()
        return if (stepNumber > 0) {
            drawableEdges[stepNumber - 1].getEdge().toString()
        } else {
            "Zero step. Graph can be redacted"
        }

    }

    fun stepForward(): String {
        if (stepNumber > totalStepsNumber) return "Wrong step number"
        stepNumber += 1
        doSteps()
        return if (stepNumber < totalStepsNumber) {
            drawableEdges[stepNumber - 1].getEdge().toString()
        } else {
            "The last step. ${drawableEdges[stepNumber - 1].getEdge()}\n" +
                    "MST weight is ${kruskal.getWeight()}."
        }
    }

    fun initialGraphState(): String {
        stepNumber = 0
        doSteps()
        return "Zero step. Graph can be redacted"
    }

    fun finalGraphState(): String {
        stepNumber = totalStepsNumber
        doSteps()
        return "The last step. ${drawableEdges[stepNumber - 1].getEdge()}\n" +
                "MST weight is ${kruskal.getWeight()}."
    }

    fun changeEdgesAfterDrag(drawVertex: DrawVertex) {
        for (i in drawableEdges) {
            if (drawVertex.getName() == i.getEdge().node1) {
                changeEdgeStart(i.getLine(), drawVertex.getCircle().centerX, drawVertex.getCircle().centerY)
                changeWeightText(i)
            }
            if (drawVertex.getName() == i.getEdge().node2) {
                changeEdgeEnd(i.getLine(), drawVertex.getCircle().centerX, drawVertex.getCircle().centerY)
                changeWeightText(i)
            }
        }
    }

    fun findTree() {
        drawableEdges.clear()
        setEdgesCoordinates()
        kruskal.startAgain()
        kruskal.doAlgorithm()

        val tempEdges = kruskal.getEdges()
        for (i in tempEdges.indices) {
            if ((i == tempEdges.size - 1)) {
                totalStepsNumber = i + 1
                break
            }
            if ((tempEdges[i].state == EdgeState.INCLUDED) and (tempEdges[i + 1].state == EdgeState.NOT_SEEN)) {
                totalStepsNumber = i + 1
                break
            }
        }

    }

    fun getAlgState(): AlgorithmState = this.state
    fun getDrawVertices(): ArrayList<DrawVertex> = drawableVertices
    fun getDrawEdges(): ArrayList<DrawEdge> = drawableEdges
    fun getConnectivity(): Boolean = kruskal.isGraphConnected()

}