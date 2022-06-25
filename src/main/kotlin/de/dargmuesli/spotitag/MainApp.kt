package de.dargmuesli.spotitag

import de.dargmuesli.spotitag.persistence.Persistence
import de.dargmuesli.spotitag.ui.SpotitagStage
import de.dargmuesli.spotitag.ui.controller.DashboardController
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import org.apache.logging.log4j.LogManager
import java.io.IOException
import java.util.*

class MainApp : Application() {
    override fun start(stage: Stage) {

        SpotitagStage.makeSpotitagStage(stage)
        Companion.stage = stage

        try {
            val dashboardLoader = FXMLLoader(MainApp::class.java.getResource("fxml/dashboard.fxml"), resources)
            val dashboard = dashboardLoader.load<Parent>()
            dashboardController = dashboardLoader.getController()

            val scene = Scene(dashboard)

            stage.scene = scene
            stage.show()
        } catch (e: IOException) {
            LogManager.getLogger().error("Loading the dashboard failed!", e)
        }

        Persistence.load()
    }

    companion object {
        lateinit var stage: Stage
        lateinit var dashboardController: DashboardController
        val resources: ResourceBundle = ResourceBundle.getBundle("i18n", Locale.getDefault())

        internal const val APPLICATION_TITLE = "Spotitag"

        @JvmStatic
        fun main(args: Array<String>) {
            launch(MainApp::class.java, *args)
        }

        fun isStageInitialized() = this::stage.isInitialized
    }
}
