package de.dargmuesli.spotitag.persistence.state.settings.file_system

import de.dargmuesli.spotitag.persistence.Persistence
import kotlin.properties.Delegates

object FileSystemSettings {
    var sourceDirectory: String? by Delegates.observable(null) { _, _, _ ->
        Persistence.stateSave()
    }
    var isSubDirectoryIncluded: Boolean? by Delegates.observable(null) { _, _, _ ->
        Persistence.stateSave()
    }
}
