package ru.etu.visualkruskal

import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.input.KeyCode
import javafx.scene.layout.GridPane
import javafx.scene.layout.Pane
import javafx.scene.text.Text
import javafx.stage.FileChooser
import java.io.File

const val dragBorder = 40

class MainController {

    @FXML
    private lateinit var graphPane: Pane

    @FXML
    private lateinit var commentText: Text

    @FXML
    private lateinit var toolsMenu: MenuButton

    @FXML
    private lateinit var startButton: Button

    @FXML
    private lateinit var nextStepButton: Button

    @FXML
    private lateinit var prevStepButton: Button

    @FXML
    private lateinit var finishButton: Button

    @FXML
    private lateinit var fileButton: Button

    private lateinit var graphWrapper: KruskalWrapper

    private var isRedacted = true

    @FXML
    private fun onFileButtonClick() {
        val fileChooser = FileChooser()
        fileChooser.extensionFilters.add(FileChooser.ExtensionFilter("TEXT files (*.txt)", "*.txt"))
        fileChooser.title = "Choose a .txt file for graph reading"
        fileChooser.initialDirectory = File(System.getProperty("user.home"))
        val result = fileChooser.showOpenDialog(graphPane.scene.window)
        graphPane.children.clear()
        if (result != null) {
            if (result.length() != 0L && result.canRead()) {
                if (graphWrapper.getGraphFromFile(result.readLines())) commentText.text = "Graph read from file"
                else commentText.text = "Incorrect data in file"
            } else commentText.text = "File must not be empty"
        }
        drawGraph()
    }


    @FXML
    private fun onClickAddEdgeDialog() {
        val edgeDialog: Dialog<ArrayList<String>> = Dialog()
        edgeDialog.headerText = "Enter edge. Example: a b 10"
        val dialogGrid = GridPane()
        dialogGrid.hgap = 10.0
        dialogGrid.vgap = 10.0

        val inputButtonType = ButtonType("OK", ButtonBar.ButtonData.OK_DONE)
        edgeDialog.dialogPane.buttonTypes.addAll(inputButtonType, ButtonType.CANCEL)

        val firstEdgeField = TextField()
        val secondEdgeField = TextField()
        val edgeWeightField = TextField()

        firstEdgeField.setMaxSize(30.0, 10.0)
        secondEdgeField.setMaxSize(30.0, 10.0)
        edgeWeightField.setMaxSize(70.0, 10.0)

        dialogGrid.add(Label("First"), 0, 0)
        dialogGrid.add(firstEdgeField, 1, 0)
        dialogGrid.add(Label("Second"), 2, 0)
        dialogGrid.add(secondEdgeField, 3, 0)
        dialogGrid.add(Label("Weight"), 4, 0)
        dialogGrid.add(edgeWeightField, 5, 0)

        edgeDialog.dialogPane.content = dialogGrid

        edgeDialog.setResultConverter { dialogButton ->
            if (dialogButton === inputButtonType) {
                return@setResultConverter arrayListOf<String>(
                    firstEdgeField.text, secondEdgeField.text, edgeWeightField.text
                )
            }
            null
        }
        val result = edgeDialog.showAndWait()
        if (result.isPresent) {
            val arr = result.get()
            commentText.text = graphWrapper.addInputEdge(arr)
            if (commentText.text == "Edge added") {
                graphPane.children.clear()
                commentText.text = "Edge ${arr[0]} <-> ${arr[1]} added"
                drawGraph()
            }
        }
    }

    @FXML
    private fun onStartButtonClick() {
        graphPane.children.clear()
        drawGraph()
        commentText.text = graphWrapper.initialGraphState()
        changeButtonsState()
        isRedacted = true
    }

    @FXML
    private fun onStepPrevButtonClick() {
        commentText.text = graphWrapper.stepBack()
        changeButtonsState()
        if (graphWrapper.getAlgState() == AlgorithmState.START) isRedacted = true
    }

    @FXML
    private fun onStepNextButtonClick() {
        if (isRedacted) {
            if (graphWrapper.getConnectivity() && !graphWrapper.oneVertexCheck()) {
                graphPane.children.clear()
                graphWrapper.findTree()
                commentText.text = graphWrapper.stepForward()
                changeButtonsState()
                isRedacted = false
                drawGraph()
            } else {
                commentText.text = "Graph is disconnected or there is only one vertex in the graph"
            }
        } else {
            commentText.text = graphWrapper.stepForward()
            changeButtonsState()
        }
    }

    @FXML
    private fun onFinishButtonClick() {
        if (isRedacted) {
            if (graphWrapper.getConnectivity() && !graphWrapper.oneVertexCheck()) {
                graphPane.children.clear()
                graphWrapper.findTree()
                commentText.text = graphWrapper.finalGraphState()
                changeButtonsState()
                isRedacted = false
                drawGraph()
            } else {
                commentText.text = "Graph is disconnected or there is only one vertex in the graph"
            }
        } else {
            commentText.text = graphWrapper.finalGraphState()
            changeButtonsState()
        }
    }


