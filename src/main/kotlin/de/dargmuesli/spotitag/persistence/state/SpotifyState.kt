package de.dargmuesli.spotitag.persistence.state

import de.dargmuesli.spotitag.persistence.Persistence
import de.dargmuesli.spotitag.persistence.PersistenceTypes
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.properties.Delegates

@Serializable(with = SpotifyState.Serializer::class)
object SpotifyState {
    var accessToken: String? by Delegates.observable(null) { _, _, _ ->
        Persistence.save(PersistenceTypes.STATE)
    }

    var expiresAt: Long? by Delegates.observable(null) { _, _, _ ->
        Persistence.save(PersistenceTypes.STATE)
    }

    object Serializer : KSerializer<SpotifyState> {
        override val descriptor: SerialDescriptor = SpotifyStateSurrogate.serializer().descriptor

        override fun serialize(encoder: Encoder, value: SpotifyState) {
            encoder.encodeSerializableValue(
                SpotifyStateSurrogate.serializer(),
                SpotifyStateSurrogate(accessToken, expiresAt)
            )
        }

        override fun deserialize(decoder: Decoder): SpotifyState {
            val spotifyState = decoder.decodeSerializableValue(SpotifyStateSurrogate.serializer())
            accessToken = spotifyState.accessToken
            expiresAt = spotifyState.expiresAt
            return SpotifyState
        }
    }

    @Serializable
    @SerialName("SpotifyState")
    private data class SpotifyStateSurrogate(val accessToken: String?, val expiresAt: Long?)
}
