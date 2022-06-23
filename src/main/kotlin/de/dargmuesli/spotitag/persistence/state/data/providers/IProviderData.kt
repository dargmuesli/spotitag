package de.dargmuesli.spotitag.persistence.state.data.providers

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import de.dargmuesli.spotitag.util.converter.MapConverter
import de.dargmuesli.spotitag.util.converter.ObservableMapConverter
import javafx.collections.ObservableMap

interface IProviderData<TrackType> {
    @get:JsonSerialize(converter = ObservableMapConverter::class)
    @set:JsonDeserialize(converter = MapConverter::class)
    var trackData: ObservableMap<String, TrackType>
}