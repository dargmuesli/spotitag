package de.dargmuesli.spotitag.persistence.state.providers

import de.dargmuesli.spotitag.persistence.Persistence
import kotlin.properties.Delegates

object SpotifyState {
    var accessToken: String? by Delegates.observable(null) { _, _, _ ->
        Persistence.save()
    }
    var expiresAt: Long? by Delegates.observable(null) { _, _, _ ->
        Persistence.save()
    }
}
