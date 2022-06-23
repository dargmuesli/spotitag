package de.dargmuesli.spotitag.persistence.state.providers

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import de.dargmuesli.spotitag.persistence.Persistence
import de.dargmuesli.spotitag.model.filesystem.MusicFile
import de.dargmuesli.spotitag.util.converter.IntConverter
import de.dargmuesli.spotitag.util.converter.SimpleIntegerPropertyConverter
import javafx.beans.property.SimpleIntegerProperty

object FileSystemState {
    @get:JsonSerialize(converter = SimpleIntegerPropertyConverter::class)
    @set:JsonDeserialize(converter = IntConverter::class)
    var filesFound = SimpleIntegerProperty(0)

    @get:JsonSerialize(converter = SimpleIntegerPropertyConverter::class)
    @set:JsonDeserialize(converter = IntConverter::class)
    var filesFoundWithSpotifyId = SimpleIntegerProperty(0)

    @get:JsonSerialize(converter = SimpleIntegerPropertyConverter::class)
    @set:JsonDeserialize(converter = IntConverter::class)
    var filesFoundWithSpotitagVersion = SimpleIntegerProperty(0)

    @get:JsonSerialize(converter = SimpleIntegerPropertyConverter::class)
    @set:JsonDeserialize(converter = IntConverter::class)
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
