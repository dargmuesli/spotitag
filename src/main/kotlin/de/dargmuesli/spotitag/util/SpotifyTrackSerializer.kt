package de.dargmuesli.spotitag.util

import com.google.gson.Gson
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import se.michaelthelin.spotify.model_objects.specification.Track

class SpotifyTrackSerializer {
    object Serializer : KSerializer<Track> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Track", PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, value: Track) {
            encoder.encodeString(Gson().toJson(value))
        }

        override fun deserialize(decoder: Decoder): Track {
            return Track.JsonUtil().createModelObject(decoder.decodeString())
        }
    }
}