package de.dargmuesli.spotitag.provider

import de.dargmuesli.spotitag.model.filesystem.MusicFile
import de.dargmuesli.spotitag.persistence.config.providers.SpotifyConfig
import de.dargmuesli.spotitag.persistence.state.providers.SpotifyState
import de.dargmuesli.spotitag.ui.controller.DashboardController
import se.michaelthelin.spotify.SpotifyApi
import se.michaelthelin.spotify.requests.data.AbstractDataPagingRequest
import java.io.File


object SpotifyProvider {
    private val spotifyApiBuilder: SpotifyApi.Builder = SpotifyApi.builder()
        .setClientId(SpotifyConfig.clientId)
        .setClientSecret(SpotifyConfig.clientSecret)
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

    fun getSpotifyTrack(musicFile: MusicFile): se.michaelthelin.spotify.model_objects.specification.Track? {
        val fileName = File(musicFile.path).nameWithoutExtension
        val tagName = (musicFile.track.artists?.let { it.joinToString() + " - " } ?: "") + musicFile.track.name

        return if (fileName != tagName) {
            DashboardController.LOGGER.warn("File name (1) does not match tags (2):\n(1) $fileName\n(2) $tagName")
            null
        } else {
            val trackPaging =
                SpotifyProvider.spotifyApi.searchTracks(tagName.split(" - ").joinToString(" ")).build().execute()
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
}
