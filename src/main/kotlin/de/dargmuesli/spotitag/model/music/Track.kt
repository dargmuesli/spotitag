package de.dargmuesli.spotitag.model.music

data class Track(val album: Album = Album(),
                 val artists: List<Artist> = listOf(),
                 val durationMs: Int = -1,
                 val name: String = String()) {
    override fun toString(): String {
        return name
    }
}