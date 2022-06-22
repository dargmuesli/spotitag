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
import kotlin.system.exitProcess

class MainApp : Application() {
    override fun start(stage: Stage) {
        Persistence.stateLoad()

        SpotitagStage.makeSpotitagStage(stage)

        Companion.stage = stage

        stage.setOnCloseRequest {
            Persistence.stateSave()
            exitProcess(0)
        }

        try {
            val resources = ResourceBundle.getBundle("i18n", Locale.getDefault())
            val dashboardLoader = FXMLLoader(MainApp::class.java.getResource("dashboard.fxml"), resources)
            val dashboard = dashboardLoader.load<Parent>()
            dashboardController = dashboardLoader.getController()

            val scene = Scene(dashboard)

            stage.scene = scene
            stage.show()
        } catch (e: IOException) {
            LogManager.getLogger().error("Loading the dashboard failed!", e)
        }
    }

    companion object {
        lateinit var stage: Stage
        lateinit var dashboardController: DashboardController

        internal const val APPLICATION_TITLE = "Spotitag"

        @JvmStatic
        fun main(args: Array<String>) {
            launch(MainApp::class.java, *args)
        }

        fun isStageInitialized() = this::stage.isInitialized
    }
}

//fun main() {
//    Application.launch(MainApp::class.java)
//}