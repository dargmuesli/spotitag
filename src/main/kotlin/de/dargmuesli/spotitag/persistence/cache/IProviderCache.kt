package de.dargmuesli.spotitag.persistence.cache

import javafx.collections.ObservableMap

interface IProviderCache<TrackType> : IClearable {
    var trackData: ObservableMap<String, TrackType>
}