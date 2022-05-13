package de.dargmuesli.spotitag.util

class Etter<T>(var getter: () -> T, var setter: (T) -> Unit)
