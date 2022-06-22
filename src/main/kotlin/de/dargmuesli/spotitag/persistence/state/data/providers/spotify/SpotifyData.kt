package de.dargmuesli.spotitag.persistence.state.data.providers.spotify

import se.michaelthelin.spotify.model_objects.specification.Track
import de.dargmuesli.spotitag.persistence.state.data.providers.IProviderData

object SpotifyData : IProviderData<Track> {
    override var trackData = mutableMapOf<String, Track>()

    var accessToken: String? = null
    var expiresAt: Long? = null
}