module de.dargmuesli.spotitag {
    requires com.fasterxml.jackson.databind;
    requires com.google.gson;
    requires java.desktop;
    requires java.logging;
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;
    requires kotlinx.coroutines.core.jvm;
    requires kotlinx.coroutines.javafx;
    requires mp3agic;
    requires org.apache.logging.log4j;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.fontawesome5;
    requires org.kordamp.ikonli.javafx;
    requires se.michaelthelin.spotify;

    opens de.dargmuesli.spotitag.model.enums to com.fasterxml.jackson.databind;
    opens de.dargmuesli.spotitag.model.filesystem to com.fasterxml.jackson.databind;
    opens de.dargmuesli.spotitag.model.music to com.fasterxml.jackson.databind;
    opens de.dargmuesli.spotitag.persistence to com.fasterxml.jackson.databind;
    opens de.dargmuesli.spotitag.persistence.cache to com.fasterxml.jackson.databind;
    opens de.dargmuesli.spotitag.persistence.cache.providers to com.fasterxml.jackson.databind;
    opens de.dargmuesli.spotitag.persistence.config to com.fasterxml.jackson.databind;
    opens de.dargmuesli.spotitag.persistence.config.providers to com.fasterxml.jackson.databind;
    opens de.dargmuesli.spotitag.persistence.state to com.fasterxml.jackson.databind;
    opens de.dargmuesli.spotitag.persistence.state.providers to com.fasterxml.jackson.databind;
    opens de.dargmuesli.spotitag.ui.controller to javafx.fxml;
    opens de.dargmuesli.spotitag.util to com.fasterxml.jackson.databind;
    opens de.dargmuesli.spotitag.util.converter to com.fasterxml.jackson.databind;

    exports de.dargmuesli.spotitag;
}