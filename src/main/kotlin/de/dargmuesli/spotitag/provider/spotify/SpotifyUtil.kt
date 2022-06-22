package de.dargmuesli.spotitag.provider.spotify

import se.michaelthelin.spotify.SpotifyApi
import se.michaelthelin.spotify.requests.data.AbstractDataPagingRequest
import de.dargmuesli.spotitag.persistence.state.SpotitagState

object SpotifyUtil {
    val spotifyApiBuilder: SpotifyApi.Builder = SpotifyApi.builder()
            .setClientId(SpotitagState.settings.spotify.clientId)
            .setClientSecret(SpotitagState.settings.spotify.clientSecret)
            .setRedirectUri(SpotitagState.settings.spotify.redirectUri)
    var spotifyApi: SpotifyApi = spotifyApiBuilder.build()

    init {
        if (SpotitagState.data.spotifyData.accessToken != "") {
            spotifyApi = spotifyApiBuilder
                    .setAccessToken(SpotitagState.data.spotifyData.accessToken)
                    .build()
        }
    }

    fun openAuthorization() {
        val authorizationCode = spotifyApi.clientCredentials()
                .build().execute()

        SpotitagState.data.spotifyData.accessToken = authorizationCode.accessToken
        SpotitagState.data.spotifyData.expiresAt = System.currentTimeMillis() / 1000 + authorizationCode.expiresIn
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
}