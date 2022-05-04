package de.dargmuesli.spotitag

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage
import java.util.*

class HelloApplication : Application() {
    override fun start(stage: Stage) {
        val resources = ResourceBundle.getBundle("i18n", Locale.getDefault())
        val fxmlLoader = FXMLLoader(HelloApplication::class.java.getResource("main.fxml"), resources)
        val scene = Scene(fxmlLoader.load())
        stage.title = "Hello!"
        stage.scene = scene
        stage.show()
    }
}

fun main() {
    Application.launch(HelloApplication::class.java)
}