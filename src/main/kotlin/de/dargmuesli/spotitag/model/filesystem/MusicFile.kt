package de.dargmuesli.spotitag.model.filesystem

import de.dargmuesli.spotitag.model.music.Track
import kotlinx.serialization.Serializable

@Serializable
data class MusicFile(val path: String, val track: Track, val spotitagVersion: String? = null)