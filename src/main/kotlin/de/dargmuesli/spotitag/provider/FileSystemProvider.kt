package de.dargmuesli.spotitag.provider

import com.mpatric.mp3agic.EncodedText
import com.mpatric.mp3agic.ID3v23Tag
import com.mpatric.mp3agic.Mp3File
import de.dargmuesli.spotitag.model.filesystem.MusicFile
import de.dargmuesli.spotitag.model.music.Album
import de.dargmuesli.spotitag.model.music.Artist
import de.dargmuesli.spotitag.model.music.Track
import de.dargmuesli.spotitag.ui.controller.DashboardController
import de.dargmuesli.spotitag.util.ID3v2TXXXFrameData
import java.io.File
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
}