package de.dargmuesli.spotitag.persistence.cache

import de.dargmuesli.spotitag.persistence.cache.providers.FileSystemCache
import de.dargmuesli.spotitag.persistence.cache.providers.SpotifyCache

object SpotitagCache {
    var fileSystem = FileSystemCache
    var spotify = SpotifyCache
}
