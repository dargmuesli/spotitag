package de.dargmuesli.spotitag.persistence.state.data.providers.file_system

import de.dargmuesli.spotitag.model.filesystem.MusicFile
import de.dargmuesli.spotitag.persistence.Persistence
import de.dargmuesli.spotitag.persistence.state.data.providers.IProviderData
import kotlin.properties.Delegates

object FileSystemData : IProviderData<MusicFile> {
    override var trackData: MutableMap<String, MusicFile> = mutableMapOf()
}