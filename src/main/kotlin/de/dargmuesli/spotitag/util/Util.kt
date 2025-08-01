package de.dargmuesli.spotitag.util

import javafx.scene.control.TextFormatter
import java.text.CharacterIterator
import java.text.StringCharacterIterator
import java.util.function.UnaryOperator

object Util {
    // https://stackoverflow.com/a/3758880/4682621
    fun humanReadableByteCountBin(bytes: Long): String? {
        val absB = if (bytes == Long.MIN_VALUE) Long.MAX_VALUE else Math.abs(bytes)
        if (absB < 1024) {
            return "$bytes B"
        }
        var value = absB
        val ci: CharacterIterator = StringCharacterIterator("KMGTPE")
        var i = 40
        while (i >= 0 && absB > 0xfffccccccccccccL shr i) {
            value = value shr 10
            ci.next()
            i -= 10
        }
        value *= java.lang.Long.signum(bytes).toLong()
        return java.lang.String.format("%.1f %ciB", value / 1024.0, ci.current())
    }

    val integerFilter: UnaryOperator<TextFormatter.Change?> = UnaryOperator<TextFormatter.Change?> { change ->
        if (change?.controlNewText?.matches(Regex("\\d*")) == true) {
            return@UnaryOperator change
        }
        return@UnaryOperator null
    }
}