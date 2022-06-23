package de.dargmuesli.spotitag.persistence

enum class PersistenceTypes(val type: String) {
    CACHE("cache"),
    CONFIG("config"),
    STATE("state");

    companion object {
        private val map: MutableMap<String, PersistenceTypes> = HashMap()

        init {
            for (albumType in PersistenceTypes.values()) {
                map[albumType.type] = albumType
            }
        }

        fun keyOf(type: String): PersistenceTypes? {
            return map[type]
        }
    }
}