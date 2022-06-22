package de.dargmuesli.spotitag.ui.controller

import de.dargmuesli.spotitag.persistence.state.settings.spotify.SpotifySettings
import de.dargmuesli.spotitag.provider.spotify.SpotifyUtil
import de.dargmuesli.spotitag.util.Etter
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Button
import javafx.scene.control.TextField
import java.net.MalformedURLException
import java.net.URI
import java.net.URISyntaxException
import java.net.URL
import java.util.*


class SettingsController : Initializable {

    @FXML
    private lateinit var spotifyClientIdTextField: TextField

    @FXML
    private lateinit var spotifyClientSecretTextField: TextField

    @FXML
    private lateinit var spotifyRedirectUriTextField: TextField

    @FXML
    private lateinit var btnOpenAuthorization: Button

    override fun initialize(url: URL?, rb: ResourceBundle?) {
        val uriToEtterMap = mapOf(
            spotifyRedirectUriTextField to Etter({ SpotifySettings.redirectUri }, { SpotifySettings.redirectUri = it })
        )

        for ((textField, etter) in uriToEtterMap) {
            textField.text = etter.getter.invoke().toString()
            textField.textProperty().addListener { _, _, newText -> etter.setter.invoke(URI(newText)) }
        }

        val txtToEtterMap = mapOf(
            spotifyClientIdTextField to Etter({ SpotifySettings.clientId }, { SpotifySettings.clientId = it }),
            spotifyClientSecretTextField to Etter(
                { SpotifySettings.clientSecret },
                { SpotifySettings.clientSecret = it })
        )

        for ((textField, etter) in txtToEtterMap) {
            textField.text = etter.getter.invoke()
            textField.textProperty().addListener { _, _, newText -> etter.setter.invoke(newText) }

            if (textField in setOf(
                    spotifyClientIdTextField,
                    spotifyClientSecretTextField,
                    spotifyRedirectUriTextField
                )
            ) {
                textField.textProperty().addListener { _, _, _ ->
                    btnOpenAuthorization.isDisable = !isAuthorizable()
                }

                if (textField == spotifyRedirectUriTextField) {
                    SpotifyUtil.spotifyApi = SpotifyUtil.spotifyApiBuilder.build()
                }
            }
        }

        btnOpenAuthorization.isDisable = !isAuthorizable()
    }

    private fun isAuthorizable(): Boolean {
        if (spotifyClientIdTextField.text == ""
            || spotifyClientSecretTextField.text == ""
            || spotifyRedirectUriTextField.text == ""
        ) {
            return false
        }

        return try {
            URL(spotifyRedirectUriTextField.text).toURI()
            true
        } catch (exception: URISyntaxException) {
            false
        } catch (exception: MalformedURLException) {
            false
        }
    }

    @FXML
    private fun openAuthorization() {
        SpotifyUtil.openAuthorization()
    }
}