    @FXML
    private fun onAddVertexButtonClick() {
        val symbol = graphWrapper.getVacantVertexOrNull()
        if (symbol != null) {
            val newVertex = DrawVertex(symbol, graphCenterX, graphCenterY)

            commentText.text = "Click to set the vertex. Press ESC to cancel."
            graphWrapper.state = AlgorithmState.ADD_VERTEX
            graphPane.children.add(newVertex.getCircle())
            graphPane.children.add(newVertex.getText())
            changeButtonsState() // Disable buttons

            // Pressed ESC - cancel adding a vertex
            toolsMenu.setOnKeyPressed { keyEvent ->
                if (keyEvent.code == KeyCode.ESCAPE) {
                    graphWrapper.state = AlgorithmState.START
                    toolsMenu.setOnKeyPressed {}
                    graphPane.setOnMouseClicked {}
                    graphPane.setOnMouseMoved {}
                    graphPane.children.remove(newVertex.getCircle())
                    graphPane.children.remove(newVertex.getText())
                    commentText.text = "Zero step. Graph can be edited"
                    changeButtonsState()
                }
            }

            graphPane.setOnMouseMoved {
                newVertex.getCircle().centerX = it.x
                newVertex.getCircle().centerY = it.y
                newVertex.getText().x = it.x - nameAlignment
                newVertex.getText().y = it.y + nameAlignment
            }

            // Click to setting a vertex
            graphPane.setOnMouseClicked {
                graphWrapper.state = AlgorithmState.START
                graphPane.children.clear()
                toolsMenu.setOnKeyPressed {}
                graphPane.setOnMouseClicked {}
                graphPane.setOnMouseMoved {}
                graphWrapper.addInputVertex(newVertex)
                commentText.text = "The vertex $symbol has been set"
                changeButtonsState()
                drawGraph()
            }

        } else commentText.text = "The maximum number of vertices has been reached"
    }

    private fun deleteHandlerReset() {
        graphWrapper.state = AlgorithmState.START
        graphWrapper.changeStrokeColour() // Restore stroke colour
        toolsMenu.setOnKeyPressed {}
        graphWrapper.getDrawEdges().forEach { it.getLine().setOnMouseClicked {} }
        graphWrapper.getDrawVertices().forEach { it.getCircle().setOnMouseClicked {} }
        graphPane.children.clear()
        changeButtonsState()
        drawGraph()
    }

    @FXML
    private fun onDeleteButtonClick() {
        commentText.text = "Click on a vertex or edge to delete. Press ESC to cancel."
        graphWrapper.state = AlgorithmState.DELETION
        graphWrapper.changeStrokeColour() // Set deletion colour
        changeButtonsState()              // Disable buttons

        // Pressed ESC - cancel deletion
        toolsMenu.setOnKeyPressed { keyEvent ->
            if (keyEvent.code == KeyCode.ESCAPE) {
                commentText.text = "Zero step. Graph can be edited"
                deleteHandlerReset()
                changeButtonsState()
            }
        }

        // Deletion for edges
        graphWrapper.getDrawEdges().forEach { edge ->
            edge.getLine().setOnMouseClicked {
                commentText.text = "Edge ${edge.getEdge().node1} <-> ${edge.getEdge().node2} has been deleted"
                graphWrapper.deleteDrawObject(edge)
                deleteHandlerReset()
            }
        }

        // Deletion for vertices
        graphWrapper.getDrawVertices().forEach { vertex ->
            vertex.getCircle().setOnMouseClicked {
                commentText.text = "Vertex ${vertex.getName()} has been deleted"
                graphWrapper.deleteDrawObject(vertex)
                deleteHandlerReset()
            }
        }
    }

    @FXML
    private fun onClickClearGraph() {
        graphPane.children.clear()
        graphWrapper.clearDrawGraph()
    }

    fun setGraphWrapper(graphWrapper: KruskalWrapper) {
        this.graphWrapper = graphWrapper
    }

    private fun drawGraph() {
        graphWrapper.getDrawEdges().forEach { graphPane.children.add(it.getLine()) }
        graphWrapper.getDrawEdges().forEach { graphPane.children.add(it.getRectangular()) }
        graphWrapper.getDrawEdges().forEach { graphPane.children.add(it.getWeightText()) }
        graphWrapper.getDrawVertices().forEach { graphPane.children.add(it.getCircle()) }
        graphWrapper.getDrawVertices().forEach { graphPane.children.add(it.getText()) }
        makeDraggableVertices()
    }

    private fun changeButtonsState() {
        when (graphWrapper.getAlgState()) {
            AlgorithmState.START -> {
                startButton.isDisable = false
                nextStepButton.isDisable = false
                prevStepButton.isDisable = true
                finishButton.isDisable = false
                toolsMenu.isDisable = false
                fileButton.isDisable = false
                toolsMenu.items.forEach { it.isDisable = false }
            }

            AlgorithmState.IN_PROGRESS -> {
                nextStepButton.isDisable = false
                prevStepButton.isDisable = false
                finishButton.isDisable = false
                toolsMenu.isDisable = true
                fileButton.isDisable = true
            }

            AlgorithmState.FINISHED -> {
                nextStepButton.isDisable = true
                prevStepButton.isDisable = false
                finishButton.isDisable = true
                toolsMenu.isDisable = true
                fileButton.isDisable = true
            }

            AlgorithmState.ADD_VERTEX, AlgorithmState.DELETION -> {
                startButton.isDisable = true
                nextStepButton.isDisable = true
                prevStepButton.isDisable = true
                finishButton.isDisable = true
                fileButton.isDisable = true
                toolsMenu.items.forEach { it.isDisable = true }
            }
        }
    }

    private fun makeDraggableVertices() {
        for (i in graphWrapper.getDrawVertices()) {
            i.getCircle().setOnMouseDragged {
                if ((it.x < graphPane.width - dragBorder) && (it.x > dragBorder / 2) && (it.y < graphPane.height - dragBorder) && (it.y > dragBorder)) {
                    i.getCircle().centerX = it.x
                    i.getCircle().centerY = it.y
                    i.getText().x = i.getCircle().centerX - nameAlignment
                    i.getText().y = i.getCircle().centerY + nameAlignment
                    graphWrapper.changeEdgesAfterDrag(i)
                    i.getCircle().toFront()
                    i.getText().toFront()
                }
            }
        }
    }

}
