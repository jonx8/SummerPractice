package ru.etu.visualkruskal

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage

class MainWindow: Application() {
    override fun start(stage: Stage) {
        val fxmlLoader = FXMLLoader(MainWindow::class.java.getResource("hello-view.fxml"))
        val scene = Scene(fxmlLoader.load(), 320.0, 240.0)
        stage.title = "Hello!"
        stage.scene = scene
        stage.show()
    }
}

fun main() {
    Application.launch(MainWindow::class.java)
}