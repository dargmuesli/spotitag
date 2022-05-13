package de.dargmuesli.spotitag.provider

interface ISpotitagProviderAuthorizable : ISpotitagProvider {
    fun isAuthorized(): Boolean
}
