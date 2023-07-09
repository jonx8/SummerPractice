package ru.etu.visualkruskal

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage

class Main : Application() {
    override fun start(stage: Stage) {
        val fxmlLoader = FXMLLoader(Main::class.java.getResource("main-view.fxml"))

        val graph = Kruskal()
        val graphWrapper  = KruskalWrapper(graph)

        val scene = Scene(fxmlLoader.load(), 800.0, 600.0)
        val controller = fxmlLoader.getController<MainController>()
        controller.setGraphWrapper(graphWrapper)

        stage.title = "Kruskal's algorithm"
        stage.scene = scene
        stage.show()
    }
}

fun main() {
    Application.launch(Main::class.java)
}