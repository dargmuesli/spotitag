package de.dargmuesli.spotitag.persistence.config

import de.dargmuesli.spotitag.persistence.Persistence
import de.dargmuesli.spotitag.persistence.PersistenceTypes
import javafx.beans.property.SimpleStringProperty
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = SpotifyConfig.Serializer::class)
object SpotifyConfig {
    var clientId = SimpleStringProperty().also {
        it.addListener { _ ->
            Persistence.save(PersistenceTypes.CONFIG)
        }
    }
    var clientSecret = SimpleStringProperty().also {
        it.addListener { _ ->
            Persistence.save(PersistenceTypes.CONFIG)
        }
    }

    object Serializer : KSerializer<SpotifyConfig> {
        override val descriptor: SerialDescriptor = SpotifyConfigSurrogate.serializer().descriptor

        override fun serialize(encoder: Encoder, value: SpotifyConfig) {
            encoder.encodeSerializableValue(
                SpotifyConfigSurrogate.serializer(),
                SpotifyConfigSurrogate(clientId.value, clientSecret.value)
            )
        }

        override fun deserialize(decoder: Decoder): SpotifyConfig {
            val spotifyConfig = decoder.decodeSerializableValue(SpotifyConfigSurrogate.serializer())
            clientId.set(spotifyConfig.clientId)
            clientSecret.set(spotifyConfig.clientSecret)
            return SpotifyConfig
        }
    }

    @Serializable
    @SerialName("SpotifyConfig")
    private data class SpotifyConfigSurrogate(val clientId: String?, val clientSecret: String?)
}
