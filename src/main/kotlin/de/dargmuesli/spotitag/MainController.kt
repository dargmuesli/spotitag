package de.dargmuesli.spotitag

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.TextField
import javafx.stage.DirectoryChooser
import javafx.stage.Stage
import java.io.File


class MainController {
    private val directoryChooser = DirectoryChooser()

    @FXML
    private lateinit var directoryChosenTextField: TextField

    fun chooseDirectory(actionEvent: ActionEvent) {
        val file: File = directoryChooser.showDialog((actionEvent.source as Node).scene.window as Stage)

        directoryChosenTextField.text=file.absolutePath
    }
}