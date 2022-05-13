package de.dargmuesli.spotitag.model.music

data class Artist(val genres: List<String> = listOf(),
                  val name: String = String()) {
    override fun toString(): String {
        return name
    }
}