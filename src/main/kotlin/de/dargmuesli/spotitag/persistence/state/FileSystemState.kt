package de.dargmuesli.spotitag.persistence.state

import de.dargmuesli.spotitag.model.music.Track
import java.io.File

object FileSystemState {
    var currentFile: File? = null
    var currentTrack: Track? = null
}
