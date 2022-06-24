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

@Serializable(with = SpotifyConfig.Serializer::class)
object SpotifyConfig {
    var clientId: String? by Delegates.observable(null) { _, _, _ ->
        Persistence.save(PersistenceTypes.CONFIG)
    }
    var clientSecret: String? by Delegates.observable(null) { _, _, _ ->
        Persistence.save(PersistenceTypes.CONFIG)
    }

    object Serializer : KSerializer<SpotifyConfig> {
        override val descriptor: SerialDescriptor = SpotifyConfigSurrogate.serializer().descriptor

        override fun serialize(encoder: Encoder, value: SpotifyConfig) {
            encoder.encodeSerializableValue(
                SpotifyConfigSurrogate.serializer(),
                SpotifyConfigSurrogate(clientId, clientSecret)
            )
        }

        override fun deserialize(decoder: Decoder): SpotifyConfig {
            val spotifyConfig = decoder.decodeSerializableValue(SpotifyConfigSurrogate.serializer())
            clientId = spotifyConfig.clientId
            clientSecret = spotifyConfig.clientSecret
            return SpotifyConfig
        }
    }

    @Serializable
    @SerialName("SpotifyConfig")
    private data class SpotifyConfigSurrogate(val clientId: String?, val clientSecret: String?)
}
