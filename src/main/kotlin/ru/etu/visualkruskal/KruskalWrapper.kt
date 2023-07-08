package ru.etu.visualkruskal

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
        totalStepsNumber = drawableVertices.size
    }

    fun getGraphFromFile(inputList: List<String>): Boolean{
        if(kruskal.createGraph(inputList)) {
            drawableEdges.clear()
            drawableVertices.clear()
            setVerticesCoordinates()
            setEdgesCoordinates()
            return true
        }
        return false
    }
    fun addInputEdge(inputList: ArrayList<String>) {
        //to do
    }

    fun clearGraphDrawables() {
        drawableVertices.clear()
        drawableEdges.clear()
        // later add vertices and edges deletion
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
        for (i in kruskal.getEdges()) {
            setOneEdgeCoordinates(i)
        }
    }

    private fun setOneEdgeCoordinates(edge: Edge) { // пригодится для добавления только одного ребра
        val firstVertex = findVertexDrawing(edge.node1)
        val secondVertex = findVertexDrawing(edge.node2)

        val x1 = firstVertex!!.getCircle().centerX
        val y1 = firstVertex.getCircle().centerY

        val x2 = secondVertex!!.getCircle().centerX
        val y2 = secondVertex.getCircle().centerY

        drawableEdges.add(DrawEdge(edge, x1, y1, x2, y2))
    }

    fun stepBack() {
        if (stepNumber < 1) return
        stepNumber -= 1
        doSteps()
    }

    fun stepForward() {
        if (stepNumber > totalStepsNumber) return
        stepNumber += 1
        doSteps()
    }

    private fun doSteps() {
        state = when (stepNumber) {
            0 -> AlgorithmState.START
            totalStepsNumber -> AlgorithmState.FINISHED
            else -> AlgorithmState.IN_PROGRESS
        }
        val newEdges = kruskal.getGraphByStep(stepNumber)
        for (i in drawableEdges.indices) {
            drawableEdges[i].setEdge(newEdges[i])
            drawableEdges[i].changeAppearance()
        }
    }

    fun initialGraphState() {
        stepNumber = 0
        doSteps()
    }

    fun finalGraphState() {
        stepNumber = totalStepsNumber
        doSteps()
    }

    fun getCircles(): ArrayList<javafx.scene.shape.Circle> {
        val drawableCircles = ArrayList<javafx.scene.shape.Circle>()
        drawableVertices.forEach { drawableCircles.add(it.getCircle()) }
        return drawableCircles
    }

    fun getTexts(type: String): ArrayList<javafx.scene.text.Text> {
        val texts = ArrayList<javafx.scene.text.Text>()
        if (type == "Circles") {
            drawableVertices.forEach { texts.add(it.getText()) }
        } else {
            drawableEdges.forEach { texts.add(it.getWeightText()) }
        }
        return texts
    }

    fun getLines(): ArrayList<javafx.scene.shape.Line> {
        val drawableLines = ArrayList<javafx.scene.shape.Line>()
        drawableEdges.forEach { drawableLines.add(it.getLine()) }
        return drawableLines
    }

    fun returnAlgState(): AlgorithmState = this.state

}