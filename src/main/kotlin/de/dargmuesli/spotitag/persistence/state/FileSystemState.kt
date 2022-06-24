package de.dargmuesli.spotitag.persistence.state

import de.dargmuesli.spotitag.model.filesystem.MusicFile
import de.dargmuesli.spotitag.persistence.Persistence
import de.dargmuesli.spotitag.persistence.PersistenceTypes
import javafx.beans.property.SimpleIntegerProperty
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = FileSystemState.Serializer::class)
object FileSystemState {
    var filesFound = SimpleIntegerProperty(0)
    var filesFoundWithSpotifyId = SimpleIntegerProperty(0)
    var filesFoundWithSpotitagVersion = SimpleIntegerProperty(0)
    var filesFoundWithSpotitagVersionNewest = SimpleIntegerProperty(0)

    object Serializer : KSerializer<FileSystemState> {
        override val descriptor: SerialDescriptor = FileSystemStateSurrogate.serializer().descriptor

        override fun serialize(encoder: Encoder, value: FileSystemState) {
            encoder.encodeSerializableValue(
                FileSystemStateSurrogate.serializer(),
                FileSystemStateSurrogate(
                    filesFound.value,
                    filesFoundWithSpotifyId.value,
                    filesFoundWithSpotitagVersion.value,
                    filesFoundWithSpotitagVersionNewest.value
                )
            )
        }

        override fun deserialize(decoder: Decoder): FileSystemState {
            val spotitagState = decoder.decodeSerializableValue(FileSystemStateSurrogate.serializer())
            filesFound = SimpleIntegerProperty(spotitagState.filesFound)
            filesFoundWithSpotifyId = SimpleIntegerProperty(spotitagState.filesFoundWithSpotifyId)
            filesFoundWithSpotitagVersion = SimpleIntegerProperty(spotitagState.filesFoundWithSpotitagVersion)
            filesFoundWithSpotitagVersionNewest =
                SimpleIntegerProperty(spotitagState.filesFoundWithSpotitagVersionNewest)
            return FileSystemState
        }
    }

    @Serializable
    @SerialName("FileSystemState")
    private data class FileSystemStateSurrogate(
        val filesFound: Int,
        val filesFoundWithSpotifyId: Int,
        val filesFoundWithSpotitagVersion: Int,
        val filesFoundWithSpotitagVersionNewest: Int
    )

    internal fun updateCounter(musicFile: MusicFile, calc: (Int) -> Int) {
        filesFound.set(calc(filesFound.value))

        if (musicFile.spotitagVersion != null) {
            filesFoundWithSpotitagVersion.set(calc(filesFoundWithSpotitagVersion.value))

            if (musicFile.spotitagVersion.toString() == Persistence.getVersion()) {
                filesFoundWithSpotitagVersionNewest.set(calc(filesFoundWithSpotitagVersionNewest.value))
            }
        }

        if (musicFile.track.id !== null) {
            filesFoundWithSpotifyId.set(calc(filesFoundWithSpotifyId.value))
        }

        Persistence.save(PersistenceTypes.STATE)
    }
}
