package ru.etu.visualkruskal

import javafx.scene.paint.Color
import javafx.scene.shape.Line
import javafx.scene.shape.Rectangle
import javafx.scene.text.Font
import javafx.scene.text.Text

const val backgroundSize = 24.0

const val edgeBorderWidth = 3.0
const val weightAlignment = 3.0
const val backgroundAlignmentX = 5.0
const val backgroundAlignmentY = 22.5

const val includedColour = "#42AAFF"
const val discardedDashedStyle = " -fx-stroke-dash-array: 5;"
const val discardedColour = "#000000"
const val unseenColour = "#000000"
const val weightColour = "#000000"
const val backgroundColour = "#F0E68C"


class DrawEdge(private var edge: Edge) {
    private val line = Line()
    private val weightText = Text()
    private val background = Rectangle()

    init {
        line.strokeWidth = edgeBorderWidth
        line.stroke = Color.web(unseenColour)
        weightText.text = edge.weight.toString()
        weightText.font = Font(20.0)
        weightText.fill = Color.web(weightColour)
        background.fill = Color.web(backgroundColour)
        background.height = backgroundSize
        background.width = backgroundSize
    }

    fun getEdge(): Edge = edge
    fun setEdge(edge: Edge) {
        this.edge = edge
    }

    fun getWeightText(): Text = weightText
    fun getLine(): Line = line
    fun changeAppearance() {
        line.style = null
        when (edge.state) {
            EdgeState.NOT_SEEN -> line.stroke = Color.web(unseenColour)
            EdgeState.DISCARDED -> {
                line.style = discardedDashedStyle
                line.stroke = Color.web(discardedColour)
            }

            EdgeState.INCLUDED -> line.stroke = Color.web(includedColour)
        }
    }

    fun getRectangular(): Rectangle {
        return background
    }
}