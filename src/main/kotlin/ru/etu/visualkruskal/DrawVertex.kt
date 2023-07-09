package ru.etu.visualkruskal

import javafx.scene.shape.Circle
import javafx.scene.text.Font
import javafx.scene.text.Text

const val circleRadius = 23.0
const val borderColour = "#000000"
const val vertexBorderWidth = 2.0
const val circleFillColour = "#fff9f2"
const val nameAlignment = 4.0

class DrawVertex(private val symbol: Char, x: Double, y: Double) {
    private val circle = Circle()
    private val text = javafx.scene.text.Text()

    init {
        text.text = symbol.toString()
        text.font = Font.font(13.0)
        text.x = x - nameAlignment
        text.y = y + nameAlignment

        circle.centerX = x
        circle.centerY = y
        circle.radius = circleRadius
        circle.fill = javafx.scene.paint.Color.web(circleFillColour)
        circle.stroke = javafx.scene.paint.Color.web(borderColour)
        circle.strokeWidth = vertexBorderWidth

    }

    fun getName():Char = symbol
    fun getCircle():Circle = circle
    fun getText():Text = text

}