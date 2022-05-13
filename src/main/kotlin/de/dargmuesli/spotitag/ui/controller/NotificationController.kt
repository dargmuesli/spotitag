package de.dargmuesli.spotitag.ui.controller

import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Label
import java.net.URL
import java.util.*

class NotificationController : Initializable {

    @FXML
    lateinit var lblNotification: Label

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        lblNotification.text = notifications.removeAt(0)
    }

    companion object {
        var notifications: ArrayList<String> = ArrayList()
    }
}
