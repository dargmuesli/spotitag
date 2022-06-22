package de.dargmuesli.spotitag.persistence.state.data

import de.dargmuesli.spotitag.persistence.state.data.providers.file_system.FileSystemData
import de.dargmuesli.spotitag.persistence.state.data.providers.spotify.SpotifyData

object SpotitagData {
    var fileSystemData = FileSystemData
    var spotifyData = SpotifyData
}
