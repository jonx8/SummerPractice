package ru.etu.visualkruskal

import javafx.scene.text.Text
import javafx.scene.shape.Line
import javafx.scene.text.Font

const val edgeBorderWidth = 2.0
const val weightAlignment = 3.0


const val includedColour = "#a61c1c"
const val discardedDashedStyle = " -fx-stroke-dash-array: 5;"
const val discardedColour = "#000000"
const val unseenColour = "#000000"


class DrawEdge(private var edge: Edge, x1: Double, y1: Double, x2: Double, y2: Double) {
    private val line = Line()
    private val weightText = Text()

    init {
        line.strokeWidth = edgeBorderWidth
        line.startX = x1
        line.startY = y1
        line.endX = x2
        line.endY = y2

        weightText.text = edge.weight.toString()
        weightText.font = Font(13.0)
        weightText.y = y1 + ((y2 - y1) / 2) - weightAlignment
        weightText.x = x1 + ((x2 - x1) / 2) + weightAlignment
    }

    fun getEdge(): Edge = edge
    fun setEdge(edge:Edge){this.edge = edge}
    fun getWeightText(): Text = weightText
    fun getLine(): Line = line
    fun changeAppearance() {
        line.style = null
        when (edge.state) {
            EdgeState.NOT_SEEN -> line.stroke = javafx.scene.paint.Color.web(unseenColour)
            EdgeState.DISCARDED -> {
                line.style = discardedDashedStyle
                line.stroke = javafx.scene.paint.Color.web(discardedColour)
            }

            EdgeState.INCLUDED -> line.stroke = javafx.scene.paint.Color.web(includedColour)
        }
    }
}