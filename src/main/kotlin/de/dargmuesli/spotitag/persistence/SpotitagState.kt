package de.dargmuesli.spotitag.persistence

import de.dargmuesli.spotitag.persistence.state.FileSystemState
import de.dargmuesli.spotitag.persistence.state.SpotifyState

object SpotitagState {
    var fileSystem: FileSystemState = FileSystemState
    var spotify: SpotifyState = SpotifyState
}
