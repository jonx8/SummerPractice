package ru.etu.visualkruskal

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage

class MainWindow: Application() {
    override fun start(stage: Stage) {
        val fxmlLoader = FXMLLoader(Main::class.java.getResource("main-view.fxml"))

        //drawing test, should be deleted later
        val testGraph = Kruskal()
        println(testGraph.addNode('a'))
        println(testGraph.addNode('b'))
        println(testGraph.addNode('c'))
        println(testGraph.addNode('d'))
        println(testGraph.addNode('e'))

        println(testGraph.addEdge('a','e',1))
        println(testGraph.addEdge('d','c',2))
        println(testGraph.addEdge('a','b',3))
        println(testGraph.addEdge('b','e',4))
        println(testGraph.addEdge('b','c',5))
        println(testGraph.addEdge('c','e',6))
        println(testGraph.addEdge('d','e',7))

        testGraph.doAlgorithm()

        val testGraphWrapper  = KruskalWrapper(testGraph)
        val scene = Scene(fxmlLoader.load(), 800.0, 600.0)
        val controller = fxmlLoader.getController<MainController>()
        controller.setGraphWrapper(testGraphWrapper)

        stage.title = "Kruskal's algorithm"
        stage.scene = scene
        stage.show()
    }
}

fun main() {
    Application.launch(MainWindow::class.java)
}
