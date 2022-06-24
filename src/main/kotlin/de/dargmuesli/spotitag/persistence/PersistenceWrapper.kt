package de.dargmuesli.spotitag.persistence

import kotlinx.serialization.Serializable

@Serializable
object PersistenceWrapper {
    var cache = SpotitagCache
    var config = SpotitagConfig
    var state = SpotitagState

    operator fun get(persistenceType: PersistenceTypes): AbstractSerializable {
        return when (persistenceType) {
            PersistenceTypes.CACHE -> cache
            PersistenceTypes.CONFIG -> config
            PersistenceTypes.STATE -> state
        }
    }

    operator fun set(persistenceType: PersistenceTypes, value: AbstractSerializable) {
        when (persistenceType) {
            PersistenceTypes.CACHE -> cache = value as SpotitagCache
            PersistenceTypes.CONFIG -> config = value as SpotitagConfig
            PersistenceTypes.STATE -> state = value as SpotitagState
        }
    }
}
