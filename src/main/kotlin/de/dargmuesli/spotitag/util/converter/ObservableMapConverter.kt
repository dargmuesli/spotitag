package de.dargmuesli.spotitag.util.converter

import com.fasterxml.jackson.databind.util.StdConverter
import javafx.collections.ObservableMap


class ObservableMapConverter<K, V> : StdConverter<ObservableMap<K, V>, Map<K, V>>() {
    override fun convert(value: ObservableMap<K, V>?): Map<K, V> {
        return value?.toMutableMap() ?: mutableMapOf()
    }
}