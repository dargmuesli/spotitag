package de.dargmuesli.spotitag.persistence.cache

import de.dargmuesli.spotitag.model.filesystem.MusicFile
import javafx.collections.FXCollections.observableHashMap
import javafx.collections.ObservableMap
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


@Serializable(with = FileSystemCache.Serializer::class)
object FileSystemCache : IProviderCache<MusicFile> {
    override var trackData: ObservableMap<String, MusicFile> = observableHashMap()

    object Serializer : KSerializer<FileSystemCache> {
        override val descriptor: SerialDescriptor = FileSystemCacheSurrogate.serializer().descriptor

        override fun serialize(encoder: Encoder, value: FileSystemCache) {
            encoder.encodeSerializableValue(
                FileSystemCacheSurrogate.serializer(),
                FileSystemCacheSurrogate(trackData.toMap())
            )
        }

        override fun deserialize(decoder: Decoder): FileSystemCache {
            val fileSystemCache = decoder.decodeSerializableValue(FileSystemCacheSurrogate.serializer())
            trackData.putAll(fileSystemCache.trackData)
            return FileSystemCache
        }
    }

    @Serializable
    @SerialName("FileSystemCache")
    private data class FileSystemCacheSurrogate(val trackData: Map<String, MusicFile>)

    override fun clear() {
        for (entry in trackData) {
            trackData.remove(entry.key)
        }
    }
}