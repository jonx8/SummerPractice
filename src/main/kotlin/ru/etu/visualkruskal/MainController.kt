package ru.etu.visualkruskal

import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.layout.GridPane
import javafx.scene.layout.Pane
import javafx.scene.text.Text
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
    private fun onClickFilePathDialog() {
        val fileDialog = TextInputDialog()
        fileDialog.title = null
        fileDialog.graphic = null
        fileDialog.headerText = "Enter path, please"
        fileDialog.contentText = "Path:"
        fileDialog.editor.text = "/home/"
        fileDialog.editor.style =
            " -fx-max-width:300px; -fx-max-height: 20px; -fx-pref-width: 300px; -fx-pref-height: 20px;"

        val result = fileDialog.showAndWait()
        graphPane.children.clear()
        if (result.isPresent) {
            val input = File(result.get())
            if (input.exists()) {
                if (input.length() != 0L && input.canRead() && !input.isDirectory ) {
                    if (graphWrapper.getGraphFromFile(input.readLines()))
                        commentText.text = "Graph read from file"
                    else commentText.text = "Incorrect data in file"
                } else commentText.text = "File must be not empty or must not be a directory"
            } else commentText.text = "There are no such file"
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
                    firstEdgeField.text,
                    secondEdgeField.text,
                    edgeWeightField.text
                )
            }
            null
        }
        val result = edgeDialog.showAndWait()
        if (result.isPresent) {
            graphWrapper.addInputEdge(result.get())
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
            if (graphWrapper.getConnectivity()) {
                graphPane.children.clear()
                graphWrapper.findTree()
                commentText.text = graphWrapper.stepForward()
                changeButtonsState()
                isRedacted = false
                drawGraph()
            } else{
                commentText.text = "Graph is disconnected"
            }
        } else {
            commentText.text = graphWrapper.stepForward()
            changeButtonsState()
        }
    }

    @FXML
    private fun onFinishButtonClick() {
        if (isRedacted) {
            if (graphWrapper.getConnectivity()) {
                graphPane.children.clear()
                graphWrapper.findTree()
                commentText.text = graphWrapper.finalGraphState()
                changeButtonsState()
                isRedacted = false
                drawGraph()
            }else{
                commentText.text = "Graph is disconnected"
            }
        } else {
            commentText.text = graphWrapper.finalGraphState()
            changeButtonsState()
        }
    }

    @FXML
    private fun onAddVertexButtonClick() {

    }

    @FXML
    private fun onDeleteButtonClick() {

    }

    @FXML
    private fun onClickClearGraph() {
        graphPane.children.clear()
        graphWrapper.clearGraphDrawables()
    }

    fun setGraphWrapper(graphWrapper: KruskalWrapper) {
        this.graphWrapper = graphWrapper
    }

    private fun drawGraph() {
        graphWrapper.getDrawEdges().forEach { graphPane.children.add(it.getLine()) }
        graphWrapper.getDrawEdges().forEach { graphPane.children.add(it.getWeightText()) }
        graphWrapper.getDrawVertices().forEach { graphPane.children.add(it.getCircle()) }
        graphWrapper.getDrawVertices().forEach { graphPane.children.add(it.getText()) }
        makeDraggableVertices()
    }

    private fun changeButtonsState() {
        when (graphWrapper.getAlgState()) {
            AlgorithmState.START -> {
                nextStepButton.isDisable = false
                prevStepButton.isDisable = true
                finishButton.isDisable = false
                toolsMenu.isDisable = false
                fileButton.isDisable = false
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
        }
    }

    private fun makeDraggableVertices() {
        for (i in graphWrapper.getDrawVertices()) {
            var deltaX = 0.0
            var deltaY = 0.0

            i.getCircle().setOnMousePressed {
                deltaX = it.sceneX - i.getCircle().centerX
                deltaY = it.sceneY - i.getCircle().centerY
            }
            i.getCircle().setOnMouseDragged {
                if ((it.sceneX < graphPane.width - dragBorder) and (it.sceneX > dragBorder) and (it.sceneY < graphPane.height - dragBorder) and (it.sceneY > dragBorder * 2)) {
                    i.getCircle().centerX = it.sceneX - deltaX
                    i.getCircle().centerY = it.sceneY - deltaY
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