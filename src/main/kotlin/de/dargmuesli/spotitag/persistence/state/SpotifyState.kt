package de.dargmuesli.spotitag.persistence.state

import de.dargmuesli.spotitag.model.music.Track

object SpotifyState {
    var accessToken: String? = null
    var expiresAt: Long? = null

    var currentTrack: Track? = null
}
