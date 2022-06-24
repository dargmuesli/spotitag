package de.dargmuesli.spotitag.model.music

import de.dargmuesli.spotitag.model.enums.AlbumType
import kotlinx.serialization.Serializable

@Serializable
data class Album(
    val albumType: AlbumType = AlbumType.ALBUM,
    val artists: List<Artist>? = null,
    val genres: List<String>? = null,
    val id: String? = null,
    val name: String? = null,
    val tracks: List<Track>? = null
) {
    override fun toString(): String {
        return name ?: super.toString()
    }
}