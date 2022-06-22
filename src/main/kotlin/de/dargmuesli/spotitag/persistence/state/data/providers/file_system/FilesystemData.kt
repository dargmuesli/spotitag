package de.dargmuesli.spotitag.persistence.state.data.providers.file_system

import de.dargmuesli.spotitag.model.filesystem.MusicFile
import de.dargmuesli.spotitag.persistence.state.data.providers.IProviderData

object FileSystemData : IProviderData<MusicFile> {
    override var trackData = mutableMapOf<String, MusicFile>()
}