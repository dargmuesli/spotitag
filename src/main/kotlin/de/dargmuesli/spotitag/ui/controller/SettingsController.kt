package de.dargmuesli.spotitag.ui.controller

import de.dargmuesli.spotitag.persistence.SpotitagConfig
import de.dargmuesli.spotitag.persistence.config.SpotifyConfig
import de.dargmuesli.spotitag.util.Util.integerFilter
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.TextField
import javafx.scene.control.TextFormatter
import javafx.util.converter.IntegerStringConverter
import java.net.URL
import java.util.*


class SettingsController : Initializable {

    @FXML
    private lateinit var spotifyClientIdTextField: TextField

    @FXML
    private lateinit var spotifyClientSecretTextField: TextField

    @FXML
    private lateinit var durationToleranceTextField: TextField

    override fun initialize(url: URL?, rb: ResourceBundle?) {
        spotifyClientIdTextField.text = SpotifyConfig.clientId.value
        spotifyClientSecretTextField.text = SpotifyConfig.clientSecret.value
        durationToleranceTextField.textFormatter = TextFormatter(IntegerStringConverter(), 0, integerFilter)
        durationToleranceTextField.text = SpotitagConfig.durationTolerance.value.toString()
    }

    @FXML
    fun onClientIdInput() {
        SpotitagConfig.spotify.clientId.set(spotifyClientIdTextField.text)
    }

    @FXML
    fun onClientSecretInput() {
        SpotitagConfig.spotify.clientSecret.set(spotifyClientSecretTextField.text)
    }

    @FXML
    fun onDurationToleranceInput() {
        SpotitagConfig.durationTolerance.set(durationToleranceTextField.text.toLong())
    }
}
