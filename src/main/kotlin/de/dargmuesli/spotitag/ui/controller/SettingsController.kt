package de.dargmuesli.spotitag.ui.controller

import de.dargmuesli.spotitag.persistence.SpotitagConfig
import de.dargmuesli.spotitag.persistence.config.SpotifyConfig
import de.dargmuesli.spotitag.util.Util.integerFilter
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.CheckBox
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
    private lateinit var isIdCheckedCheckBox: CheckBox

    @FXML
    private lateinit var isCoverCheckedCheckBox: CheckBox

    @FXML
    private lateinit var durationToleranceTextField: TextField

    override fun initialize(url: URL?, rb: ResourceBundle?) {
        spotifyClientIdTextField.text = SpotifyConfig.clientId.value
        spotifyClientSecretTextField.text = SpotifyConfig.clientSecret.value
        isIdCheckedCheckBox.isSelected = SpotitagConfig.isIdChecked.value
        isCoverCheckedCheckBox.isSelected = SpotitagConfig.isCoverChecked.value
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
    fun onIsIdCheckedInput() {
        SpotitagConfig.isIdChecked.set(isIdCheckedCheckBox.isSelected)
    }

    @FXML
    fun onIsCoverCheckedInput() {
        SpotitagConfig.isCoverChecked.set(isCoverCheckedCheckBox.isSelected)
    }

    @FXML
    fun onDurationToleranceInput() {
        SpotitagConfig.durationTolerance.set(durationToleranceTextField.text.toLong())
    }
}
