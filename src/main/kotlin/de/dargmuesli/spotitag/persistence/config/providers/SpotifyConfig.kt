package de.dargmuesli.spotitag.persistence.config.providers

import de.dargmuesli.spotitag.persistence.Persistence
import kotlin.properties.Delegates

object SpotifyConfig {
    var clientId: String? by Delegates.observable(null) { _, _, _ ->
        Persistence.save()
    }
    var clientSecret: String? by Delegates.observable(null) { _, _, _ ->
        Persistence.save()
    }
}
