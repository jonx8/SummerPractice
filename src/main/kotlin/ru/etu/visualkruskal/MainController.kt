package ru.etu.visualkruskal

import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.layout.GridPane
import javafx.scene.layout.Pane
import javafx.scene.text.Text
import java.io.File


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


    private lateinit var graphWrapper: KruskalWrapper

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
            if(input.exists()) {
                if(input.length() != 0L && input.canRead()) {
                    if (graphWrapper.getGraphFromFile(input.readLines()))
                        commentText.text = "Graph read from file"
                    else commentText.text = "Incorrect data in file"
                }
                else commentText.text = "File must be not empty"
            }
            else commentText.text = "There are no such file"
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
            /*
            val arr = result.get()
            commentText.text = "first - ${arr[0]} second - ${arr[1]} weight ${arr[2]} " //test, should be deleted later
             */
        }

    }

    @FXML
    private fun onStartButtonClick() {
        graphPane.children.clear()
        graphWrapper.initialGraphState()
        drawGraph()
        changeButtonsState()
    }

    @FXML
    private fun onStepPrevButtonClick() {
        graphPane.children.clear()
        graphWrapper.stepBack()
        drawGraph()
        changeButtonsState()
    }

    @FXML
    private fun onStepNextButtonClick() {
        graphPane.children.clear()
        graphWrapper.stepForward()
        drawGraph()
        changeButtonsState()
    }

    @FXML
    private fun onFinishButtonClick() {
        graphPane.children.clear()
        graphWrapper.finalGraphState()
        drawGraph()
        changeButtonsState()
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
        graphPane.children.addAll(graphWrapper.getLines())
        graphPane.children.addAll(graphWrapper.getTexts("Lines"))
        graphPane.children.addAll(graphWrapper.getCircles())
        graphPane.children.addAll(graphWrapper.getTexts("Circles"))
    }

    private fun changeButtonsState() {
        when (graphWrapper.returnAlgState()) {
            AlgorithmState.START -> {
                nextStepButton.isDisable = false
                prevStepButton.isDisable = true
                finishButton.isDisable = false
                toolsMenu.isDisable = false
            }

            AlgorithmState.IN_PROGRESS -> {
                nextStepButton.isDisable = false
                prevStepButton.isDisable = false
                finishButton.isDisable = false
                toolsMenu.isDisable = true
            }

            AlgorithmState.FINISHED -> {
                nextStepButton.isDisable = true
                prevStepButton.isDisable = false
                finishButton.isDisable = true
                toolsMenu.isDisable = true
            }
        }
    }

}
