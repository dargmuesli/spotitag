package de.dargmuesli.spotitag.persistence.state.data.providers.file_system

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import de.dargmuesli.spotitag.model.filesystem.MusicFile
import de.dargmuesli.spotitag.persistence.Persistence
import de.dargmuesli.spotitag.persistence.state.data.providers.IProviderData
import de.dargmuesli.spotitag.util.converter.IntConverter
import de.dargmuesli.spotitag.util.converter.SimpleIntegerPropertyConverter
import javafx.beans.property.SimpleIntegerProperty
import javafx.collections.FXCollections.observableHashMap
import javafx.collections.MapChangeListener
import javafx.collections.ObservableMap


object FileSystemData : IProviderData<MusicFile> {
    override var trackData: ObservableMap<String, MusicFile> = observableHashMap<String, MusicFile>().also {
        it.addListener(
            MapChangeListener { change ->
                if (change.valueAdded != null) {
                    updateCounter(change.valueAdded) { x -> x + 1 }
                }

                if (change.valueRemoved != null) {
                    updateCounter(change.valueRemoved) { x -> x - 1 }
                }
            })
    }

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

    private fun updateCounter(musicFile: MusicFile, calc: (Int) -> Int) {
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