package de.dargmuesli.spotitag.persistence.cache.providers

import de.dargmuesli.spotitag.persistence.cache.IProviderCache
import javafx.collections.FXCollections.observableHashMap
import javafx.collections.ObservableMap
import se.michaelthelin.spotify.model_objects.specification.Track

object SpotifyCache : IProviderCache<Track> {
    override var trackData: ObservableMap<String, Track> = observableHashMap()
}