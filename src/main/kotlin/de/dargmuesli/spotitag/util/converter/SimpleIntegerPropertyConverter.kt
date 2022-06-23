package de.dargmuesli.spotitag.util.converter

import com.fasterxml.jackson.databind.util.StdConverter
import javafx.beans.property.SimpleIntegerProperty


class SimpleIntegerPropertyConverter : StdConverter<SimpleIntegerProperty, Int?>() {
    override fun convert(value: SimpleIntegerProperty?): Int? {
        return value?.value
    }
}