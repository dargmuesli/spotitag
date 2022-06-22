package de.dargmuesli.spotitag.persistence.state.settings

import de.dargmuesli.spotitag.persistence.state.settings.file_system.FileSystemSettings
import de.dargmuesli.spotitag.persistence.state.settings.spotify.SpotifySettings

object SpotitagSettings {
    var fileSystem: FileSystemSettings = FileSystemSettings
    var spotify: SpotifySettings = SpotifySettings
}
