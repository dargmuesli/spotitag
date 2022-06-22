package de.dargmuesli.spotitag.ui

import de.dargmuesli.spotitag.ui.controller.NotificationController
import javafx.stage.Modality
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.javafx.JavaFxDispatcher
import kotlinx.coroutines.launch
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

object SpotitagNotification : CoroutineScope {
    private val LOGGER: Logger = LogManager.getLogger()

    fun error(text: String, e: Exception? = null) {
        LOGGER.error(text, e)
        launch(Dispatchers.JavaFx) {
            displayPopup(text, "Error")
        }
    }

    fun info(text: String) {
        LOGGER.info(text)
        launch(Dispatchers.JavaFx) {
            displayPopup(text, "Information")
        }
    }

    fun warn(text: String, e: Exception? = null) {
        LOGGER.warn(text, e)
        launch(Dispatchers.JavaFx) {
            displayPopup(text, "Warning")
        }
    }

    private fun displayPopup(text: String, title: String = "Notification") {
        NotificationController.notifications.add(text)
        SpotitagStage(
            "/de/dargmuesli/spotitag/fxml/notification.fxml",
            Modality.APPLICATION_MODAL,
            title,
            true
        ).showAndWait()
    }

    override val coroutineContext: JavaFxDispatcher
        get() = Dispatchers.JavaFx
}