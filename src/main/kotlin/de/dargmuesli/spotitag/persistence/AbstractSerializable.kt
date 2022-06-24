package de.dargmuesli.spotitag.persistence

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable

@Serializable
sealed class AbstractSerializable {
    abstract fun serializer(): DeserializationStrategy<AbstractSerializable>
}
