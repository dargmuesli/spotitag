package de.dargmuesli.spotitag.model.enums

enum class AlbumType(
    val type: String
) {
    ALBUM("album"), APPEARS_ON("appears_on"), COMPILATION("compilation"), SINGLE("single");

    companion object {
        private val map: MutableMap<String, AlbumType> = HashMap()

        init {
            for (albumType in values()) {
                map[albumType.type] = albumType
            }
        }

        fun keyOf(type: String): AlbumType? {
            return map[type]
        }
    }
}