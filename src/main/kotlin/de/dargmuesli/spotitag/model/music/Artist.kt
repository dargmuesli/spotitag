package de.dargmuesli.spotitag.model.music

data class Artist(
    val genres: List<String>? = null,
    val id: String? = null,
    val name: String? = null
) {
    override fun toString(): String {
        return name ?: super.toString()
    }
}