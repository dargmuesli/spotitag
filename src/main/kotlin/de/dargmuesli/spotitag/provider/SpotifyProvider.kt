package de.dargmuesli.spotitag.provider

import com.neovisionaries.i18n.CountryCode
import de.dargmuesli.spotitag.model.enums.Id3Properties
import de.dargmuesli.spotitag.model.filesystem.MusicFile
import de.dargmuesli.spotitag.model.music.Album
import de.dargmuesli.spotitag.model.music.Artist
import de.dargmuesli.spotitag.persistence.config.SpotifyConfig
import de.dargmuesli.spotitag.persistence.state.SpotifyState
import de.dargmuesli.spotitag.provider.FileSystemProvider.getFileNameFromTrack
import de.dargmuesli.spotitag.ui.SpotitagNotification
import org.apache.commons.text.similarity.JaroWinklerDistance
import org.apache.logging.log4j.LogManager
import se.michaelthelin.spotify.SpotifyApi
import se.michaelthelin.spotify.exceptions.detailed.NotFoundException
import se.michaelthelin.spotify.model_objects.specification.Track
import se.michaelthelin.spotify.requests.data.AbstractDataPagingRequest
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.URL
import java.util.*
import javax.imageio.ImageIO


object SpotifyProvider {
    private val LOGGER = LogManager.getLogger()
    private val DISTANCE = JaroWinklerDistance()
    private val spotifyApiBuilder: SpotifyApi.Builder = SpotifyApi.builder()
        .setClientId(SpotifyConfig.clientId.value)
        .setClientSecret(SpotifyConfig.clientSecret.value)
    var spotifyApi: SpotifyApi = spotifyApiBuilder.build()

    init {
        if (SpotifyState.accessToken != null) {
            spotifyApi = spotifyApiBuilder
                .setAccessToken(SpotifyState.accessToken)
                .build()
        }
    }

    fun authorize() {
        try {
            val authorizationCode = spotifyApi.clientCredentials()
                .build().execute()

            spotifyApi.accessToken = authorizationCode.accessToken

            SpotifyState.accessToken = authorizationCode.accessToken
            SpotifyState.expiresAt = System.currentTimeMillis() / 1000 + authorizationCode.expiresIn
        } catch (_: Exception) {
            SpotitagNotification.error("Could not authenticate against Spotify! Check your credentials.")
        }
    }

    fun <T> getAllPagingItems(requestBuilder: AbstractDataPagingRequest.Builder<T, *>): List<T> {
        val list = arrayListOf<T>()

        do {
            val paging = requestBuilder.build().execute()
            list.addAll(paging.items)
            requestBuilder
                .offset(paging.offset + paging.limit)
        } while (paging.next != null)

        return list
    }

    fun isAuthorized(): Boolean {
        if (SpotifyState.expiresAt == null || (SpotifyState.expiresAt!! < System.currentTimeMillis() / 1000)) {
            return false
        }

        return true
    }

    fun getSpotifyTrack(
        musicFile: MusicFile,
        vararg id3Properties: Id3Properties = arrayOf(
            Id3Properties.TITLE,
            Id3Properties.ARTISTS,
            Id3Properties.ALBUM
        )
    ): Track? {
        LOGGER.debug("Getting spotify track by properties: ${id3Properties.joinToString()}")

        val fileName = File(musicFile.path).nameWithoutExtension
        val tagName = getFileNameFromTrack(musicFile.track)

        if (fileName != tagName) {
            LOGGER.warn("File name (1) does not match tags (2):\n(1) $fileName\n(2) $tagName")
        }

        val queryList = mutableListOf<String>()

        if (id3Properties.contains(Id3Properties.TITLE)) {
            musicFile.track.name?.let {
                queryList.add("track:\"" + it.split(" ").joinToString("\" OR \"") + "\"")
            }
        }
        if (id3Properties.contains(Id3Properties.ARTISTS)) {
            musicFile.track.artists?.let {
                queryList.add("artist:\"" + it.joinToString("\" OR \"") + "\"")
            }
        }
        if (id3Properties.contains(Id3Properties.ALBUM)) {
            musicFile.track.album?.name?.let {
                queryList.add("album:\"" + it.split(" ").joinToString("\" OR \"") + "\"")
            }
        }
        val query = queryList.joinToString(" ")
        LOGGER.debug("Query: '${query}'")

        try {
            val trackPaging =
                spotifyApi.searchTracks(query).market(CountryCode.SE).build().execute()
            LOGGER.debug("${trackPaging.total} found!")

            if (trackPaging.items.isNotEmpty()) {
                val distanceMap = mutableMapOf<Track, Double>()

                for (item in trackPaging.items) {
                    val nameDistance = musicFile.track.name?.let { DISTANCE.apply(it, item.name) } ?: 0.0
                    val artistsDistance = musicFile.track.artists?.let { artists ->
                        DISTANCE.apply(
                            artists.joinToString(),
                            item.artists.joinToString { it.name })
                    } ?: 0.0
                    val albumDistance = musicFile.track.album?.name?.let { DISTANCE.apply(it, item.album.name) } ?: 0.0
                    distanceMap[item] = nameDistance + artistsDistance + albumDistance
                }

                val bestTrack: Track = distanceMap.maxByOrNull { it.value }?.key ?: trackPaging.items[0]
                LOGGER.debug("Best distance: ${distanceMap[bestTrack]}")
                val spotifyFileName =
                    bestTrack.artists?.let { it.joinToString { artistSimplified -> artistSimplified.name } + " - " } + bestTrack.name
                LOGGER.debug("Choosing id \"${bestTrack.id}\"")

                if (spotifyFileName != fileName) {
                    LOGGER.warn("Spotify file name (1) does not match file name (2):\n(1) ${spotifyFileName}\n(2) $fileName")
                }

                return bestTrack
            } else if (id3Properties.size > 1) {
                return getSpotifyTrack(musicFile, *id3Properties.sliceArray(0..(id3Properties.size - 2)))
            }
        } catch (_: NotFoundException) {
            if (id3Properties.size > 1) {
                return getSpotifyTrack(musicFile, *id3Properties.sliceArray(0..(id3Properties.size - 2)))
            }
        }

        return null
    }

    fun getTrackFromSpotifyTrack(track: Track): de.dargmuesli.spotitag.model.music.Track {
        return de.dargmuesli.spotitag.model.music.Track(
            album = Album(
                artists = track.album.artists?.let { track.album.artists.map { (Artist(name = it.name)) } },
                coverBase64 = track.album.images?.let {
                    val byteArrayOutputStream = ByteArrayOutputStream()
                    val url = URL(it[0].url)
                    ImageIO.write(ImageIO.read(url), "jpg", byteArrayOutputStream)
                    val output = Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray())
                    byteArrayOutputStream.close()
                    output
                },
                name = track.album.name
            ),
            artists = track.artists?.let { track.artists.map { Artist(name = it.name) } },
            durationMs = track.durationMs?.toLong(),
            id = track.id,
            name = track.name
        )
    }
}
