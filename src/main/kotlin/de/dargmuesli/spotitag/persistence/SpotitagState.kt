package de.dargmuesli.spotitag.persistence

import de.dargmuesli.spotitag.persistence.cache.FileSystemCache
import de.dargmuesli.spotitag.persistence.state.FileSystemState
import de.dargmuesli.spotitag.persistence.state.SpotifyState

object SpotitagState {
    var fileSystem: FileSystemState = FileSystemState
    var spotify: SpotifyState = SpotifyState

    fun refresh() {
        fileSystem.filesFound.set(0)
        fileSystem.filesFoundWithSpotifyId.set(0)
        fileSystem.filesFoundWithSpotitagVersion.set(0)
        fileSystem.filesFoundWithSpotitagVersionNewest.set(0)

        for (musicFile in FileSystemCache.trackData.values) {
            FileSystemState.updateCounter(musicFile) { x -> x + 1 }
        }
    }
}
