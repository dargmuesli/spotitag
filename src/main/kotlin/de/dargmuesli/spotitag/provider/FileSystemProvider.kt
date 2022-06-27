package de.dargmuesli.spotitag.provider

import com.mpatric.mp3agic.EncodedText
import com.mpatric.mp3agic.ID3v23Tag
import com.mpatric.mp3agic.Mp3File
import de.dargmuesli.spotitag.model.enums.Id3Properties
import de.dargmuesli.spotitag.model.filesystem.MusicFile
import de.dargmuesli.spotitag.model.music.Album
import de.dargmuesli.spotitag.model.music.Artist
import de.dargmuesli.spotitag.model.music.Track
import de.dargmuesli.spotitag.persistence.Persistence
import de.dargmuesli.spotitag.persistence.cache.FileSystemCache
import de.dargmuesli.spotitag.persistence.state.FileSystemState
import de.dargmuesli.spotitag.persistence.state.SpotifyState
import de.dargmuesli.spotitag.ui.controller.DashboardController
import de.dargmuesli.spotitag.util.ID3v2TXXXFrameData
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.*

object FileSystemProvider {
    fun getMusicFile(file: File): MusicFile {
        val mp3File = Mp3File(file)

        if (!mp3File.hasId3v1Tag()) {
            DashboardController.LOGGER.warn("File \"${mp3File.filename}\" does not have Id3v1 Tag!")
        }

        if (!mp3File.hasId3v2Tag()) {
            mp3File.id3v2Tag = ID3v23Tag()
        }

        val id3v2Tag = mp3File.id3v2Tag

        if (id3v2Tag.version != "4.0") {
            DashboardController.LOGGER.warn("File \"${mp3File.filename}\" does not have the preferred Id3v2 version! (${id3v2Tag.version} instead of 4.0)")
        }

        val version: EncodedText? = ID3v2TXXXFrameData.extract(
            id3v2Tag.frameSets,
            DashboardController.IS_SYNCHRONIZED,
            "Version"
        )?.value

        return MusicFile(
            file.absolutePath,
            Track(
                album = Album(
                    artists = id3v2Tag.albumArtist?.let { listOf(Artist(name = id3v2Tag.albumArtist)) },
                    coverBase64 = Base64.getEncoder().encodeToString(id3v2Tag.albumImage),
                    name = id3v2Tag.album
                ),
                artists = id3v2Tag.artist?.let { listOf(Artist(name = id3v2Tag.artist)) },
                durationMs = mp3File.lengthInMilliseconds,
                id = id3v2Tag.audioSourceUrl,
                name = id3v2Tag.title
            ),
            version?.toString()
        )
    }

    fun writeMusicFile(vararg properties: Id3Properties) {
        if (properties.isEmpty()) {
            writeMusicFile(
                *Id3Properties.values()
            )
        } else {
            val file = FileSystemState.currentFile
            val track = SpotifyState.currentTrack

            val mp3File = Mp3File(file)

            val id3v2Tag = mp3File.id3v2Tag

            if (properties.contains(Id3Properties.TITLE)) {
                track?.name?.let {
                    id3v2Tag.title = it
                }
            }

            if (properties.contains(Id3Properties.ARTISTS)) {
                track?.artists?.joinToString()?.let {
                    id3v2Tag.artist = it
                }
            }

            if (properties.contains(Id3Properties.ALBUM)) {
                track?.album?.name?.let {
                    id3v2Tag.album = it
                }
            }

            if (properties.contains(Id3Properties.ID)) {
                track?.id?.let {
                    id3v2Tag.audioSourceUrl = it
                }
            }

            if (properties.contains(Id3Properties.COVER)) {
                track?.album?.coverBase64?.let {
                    id3v2Tag.setAlbumImage(Base64.getDecoder().decode(it), "jpg")
                }
            }

            ID3v2TXXXFrameData.createOrAddField(
                id3v2Tag.frameSets,
                DashboardController.IS_SYNCHRONIZED,
                "Version",
                Persistence.getVersion(),
                true
            )

            val tempFileName = Paths.get(Persistence.tmpDirectory.toString(), file?.name)
            Files.createDirectories(tempFileName.parent)
            mp3File.save(tempFileName.toString())
            file?.absolutePath?.let {
                Files.move(tempFileName, Paths.get(it), StandardCopyOption.REPLACE_EXISTING)
                FileSystemCache.trackData.remove(it)
            }
        }
    }
}