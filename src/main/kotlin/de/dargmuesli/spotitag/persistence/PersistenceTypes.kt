package de.dargmuesli.spotitag.persistence

import kotlinx.serialization.Serializable

enum class PersistenceTypes(@Serializable val spotitagPersistence: AbstractSerializable) {
    CACHE(SpotitagCache),
    CONFIG(SpotitagConfig);

    companion object {
        private val map: MutableMap<AbstractSerializable, PersistenceTypes> = HashMap()

        init {
            for (albumType in PersistenceTypes.values()) {
                map[albumType.spotitagPersistence] = albumType
            }
        }

        fun keyOf(type: AbstractSerializable): PersistenceTypes? {
            return map[type]
        }
    }
}