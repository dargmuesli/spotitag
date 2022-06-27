package de.dargmuesli.spotitag.persistence

import de.dargmuesli.spotitag.persistence.state.FileSystemState
import de.dargmuesli.spotitag.persistence.state.SpotifyState

object SpotitagState {
    val fileSystem: FileSystemState = FileSystemState
    val spotify: SpotifyState = SpotifyState
}
