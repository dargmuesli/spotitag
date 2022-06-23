package de.dargmuesli.spotitag.ui.controller

import de.dargmuesli.spotitag.persistence.config.providers.SpotifyConfig
import de.dargmuesli.spotitag.util.Etter
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
        val txtToEtterMap = mapOf(
            spotifyClientIdTextField to Etter({ SpotifyConfig.clientId }, { SpotifyConfig.clientId = it }),
            spotifyClientSecretTextField to Etter(
                { SpotifyConfig.clientSecret },
                { SpotifyConfig.clientSecret = it })
        )

        for ((textField, etter) in txtToEtterMap) {
            textField.text = etter.getter.invoke()
            textField.textProperty().addListener { _, _, newText -> etter.setter.invoke(newText) }
        }
    }
}
