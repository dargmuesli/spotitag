package de.dargmuesli.spotitag.provider.spotify

import se.michaelthelin.spotify.model_objects.specification.PlaylistTrack
import de.dargmuesli.spotitag.model.enums.AlbumType
import de.dargmuesli.spotitag.model.music.Album
import de.dargmuesli.spotitag.model.music.Artist
import de.dargmuesli.spotitag.model.music.Track
import de.dargmuesli.spotitag.persistence.state.SpotitagState
import de.dargmuesli.spotitag.provider.ISpotitagProviderAuthorizable


object SpotifyProvider : ISpotitagProviderAuthorizable {

//    override fun getPlaylist(playlistId: String): Playlist {
//        val spotifyPlaylistName = SpotifyUtil.spotifyApi.getPlaylist(playlistId).build().execute().name
//        val spotifyPlaylistTracks = SpotifyUtil.getAllPagingItems(
//            SpotifyUtil.spotifyApi.getPlaylistsItems(playlistId)
//        )
//        val playlistTracks = arrayListOf<Track>()
//
//        spotifyPlaylistTracks.forEach { spotifyPlaylistTrack: PlaylistTrack ->
//            val track = spotifyPlaylistTrack.track as se.michaelthelin.spotify.model_objects.specification.Track
//
//            if (track.linkedFrom != null && track.id !== track.linkedFrom.id) {
//                println(track.name + "might differ! " + track.linkedFrom.id)
//            }
//
//            val trackAlbumType = AlbumType.valueOf(track.album.albumType.name)
//            val trackAlbumArtists: MutableList<Artist> = mutableListOf()
//            val trackArtists: MutableList<Artist> = mutableListOf()
//
//            track.album.artists.forEach { artistSimplified ->
//                trackAlbumArtists.add(Artist(name = artistSimplified.name))
//            }
//
//            track.artists.forEach { artistSimplified ->
//                trackArtists.add(Artist(name = artistSimplified.name))
//            }
//
//            val trackAlbumName: String = track.album.name
//            val trackAlbum = Album(albumType = trackAlbumType, artists = trackAlbumArtists, name = trackAlbumName)
//            val trackDurationMs = spotifyPlaylistTrack.track.durationMs
//            val trackName = spotifyPlaylistTrack.track.name
//
//            playlistTracks.add(Track(trackAlbum, trackArtists, trackDurationMs, trackName))
//        }
//
//        return Playlist(spotifyPlaylistName, playlistTracks)
//    }

//    override fun isPlaylistIdValid(playlistId: String): Boolean {
//        val errorMessage = "Playlist validation failed!"
//
//        if (playlistId == "" || playlistId.length != 22) {
//            return false
//        }
//
//        return true
//
////        return try {
////            SpotifyUtil.spotifyApi.getPlaylist(playlistId).build().execute()
////            true
////        } catch (e: IOException) {
////            LogManager.getLogger().error(errorMessage, e)
////            false
////        } catch (e: UnauthorizedException) {
////            throw e
////        } catch (e: SpotifyWebApiException) {
////            if (e !is NotFoundException) {
////                LogManager.getLogger().error("$errorMessage SpotifyWebApiException is not a NotFoundException.", e)
////            }
////
////            false
////        }
//    }

    override fun isAuthorized(): Boolean {
        if (SpotitagState.data.spotifyData.expiresAt == null || (SpotitagState.data.spotifyData.expiresAt!! < System.currentTimeMillis() / 1000)) {
            return false
        }

        return true
    }
}
