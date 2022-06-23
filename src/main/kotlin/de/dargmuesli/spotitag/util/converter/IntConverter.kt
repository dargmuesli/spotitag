package de.dargmuesli.spotitag.util.converter

import com.fasterxml.jackson.databind.util.StdConverter
import javafx.beans.property.SimpleIntegerProperty


class IntConverter : StdConverter<Int, SimpleIntegerProperty?>() {
    override fun convert(value: Int?): SimpleIntegerProperty? {
        return value?.let { SimpleIntegerProperty(value) }
    }
}