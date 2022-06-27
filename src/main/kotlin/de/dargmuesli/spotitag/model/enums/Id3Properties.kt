package de.dargmuesli.spotitag.model.enums

enum class Id3Properties(
    val type: String
) {
    TITLE("title"), ARTISTS("artists"), ALBUM("album"), ID("id"), COVER("cover");

    companion object {
        private val map: MutableMap<String, Id3Properties> = HashMap()

        init {
            for (albumType in values()) {
                map[albumType.type] = albumType
            }
        }

        fun keyOf(type: String): Id3Properties? {
            return map[type]
        }
    }
}