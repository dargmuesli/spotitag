package de.dargmuesli.spotitag.persistence.state

import de.dargmuesli.spotitag.model.filesystem.MusicFile
import de.dargmuesli.spotitag.persistence.Persistence
import javafx.beans.property.SimpleIntegerProperty

object FileSystemState {
    var filesFound = SimpleIntegerProperty(0)
    var filesFoundWithSpotifyId = SimpleIntegerProperty(0)
    var filesFoundWithSpotitagVersion = SimpleIntegerProperty(0)
    var filesFoundWithSpotitagVersionNewest = SimpleIntegerProperty(0)

    internal fun updateCounter(musicFile: MusicFile, calc: (Int) -> Int) {
        filesFound.set(calc(filesFound.value))

        if (musicFile.spotitagVersion != null) {
            filesFoundWithSpotitagVersion.set(calc(filesFoundWithSpotitagVersion.value))

            if (musicFile.spotitagVersion.toString() == Persistence.getVersion()) {
                filesFoundWithSpotitagVersionNewest.set(calc(filesFoundWithSpotitagVersionNewest.value))
            }
        }

        if (musicFile.track.id !== null) {
            filesFoundWithSpotifyId.set(calc(filesFoundWithSpotifyId.value))
        }
    }
}
