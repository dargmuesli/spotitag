package de.dargmuesli.spotitag.persistence.config

import de.dargmuesli.spotitag.persistence.Persistence
import de.dargmuesli.spotitag.persistence.PersistenceTypes
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.properties.Delegates

@Serializable(with = FileSystemConfig.Serializer::class)
object FileSystemConfig {
    var sourceDirectory: String? by Delegates.observable(null) { _, _, _ ->
        Persistence.save(PersistenceTypes.CONFIG)
    }
    var isSubDirectoryIncluded: Boolean? by Delegates.observable(null) { _, _, _ ->
        Persistence.save(PersistenceTypes.CONFIG)
    }

    object Serializer : KSerializer<FileSystemConfig> {
        override val descriptor: SerialDescriptor = FileSystemConfigSurrogate.serializer().descriptor

        override fun serialize(encoder: Encoder, value: FileSystemConfig) {
            encoder.encodeSerializableValue(
                FileSystemConfigSurrogate.serializer(),
                FileSystemConfigSurrogate(sourceDirectory, isSubDirectoryIncluded)
            )
        }

        override fun deserialize(decoder: Decoder): FileSystemConfig {
            val fileSystemConfig = decoder.decodeSerializableValue(FileSystemConfigSurrogate.serializer())
            sourceDirectory = fileSystemConfig.sourceDirectory
            isSubDirectoryIncluded = fileSystemConfig.isSubDirectoryIncluded
            return FileSystemConfig
        }
    }

    @Serializable
    @SerialName("FileSystemConfig")
    private data class FileSystemConfigSurrogate(val sourceDirectory: String?, val isSubDirectoryIncluded: Boolean?)
}
