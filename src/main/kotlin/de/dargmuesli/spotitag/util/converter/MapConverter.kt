package de.dargmuesli.spotitag.util.converter

import com.fasterxml.jackson.databind.util.StdConverter
import javafx.collections.FXCollections.observableHashMap
import javafx.collections.ObservableMap


class MapConverter<K, V> : StdConverter<Map<K, V>, ObservableMap<K, V>>() {
    override fun convert(value: Map<K, V>?): ObservableMap<K, V> {
        return observableHashMap<K, V>().also {
            it.putAll(value!!)
        }
    }
}