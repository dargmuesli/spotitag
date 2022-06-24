package de.dargmuesli.spotitag.persistence

import de.dargmuesli.spotitag.persistence.cache.FileSystemCache
import de.dargmuesli.spotitag.persistence.state.FileSystemState
import de.dargmuesli.spotitag.persistence.state.SpotifyState
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = SpotitagState.Serializer::class)
object SpotitagState : AbstractSerializable() {
    var fileSystem: FileSystemState = FileSystemState
    var spotify: SpotifyState = SpotifyState

    object Serializer : KSerializer<SpotitagState> {
        override val descriptor: SerialDescriptor = SpotitagStateSurrogate.serializer().descriptor

        override fun serialize(encoder: Encoder, value: SpotitagState) {
            encoder.encodeSerializableValue(
                SpotitagStateSurrogate.serializer(),
                SpotitagStateSurrogate(fileSystem, spotify)
            )
        }

        override fun deserialize(decoder: Decoder): SpotitagState {
            val spotitagState = decoder.decodeSerializableValue(SpotitagStateSurrogate.serializer())
            fileSystem = spotitagState.fileSystem
            spotify = spotitagState.spotify
            return SpotitagState
        }
    }

    @Serializable
    @SerialName("SpotitagState")
    private data class SpotitagStateSurrogate(val fileSystem: FileSystemState, val spotify: SpotifyState)

    fun refresh() {
        fileSystem.filesFound.set(0)
        fileSystem.filesFoundWithSpotifyId.set(0)
        fileSystem.filesFoundWithSpotitagVersion.set(0)
        fileSystem.filesFoundWithSpotitagVersionNewest.set(0)

        for (musicFile in FileSystemCache.trackData.values) {
            FileSystemState.updateCounter(musicFile) { x -> x + 1 }
        }
    }
}
