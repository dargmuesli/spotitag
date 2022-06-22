package de.dargmuesli.spotitag.persistence.state.settings.spotify

import de.dargmuesli.spotitag.persistence.Persistence
import java.net.URI
import kotlin.properties.Delegates

object SpotifySettings {
    var clientId: String? by Delegates.observable(null) { _, _, _ ->
        Persistence.stateSave()
    }
    var clientSecret: String? by Delegates.observable(null) { _, _, _ ->
        Persistence.stateSave()
    }
    var redirectUri: URI? by Delegates.observable(null) { _, _, _ ->
        Persistence.stateSave()
    }
}
