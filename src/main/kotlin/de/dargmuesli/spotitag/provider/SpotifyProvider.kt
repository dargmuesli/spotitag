package de.dargmuesli.spotitag.provider

import de.dargmuesli.spotitag.model.filesystem.MusicFile
import de.dargmuesli.spotitag.model.music.Album
import de.dargmuesli.spotitag.model.music.Artist
import de.dargmuesli.spotitag.persistence.config.SpotifyConfig
import de.dargmuesli.spotitag.persistence.state.SpotifyState
import de.dargmuesli.spotitag.ui.controller.DashboardController
import se.michaelthelin.spotify.SpotifyApi
import se.michaelthelin.spotify.model_objects.specification.Track
import se.michaelthelin.spotify.requests.data.AbstractDataPagingRequest
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.URL
import java.util.*
import javax.imageio.ImageIO


object SpotifyProvider {
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
        val authorizationCode = spotifyApi.clientCredentials()
            .build().execute()

        spotifyApi.accessToken = authorizationCode.accessToken

        SpotifyState.accessToken = authorizationCode.accessToken
        SpotifyState.expiresAt = System.currentTimeMillis() / 1000 + authorizationCode.expiresIn
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

    fun getSpotifyTrack(musicFile: MusicFile): Track? {
        val fileName = File(musicFile.path).nameWithoutExtension
        val tagName = (musicFile.track.artists?.let { it.joinToString() + " - " } ?: "") + musicFile.track.name

        return if (fileName != tagName) {
            DashboardController.LOGGER.warn("File name (1) does not match tags (2):\n(1) $fileName\n(2) $tagName")
            null
        } else {
            val trackPaging =
                spotifyApi.searchTracks(tagName.split(" - ").joinToString(" ")).build().execute()
            DashboardController.LOGGER.debug("${trackPaging.total} found!")

            if (trackPaging.items.isNotEmpty()) {
                val track = trackPaging.items[0]
                val spotifyFileName =
                    track.artists?.let { it.joinToString { artistSimplified -> artistSimplified.name } + " - " } + track.name
                DashboardController.LOGGER.debug("Choosing id \"${track.id}\"")

                if (spotifyFileName != fileName) {
                    DashboardController.LOGGER.warn("Spotify file name (1) does not match file name (2):\n(1) ${spotifyFileName}\n(2) for $fileName")
                }

                track
            } else {
                null
            }
        }
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
