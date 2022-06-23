package de.dargmuesli.spotitag.persistence.state

import de.dargmuesli.spotitag.persistence.state.providers.FileSystemState
import de.dargmuesli.spotitag.persistence.state.providers.SpotifyState

object SpotitagState {
    var fileSystem: FileSystemState = FileSystemState
    var spotify: SpotifyState = SpotifyState
}
