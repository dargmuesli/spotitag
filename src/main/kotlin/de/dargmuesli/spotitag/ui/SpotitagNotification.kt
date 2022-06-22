package de.dargmuesli.spotitag.ui

import de.dargmuesli.spotitag.ui.controller.NotificationController
import javafx.stage.Modality
import org.apache.logging.log4j.LogManager

object SpotitagNotification {
    fun displayError(text: String, e: Exception) {
        LogManager.getLogger().error(text, e)
        displayPopup(text, "Error")
    }

    fun displayInformation(text: String) {
        LogManager.getLogger().info(text)
        displayPopup(text, "Information")
    }

    fun displayWarning(text: String, e: Exception) {
        LogManager.getLogger().warn(text, e)
        displayPopup(text, "Warning")
    }

    private fun displayPopup(text: String, title: String = "Notification") {
        NotificationController.notifications.add(text)
        SpotitagStage("/de/dargmuesli/spotitag/fxml/notification.fxml", Modality.APPLICATION_MODAL, title, true).showAndWait()
    }
}