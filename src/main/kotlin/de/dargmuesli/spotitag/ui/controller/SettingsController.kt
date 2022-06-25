package de.dargmuesli.spotitag.ui.controller

import de.dargmuesli.spotitag.persistence.config.SpotifyConfig
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.TextField
import java.net.URL
import java.util.*


class SettingsController : Initializable {

    @FXML
    private lateinit var spotifyClientIdTextField: TextField

    @FXML
    private lateinit var spotifyClientSecretTextField: TextField

    override fun initialize(url: URL?, rb: ResourceBundle?) {
        SpotifyConfig.clientId.addListener { _ ->
            spotifyClientIdTextField.text = SpotifyConfig.clientId.value
        }
        SpotifyConfig.clientSecret.addListener { _ ->
            spotifyClientSecretTextField.text = SpotifyConfig.clientSecret.value
        }
    }
}
