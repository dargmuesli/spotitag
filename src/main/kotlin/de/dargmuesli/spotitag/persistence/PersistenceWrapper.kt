package de.dargmuesli.spotitag.persistence

import de.dargmuesli.spotitag.persistence.cache.SpotitagCache
import de.dargmuesli.spotitag.persistence.config.SpotitagConfig
import de.dargmuesli.spotitag.persistence.state.SpotitagState

object PersistenceWrapper {
    var cache = SpotitagCache
    var config = SpotitagConfig
    var state = SpotitagState

    operator fun get(persistenceType: PersistenceTypes): Any {
        return when (persistenceType) {
            PersistenceTypes.CACHE -> cache
            PersistenceTypes.CONFIG -> config
            PersistenceTypes.STATE -> state
        }
    }

    operator fun set(persistenceType: PersistenceTypes, value: Any) {
        when (persistenceType) {
            PersistenceTypes.CACHE -> cache = value as SpotitagCache
            PersistenceTypes.CONFIG -> config = value as SpotitagConfig
            PersistenceTypes.STATE -> state = value as SpotitagState
        }
    }
}
