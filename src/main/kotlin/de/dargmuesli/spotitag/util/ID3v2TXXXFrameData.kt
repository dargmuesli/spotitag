package de.dargmuesli.spotitag.util

import com.mpatric.mp3agic.*

/**
 * Source: https://github.com/Code0987/mp3agic
 */
open class ID3v2TXXXFrameData : AbstractID3v2FrameData {
    var description: EncodedText? = null
    var value: EncodedText? = null

    constructor(unsynchronisation: Boolean, description: EncodedText, value: EncodedText) : super(unsynchronisation) {
        this.description = description
        this.value = value
    }

    @Throws(InvalidDataException::class)
    constructor(unsynchronisation: Boolean, bytes: ByteArray?) : super(unsynchronisation) {
        synchroniseAndUnpackFrameData(bytes)
    }

    @Throws(InvalidDataException::class)
    override fun unpackFrameData(bytes: ByteArray) {
        var marker = BufferTools.indexOfTerminatorForEncoding(bytes, 1, bytes[0].toInt())
        if (marker >= 0) {
            description = EncodedText(bytes[0], BufferTools.copyBuffer(bytes, 1, marker - 1))
            marker += description!!.terminator.size
        } else {
            description = EncodedText(bytes[0], "")
            marker = 1
        }
        value = if (bytes.size - marker >= 0) {
            EncodedText(bytes[0], BufferTools.copyBuffer(bytes, marker, bytes.size - marker))
        } else {
            EncodedText(bytes[0], "")
        }
    }

    override fun packFrameData(): ByteArray? {
        val bytes = ByteArray(length)
        if (value != null) {
            bytes[0] = value!!.textEncoding
        } else {
            bytes[0] = 0
        }
        var marker = 1
        if (description != null) {
            val descriptionBytes = description!!.toBytes(true, true)
            BufferTools.copyIntoByteBuffer(descriptionBytes, 0, descriptionBytes.size, bytes, marker)
            marker += descriptionBytes.size
        } else {
            bytes[marker++] = 0
        }
        if (value != null) {
            val commentBytes = value!!.toBytes(true, false)
            BufferTools.copyIntoByteBuffer(commentBytes, 0, commentBytes.size, bytes, marker)
        }
        return bytes
    }

    override fun getLength(): Int {
        var length = 1
        if (description != null) {
            length += description!!.toBytes(true, true).size
        } else {
            length++
        }
        if (value != null) {
            length += value!!.toBytes(true, false).size
        }
        return length
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = super.hashCode()
        result = prime * result + if (value == null) 0 else value.hashCode()
        result = prime * result + if (description == null) 0 else description.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (!super.equals(other)) return false
        if (javaClass != other?.javaClass) return false
        val otherFrameData = other as ID3v2TXXXFrameData
        if (description == null) {
            if (otherFrameData.description != null) return false
        } else if (description != otherFrameData.description) return false
        if (value == null) {
            if (otherFrameData.value != null) return false
        } else if (value != otherFrameData.value) return false
        return true
    }

    companion object {
        private const val ID_FIELD = "TXXX"

        private fun extractAllFrames(
            frameSets: Map<String?, ID3v2FrameSet?>,
            useFrameUnsynchronisation: Boolean,
            description: String?
        ): Map<ID3v2Frame, ID3v2TXXXFrameData> {
            val frameSet = frameSets[ID_FIELD]
            val frames = HashMap<ID3v2Frame, ID3v2TXXXFrameData>()
            if (frameSet == null) {
                return frames
            }
            for (frame in frameSet.frames) {
                try {
                    val field = ID3v2TXXXFrameData(
                        useFrameUnsynchronisation,
                        frame.data
                    )
                    if (description == null || field.description.toString().contains(description)) {
                        frames[frame] = field
                    }
                } catch (e: InvalidDataException) {
                    e.printStackTrace()
                }
            }
            return frames
        }

        private fun extractAll(
            frameSets: Map<String?, ID3v2FrameSet?>,
            useFrameUnsynchronisation: Boolean,
            description: String?
        ): ArrayList<ID3v2TXXXFrameData> {
            val frames = extractAllFrames(
                frameSets,
                useFrameUnsynchronisation,
                description
            )
            val fields = ArrayList<ID3v2TXXXFrameData>()
            for ((_, value1) in frames) {
                fields.add(value1)
            }
            return fields
        }

        fun extract(
            frameSets: Map<String?, ID3v2FrameSet?>,
            useFrameUnsynchronisation: Boolean,
            description: String?
        ): ID3v2TXXXFrameData? {
            val items = extractAll(
                frameSets,
                useFrameUnsynchronisation,
                description
            )
            return if (items.size > 0) {
                items[0]
            } else null
        }

        fun createOrAddField(
            frameSets: MutableMap<String?, ID3v2FrameSet?>,
            useFrameUnsynchronisation: Boolean,
            description: String?,
            value: String?,
            useDescriptionToMatch: Boolean
        ) {
            val field = ID3v2TXXXFrameData(
                useFrameUnsynchronisation,
                EncodedText(description),
                EncodedText(value)
            )
            val frame = ID3v2Frame(
                ID_FIELD,
                field.toBytes()
            )
            var frameSet = frameSets[frame.id]
            if (frameSet == null) {
                frameSet = ID3v2FrameSet(frame.id)
                frameSet.addFrame(frame)
                frameSets[frame.id] = frameSet
            } else {
                if (useDescriptionToMatch) {
                    val frames = extractAllFrames(
                        frameSets,
                        useFrameUnsynchronisation,
                        description
                    )
                    if (frames.isNotEmpty()) {
                        frameSet.frames.removeAll(frames.keys)
                    }
                }
                frameSet.addFrame(frame)
            }
        }
    }
}