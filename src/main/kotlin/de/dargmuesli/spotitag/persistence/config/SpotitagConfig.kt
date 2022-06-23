package de.dargmuesli.spotitag.persistence.config

import de.dargmuesli.spotitag.persistence.config.providers.FileSystemConfig
import de.dargmuesli.spotitag.persistence.config.providers.SpotifyConfig

object SpotitagConfig {
    var fileSystem: FileSystemConfig = FileSystemConfig
    var spotify: SpotifyConfig = SpotifyConfig
}
