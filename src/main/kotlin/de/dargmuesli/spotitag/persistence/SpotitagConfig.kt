package de.dargmuesli.spotitag.persistence

import de.dargmuesli.spotitag.persistence.config.FileSystemConfig
import de.dargmuesli.spotitag.persistence.config.SpotifyConfig
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = SpotitagConfig.Serializer::class)
object SpotitagConfig : AbstractSerializable() {
    var fileSystem: FileSystemConfig = FileSystemConfig
    var spotify: SpotifyConfig = SpotifyConfig

    object Serializer : KSerializer<SpotitagConfig> {
        override val descriptor: SerialDescriptor = SpotitagConfigSurrogate.serializer().descriptor

        override fun serialize(encoder: Encoder, value: SpotitagConfig) {
            encoder.encodeSerializableValue(
                SpotitagConfigSurrogate.serializer(),
                SpotitagConfigSurrogate(fileSystem, spotify)
            )
        }

        override fun deserialize(decoder: Decoder): SpotitagConfig {
            val spotitagConfig = decoder.decodeSerializableValue(SpotitagConfigSurrogate.serializer())
            fileSystem = spotitagConfig.fileSystem
            spotify = spotitagConfig.spotify
            return SpotitagConfig
        }
    }

    @Serializable
    @SerialName("SpotitagConfig")
    private data class SpotitagConfigSurrogate(val fileSystem: FileSystemConfig, val spotify: SpotifyConfig)
}