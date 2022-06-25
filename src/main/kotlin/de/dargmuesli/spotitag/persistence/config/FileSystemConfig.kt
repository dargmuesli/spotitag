package de.dargmuesli.spotitag.persistence.config

import de.dargmuesli.spotitag.persistence.Persistence
import de.dargmuesli.spotitag.persistence.PersistenceTypes
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = FileSystemConfig.Serializer::class)
object FileSystemConfig {
    var sourceDirectory = SimpleStringProperty().also {
        it.addListener { _ ->
            Persistence.save(PersistenceTypes.CONFIG)
        }
    }
    var isSubDirectoryIncluded = SimpleBooleanProperty().also {
        it.addListener { _ ->
            Persistence.save(PersistenceTypes.CONFIG)
        }
    }

    object Serializer : KSerializer<FileSystemConfig> {
        override val descriptor: SerialDescriptor = FileSystemConfigSurrogate.serializer().descriptor

        override fun serialize(encoder: Encoder, value: FileSystemConfig) {
            encoder.encodeSerializableValue(
                FileSystemConfigSurrogate.serializer(),
                FileSystemConfigSurrogate(sourceDirectory.value, isSubDirectoryIncluded.value)
            )
        }

        override fun deserialize(decoder: Decoder): FileSystemConfig {
            val fileSystemConfig = decoder.decodeSerializableValue(FileSystemConfigSurrogate.serializer())
            sourceDirectory.set(fileSystemConfig.sourceDirectory)
            isSubDirectoryIncluded.set(fileSystemConfig.isSubDirectoryIncluded ?: false)
            return FileSystemConfig
        }
    }

    @Serializable
    @SerialName("FileSystemConfig")
    private data class FileSystemConfigSurrogate(val sourceDirectory: String?, val isSubDirectoryIncluded: Boolean?)
}
