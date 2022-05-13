package de.dargmuesli.spotitag.persistence.state.data.providers

interface IProviderData<PT, TT> {
    var playlistData: MutableMap<String, PT>
    var playlistItemData: MutableMap<String, TT>
}