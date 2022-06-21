package de.dargmuesli.spotitag.persistence.state.settings

import de.dargmuesli.spotitag.persistence.state.settings.spotify.SpotifySettings
import de.dargmuesli.spotitag.persistence.state.settings.youtube.YouTubeSettings

object SpotitagSettings {
    var sourceDirectory: String = String()

    var spotifySettings: SpotifySettings = SpotifySettings
    var youTubeSettings: YouTubeSettings = YouTubeSettings
}
