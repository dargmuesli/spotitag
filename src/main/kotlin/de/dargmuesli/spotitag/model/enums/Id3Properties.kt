package de.dargmuesli.spotitag.model.enums

enum class Id3Properties(
    val type: String
) {
    TITLE("title"), ARTISTS("artists"), ALBUM("album"), ID("id"), COVER("cover"), FILENAME("filename");

    companion object {
        private val map: MutableMap<String, Id3Properties> = HashMap()

        init {
            for (id3Property in values()) {
                map[id3Property.type] = id3Property
            }
        }

        fun keyOf(type: String): Id3Properties? {
            return map[type]
        }
    }
}