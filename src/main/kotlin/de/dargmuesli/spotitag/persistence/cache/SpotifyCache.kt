package de.dargmuesli.spotitag.persistence.cache

import de.dargmuesli.spotitag.util.SpotifyTrackSerializer
import javafx.collections.FXCollections.observableHashMap
import javafx.collections.ObservableMap
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import se.michaelthelin.spotify.model_objects.specification.Track

@Serializable(with = SpotifyCache.Serializer::class)
object SpotifyCache : IProviderCache<Track> {
    override var trackData: ObservableMap<String, Track> = observableHashMap()

    object Serializer : KSerializer<SpotifyCache> {
        override val descriptor: SerialDescriptor = SpotifyCacheSurrogate.serializer().descriptor

        override fun serialize(encoder: Encoder, value: SpotifyCache) {
            encoder.encodeSerializableValue(
                SpotifyCacheSurrogate.serializer(),
                SpotifyCacheSurrogate(trackData.toMap())
            )
        }

        override fun deserialize(decoder: Decoder): SpotifyCache {
            val spotifyCache = decoder.decodeSerializableValue(SpotifyCacheSurrogate.serializer())
            trackData = observableHashMap<String, Track>().also {
                it.putAll(spotifyCache.trackData)
            }
            return SpotifyCache
        }
    }

    @Serializable
    @SerialName("SpotifyCache")
    private data class SpotifyCacheSurrogate(val trackData: Map<String, @Serializable(with = SpotifyTrackSerializer.Serializer::class) Track>)

    override fun clear() {
        for (entry in trackData) {
            trackData.remove(entry.key)
        }
    }
}
