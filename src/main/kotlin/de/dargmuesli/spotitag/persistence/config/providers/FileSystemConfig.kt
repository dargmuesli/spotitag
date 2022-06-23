package de.dargmuesli.spotitag.persistence.config.providers

import de.dargmuesli.spotitag.persistence.Persistence
import kotlin.properties.Delegates

object FileSystemConfig {
    var sourceDirectory: String? by Delegates.observable(null) { _, _, _ ->
        Persistence.save()
    }
    var isSubDirectoryIncluded: Boolean? by Delegates.observable(null) { _, _, _ ->
        Persistence.save()
    }
}
