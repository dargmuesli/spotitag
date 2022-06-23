package de.dargmuesli.spotitag.persistence.cache.providers

import de.dargmuesli.spotitag.model.filesystem.MusicFile
import de.dargmuesli.spotitag.persistence.cache.IProviderCache
import de.dargmuesli.spotitag.persistence.state.providers.FileSystemState.updateCounter
import javafx.collections.FXCollections.observableHashMap
import javafx.collections.MapChangeListener
import javafx.collections.ObservableMap


object FileSystemCache : IProviderCache<MusicFile> {
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
}