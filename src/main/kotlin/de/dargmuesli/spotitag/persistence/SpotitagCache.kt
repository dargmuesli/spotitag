package de.dargmuesli.spotitag.persistence

import de.dargmuesli.spotitag.persistence.cache.FileSystemCache
import de.dargmuesli.spotitag.persistence.cache.SpotifyCache
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


@Serializable(with = SpotitagCache.Serializer::class)
object SpotitagCache : AbstractSerializable() {
    var fileSystem = FileSystemCache
    var spotify = SpotifyCache

    object Serializer : KSerializer<SpotitagCache> {
        override val descriptor: SerialDescriptor = SpotitagCacheSurrogate.serializer().descriptor

        override fun serialize(encoder: Encoder, value: SpotitagCache) {
            encoder.encodeSerializableValue(
                SpotitagCacheSurrogate.serializer(),
                SpotitagCacheSurrogate(fileSystem, spotify)
            )
        }

        override fun deserialize(decoder: Decoder): SpotitagCache {
            val spotitagCache = decoder.decodeSerializableValue(SpotitagCacheSurrogate.serializer())
            fileSystem = spotitagCache.fileSystem
            spotify = spotitagCache.spotify
            return SpotitagCache
        }
    }

    @Serializable
    @SerialName("SpotitagCache")
    private data class SpotitagCacheSurrogate(val fileSystem: FileSystemCache, val spotify: SpotifyCache)
}
