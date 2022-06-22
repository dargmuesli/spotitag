package de.dargmuesli.spotitag.persistence.state.data.providers.spotify

import de.dargmuesli.spotitag.persistence.Persistence
import de.dargmuesli.spotitag.persistence.state.data.providers.IProviderData
import se.michaelthelin.spotify.model_objects.specification.Track
import kotlin.properties.Delegates

object SpotifyData : IProviderData<Track> {
    override var trackData: MutableMap<String, Track> = mutableMapOf()

    var accessToken: String? by Delegates.observable(null) { _, _, _ ->
        Persistence.stateSave()
    }
    var expiresAt: Long? by Delegates.observable(null) { _, _, _ ->
        Persistence.stateSave()
    }
}