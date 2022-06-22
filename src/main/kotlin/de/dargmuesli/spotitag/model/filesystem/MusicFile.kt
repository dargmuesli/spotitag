package de.dargmuesli.spotitag.model.filesystem

import de.dargmuesli.spotitag.model.music.Track

data class MusicFile(val track: Track = Track(), val spotitagVersion: String? = null)