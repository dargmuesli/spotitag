package de.dargmuesli.spotitag.ui

import de.dargmuesli.spotitag.MainApp
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Control
import javafx.scene.image.Image
import javafx.stage.Modality
import javafx.stage.Stage
import org.apache.logging.log4j.LogManager

import java.io.IOException

class SpotitagStage(
    fxmlPath: String,
    modality: Modality,
    title: String,
    isAlwaysOnTop: Boolean = false,
    minHeight: Double = Control.USE_COMPUTED_SIZE,
    minWidth: Double = Control.USE_COMPUTED_SIZE
) : Stage() {
    init {
        try {
            val dashboard = FXMLLoader.load<Parent>(MainApp::class.java.getResource(fxmlPath))
            val scene = Scene(dashboard)

            if (minHeight != Control.USE_COMPUTED_SIZE) {
                this.minHeight = minHeight
            }

            if (minWidth != Control.USE_COMPUTED_SIZE) {
                this.minWidth = minWidth
            }

            this.scene = scene
            this.title = MainApp.APPLICATION_TITLE + " - " + title
            this.icons.add(Image(javaClass.getResourceAsStream("/de/dargmuesli/spotitag/icons/spotitag.png")))
            this.isAlwaysOnTop = isAlwaysOnTop
            this.initModality(modality)

            if (MainApp.isStageInitialized()) {
                this.initOwner(MainApp.stage)
            } else {
                this.initOwner(Stage())
            }
        } catch (e: IOException) {
            LogManager.getLogger().error("Construction of SpotitagStage failed!", e)
        }
    }

    companion object {
        internal fun makeSpotitagStage(stage: Stage) {
            stage.title = MainApp.APPLICATION_TITLE
            stage.icons.add(Image(MainApp().javaClass.getResourceAsStream("/de/dargmuesli/spotitag/icons/spotitag.png")))
        }
    }
}
